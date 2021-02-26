/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.ote.ci.test_server.internal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.osee.ote.OTETestEnvironmentClient;
import org.eclipse.osee.ote.core.environment.status.CommandAdded;
import org.eclipse.osee.ote.core.environment.status.CommandRemoved;
import org.eclipse.osee.ote.core.environment.status.EnvironmentError;
import org.eclipse.osee.ote.core.environment.status.IServiceStatusDataVisitor;
import org.eclipse.osee.ote.core.environment.status.SequentialCommandBegan;
import org.eclipse.osee.ote.core.environment.status.SequentialCommandEnded;
import org.eclipse.osee.ote.core.environment.status.TestComplete;
import org.eclipse.osee.ote.core.environment.status.TestPointUpdate;
import org.eclipse.osee.ote.core.environment.status.TestServerCommandComplete;
import org.eclipse.osee.ote.core.environment.status.TestStart;

/**
 * @author Andy Jury
 */
public class MyServiceStatusDataVisitor implements IServiceStatusDataVisitor {

   private OTETestEnvironmentClient oteClient;
   private ReentrantLock lock;
   private Condition condition;
   private boolean commandComplete = false;
   private String testName;

   public MyServiceStatusDataVisitor(OTETestEnvironmentClient oteClient, URI uri, ReentrantLock lock, Condition condition) {
      this.oteClient = oteClient;
      this.lock = lock;
      this.condition = condition;
   }

   public void reset() {
      commandComplete = false;
      testName = null;
   }

   @Override
   public void asCommandAdded(CommandAdded commandAdded) {
      // Intentionally empty
   }

   @Override
   public void asCommandRemoved(CommandRemoved commandRemoved) {
      commandComplete = true;
      lock.lock();
      try {
         condition.signalAll();
      } finally {
         lock.unlock();
      }
   }

   @Override
   public void asEnvironmentError(EnvironmentError environmentError) {
      environmentError.getErr().printStackTrace();
   }

   @Override
   public void asSequentialCommandBegan(SequentialCommandBegan sequentialCommandBegan) {
      // Intentionally empty
   }

   @Override
   public void asSequentialCommandEnded(SequentialCommandEnded sequentialCommandEnded) {
      commandComplete = true;
      lock.lock();
      try {
         condition.signalAll();
      } finally {
         lock.unlock();
      }
   }

   public boolean isCommandComplete() {
      return commandComplete;
   }

   @Override
   public void asTestPointUpdate(TestPointUpdate testPointUpdate) {
      System.out.printf("%s - PASS[%d] FAIL[%d]\n", testPointUpdate.getClassName(), testPointUpdate.getPass(),
         testPointUpdate.getFail());
   }

   @Override
   public void asTestServerCommandComplete(TestServerCommandComplete end) {
      commandComplete = true;
      lock.lock();
      try {
         condition.signalAll();
      } finally {
         lock.unlock();
      }
   }

   @Override
   public void asTestComplete(TestComplete testComplete) {
      System.out.println("Test Complete: " + testComplete.getClassName());
      try {
         System.out.println("serverOutfile: " + testComplete.getServerOutfilePath());
         File localFile = new File(testComplete.getClientOutfilePath());
         File serverFile = new File(testComplete.getServerOutfilePath());
         if (!oteClient.getServerFile(localFile, serverFile)) {
            System.out.println("failed to get tmo - " + serverFile.getAbsolutePath());
         }

         // Assume the tmz file is in the location and grab it.
         String serverName = serverFile.getName().replaceAll("\\.tmo$", ".tmz");
         String clientName = localFile.getName().replaceAll("\\.tmo$", ".tmz");

         localFile = new File(localFile.getParentFile(), clientName);
         serverFile = new File(serverFile.getParentFile(), serverName);
         if (!oteClient.getServerFile(localFile, serverFile)) {
            System.out.println("failed to get tmz - " + serverFile.getAbsolutePath());
         }
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ExecutionException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void asTestStart(TestStart testStart) {
      testName = testStart.getTestClassName();
      System.out.println("Starting: " + testStart.getTestClassName());
   }

   public String getTestName() {
      return testName;
   }
}
