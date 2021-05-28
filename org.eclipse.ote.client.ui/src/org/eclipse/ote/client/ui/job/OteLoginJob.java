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

package org.eclipse.ote.client.ui.job;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IWorkbenchUser;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.properties.OteProperties;
import org.eclipse.ote.client.Activator;
import org.eclipse.ote.client.ui.core.OteSessionDelegateViewImpl;
import org.eclipse.ote.client.ui.internal.OteClientUiPlugin;

/**
 * @author Ken J. Aguilar
 */
public class OteLoginJob extends Job {

   private OSEEPerson1_4 user;
   private final InetAddress address;

   public OteLoginJob() throws UnknownHostException {
      this(null);
   }

   public OteLoginJob(OSEEPerson1_4 user) throws UnknownHostException {
      this(user, OteProperties.getDefaultInetAddress());
   }

   public OteLoginJob(OSEEPerson1_4 user, InetAddress address) {
      super("OTE login");
      this.user = user;
      this.address = address;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      try {
         if (user == null) {
            try {
               IWorkbenchUser workbenchUser = OteClientUiPlugin.getDefault().getDirectoryService().getUser();
               user = new OSEEPerson1_4(workbenchUser.getName(), workbenchUser.getEmail(), workbenchUser.getUserID());
            } catch (OseeCoreException ex) {
               user = getFallbackUser(ex);
            } catch (Throwable th) {
               user = getFallbackUser(th);
            }
         }
         Activator.getDefault().getClientService().setUser(user, address);
         Activator.getDefault().getClientService().setSessionDelegate(new OteSessionDelegateViewImpl());
         return Status.OK_STATUS;
      } catch (Exception ex) {
         Activator.log(Level.SEVERE, "failed to login into OTE service", ex);
         return new Status(IStatus.ERROR, "org.eclipse.ote.client", -1, "could not login into OTE client service", ex);
      }
   }

   private OSEEPerson1_4 getFallbackUser(Throwable th) {
      String userName = System.getProperty("user.name");
      OseeLog.log(OteClientUiPlugin.class, Level.INFO,
         "Could not log you in using OSEE Authentication. You will be logged in as " + userName, th);
      return new OSEEPerson1_4(userName, "", userName);
   }
}
