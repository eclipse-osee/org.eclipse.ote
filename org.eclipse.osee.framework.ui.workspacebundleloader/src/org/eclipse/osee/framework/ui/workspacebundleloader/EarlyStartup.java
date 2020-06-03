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

package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.workspacebundleloader.internal.Activator;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * @author Andrew M. Finkbeiner
 */
public class EarlyStartup implements IStartup {

   private SafeWorkspaceTracker workspaceTracker;

   @Override
   public void earlyStartup() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               Bundle bundle = Platform.getBundle("org.eclipse.osee.framework.ui.workspacebundleloader");
               bundle.start();
               workspaceTracker = new SafeWorkspaceTracker(bundle.getBundleContext());
               workspaceTracker.open(true);
            } catch (BundleException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });

   }
}
