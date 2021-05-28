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

package org.eclipse.osee.ote.runtimemanager.internal;

import java.net.BindException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.classserver.ClassServer;
import org.eclipse.osee.ote.classserver.ResourceFinder;
import org.eclipse.osee.ote.properties.OteProperties;
import org.eclipse.osee.ote.runtimemanager.RuntimeManager;
import org.eclipse.osee.ote.runtimemanager.SafeWorkspaceTracker;

public class RuntimeBundleServer {
   private ClassServer classServer;
   private String classServerPath;
   private ResourceFinder resourceFinder;

   /**
    * Creates a new ClassServer which will serve all projects currently in the workspace
    */
   public RuntimeBundleServer(SafeWorkspaceTracker safeWorkspaceTracker) {
      try {
         InetAddress useHostAddress = OteProperties.getDefaultInetAddress();
         classServer = new ClassServer(0, useHostAddress) {
            @Override
            protected void fileDownloaded(String fp, InetAddress addr) {
               System.out.println("RuntimeBundleServer: File " + fp + " downloaded to " + addr);
            }
         };
         resourceFinder = new RuntimeLibResourceFinder(safeWorkspaceTracker);
         classServer.addResourceFinder(resourceFinder);
         classServer.start();
         if (useHostAddress instanceof Inet6Address) {
            classServerPath = "http://[" + useHostAddress.getHostAddress() + "]:" + classServer.getPort() + "/";
         } else {
            classServerPath = "http://" + useHostAddress.getHostAddress() + ":" + classServer.getPort() + "/";
         }

      } catch (BindException ex) {
         OseeLog.log(RuntimeManager.class, Level.SEVERE,
            "Class Server not started.  Likely the IP address used is not local.  Set your IP address in the advanced page.",
            ex);
      } catch (Exception ex) {
         OseeLog.log(RuntimeManager.class, Level.SEVERE, "Class Server not started.", ex);
      }
   }

   /**
    * @return the path to the class server, to be passed to the environment upon connection
    */
   public String getClassServerPath() {
      return classServerPath;
   }

   /**
    * Stops the class server. This should be called upon stop of the RuntimeManager
    */
   public void stopServer() {
      classServer.terminate();
   }
}
