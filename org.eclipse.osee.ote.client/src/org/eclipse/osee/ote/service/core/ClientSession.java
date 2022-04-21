/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ote.service.core;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.core.AbstractRemoteSession;
import org.eclipse.osee.ote.core.ConnectionRequestResult;
import org.eclipse.osee.ote.core.IRemoteUserSession;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.framework.prompt.IPassFailPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IResumeResponse;
import org.eclipse.osee.ote.core.framework.prompt.IUserInputPromptResponse;
import org.eclipse.osee.ote.core.framework.prompt.IYesNoPromptResponse;
import org.eclipse.osee.ote.service.Activator;
import org.eclipse.osee.ote.service.SessionDelegate;

/**
 * @author Ken J. Aguilar
 */
public class ClientSession extends AbstractRemoteSession {
   private static final int TIMEOUT = 1; // 1 minute timeout
   private static final String LOCK_ERROR_MSG = "could not acquire lock";
   private final InetAddress address;
   private SessionDelegate sessionDelegate = null;
   private final ReentrantLock lock = new ReentrantLock();
   //   private final OteClientEndpointReceive receive;
   private final UUID id;

   public ClientSession(OSEEPerson1_4 user, InetAddress address) {
      super(user);
      this.address = address;
      //      this.receive = receive;
      this.id = UUID.randomUUID();
      Activator.log(Level.INFO,
         String.format("Created OTE session for %s. Address=%s\n ", user.getName(), address.toString()));
   }

   @Override
   public String getAddress() {
      return address.getHostAddress();
   }

   @Override
   public byte[] getFile(String workspacePath) throws RemoteException {
      if (sessionDelegate != null) {
         try {
            return sessionDelegate.getFile(workspacePath);
         } catch (Exception ex) {
            throw new RemoteException("failed to get the file " + workspacePath, ex);
         }
      }
      throw new IllegalStateException("session delegate not set");
   }

   @Override
   public long getFileDate(String workspacePath) throws RemoteException {
      if (sessionDelegate != null) {
         try {
            return sessionDelegate.getFileDate(workspacePath);
         } catch (Exception ex) {
            throw new RemoteException("failed to get the file date" + workspacePath, ex);
         }
      }
      throw new IllegalStateException("session delegate not set");
   }

   @Override
   public boolean isAlive() throws RemoteException {
      try {
         if (lock.tryLock(TIMEOUT, TimeUnit.MINUTES)) {
            try {
               return true;
            } finally {
               lock.unlock();
            }
         }
         return false;
      } catch (InterruptedException ex) {
         throw new RemoteException(LOCK_ERROR_MSG, ex);
      }
   }

   @Override
   public void initiateInformationalPrompt(String message) {
      assert sessionDelegate != null : "delegate is null";
      try {
         sessionDelegate.handleInformationPrompt(message);
      } catch (Exception ex) {
         System.out.println(message);
      }
   }

   @Override
   public void initiatePassFailPrompt(IPassFailPromptResponse prompt) throws RemoteException {
      assert sessionDelegate != null : "delegate is null";
      try {
         sessionDelegate.handlePassFail(prompt);
      } catch (Exception ex) {
         throw new RemoteException("exception initiating prompt", ex);
      }
   }

   @Override
   public void initiateResumePrompt(IResumeResponse prompt) throws RemoteException {
      assert sessionDelegate != null : "delegate is null";
      try {
         sessionDelegate.handlePause(prompt);
      } catch (Exception ex) {
         throw new RemoteException("exception initiating prompt", ex);
      }
   }

   @Override
   public void cancelPrompts() throws RemoteException {
      assert sessionDelegate != null : "delegate is null";
      try {
         sessionDelegate.cancelPrompts();
      } catch (Exception ex) {
         throw new RemoteException("exception canceling prompt", ex);
      }
   }

   @Override
   public void initiateUserInputPrompt(IUserInputPromptResponse prompt) throws RemoteException {
      assert sessionDelegate != null : "delegate is null";
      try {
         sessionDelegate.handleUserInput(prompt);
      } catch (Exception ex) {
         throw new RemoteException("exception initiating prompt", ex);
      }
   }

   /**
    * this must be called prior to establishing a test host connection
    */
   synchronized void setSessionDelegate(SessionDelegate sessionDelegate) {
      // intentionally package-private
      this.sessionDelegate = sessionDelegate;
   }

   /**
    * closes this session
    */
   void close() {
      // INTENTIONALLY EMPTY BLOCK
   }

   TestHostConnection connect(IServiceConnector connector, IHostTestEnvironment testHost, TestEnvironmentConfig config) throws Exception {
      // intentionally package-private
      if (lock.tryLock(TIMEOUT, TimeUnit.MINUTES)) {
         try {
            IRemoteUserSession exportedSession = (IRemoteUserSession) connector.export(this);
            UUID id = UUID.randomUUID();
            Thread.currentThread().setContextClassLoader(ExportClassLoader.getInstance());
            ConnectionRequestResult result = testHost.requestEnvironment(exportedSession, id, config);
            if (result != null && result.getStatus().getStatus()) {
               connector.setConnected(true);
               return new TestHostConnection(connector, testHost, result.getEnvironment(), result.getSessionKey(),
                  false);
            } else if (result != null && result.getStatus().isUnauthorizedUser()) {
               return new TestHostConnection(null, testHost, null, null, true);
            } else {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error Connecting to the OTE Test Server.",
                  new Exception(
                     result != null ? result.getStatus().getMessage() : "ConnectionRequestResult result is null)"));
            }
            return null;
         } finally {
            lock.unlock();
         }
      }
      throw new IllegalStateException(LOCK_ERROR_MSG);

   }

   void disconnect(TestHostConnection connection) throws InterruptedException, RemoteException {
      // intentionally package-private
      if (lock.tryLock(TIMEOUT, TimeUnit.MINUTES)) {
         try {
            connection.getServiceConnector().setConnected(false);
            connection.endConnection();
            return;
         } finally {
            lock.unlock();
         }
      }
      throw new IllegalStateException(LOCK_ERROR_MSG);
   }

   @Override
   public void initiateYesNoPrompt(IYesNoPromptResponse prompt) throws Exception {
      assert sessionDelegate != null : "delegate is null";
      try {
         sessionDelegate.handleYesNo(prompt);
      } catch (Exception ex) {
         throw new RemoteException("exception initiating prompt", ex);
      }
   }

   @Override
   public UUID getUserId() throws Exception {
      return id;
   }
}
