/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.simple.oteide.product.load;

import java.util.Dictionary;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.services.core.ServiceUtility;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * @author Andrew M. Finkbeiner
 */
public class SetTitleBar implements IStartup {

   @Override
   public void earlyStartup() {
      String title = getTitle();
      if(title != null) {
         setTitle(title);
      } else if(ServiceUtility.getContext() != null){
         ServiceUtility.getContext().addBundleListener(new BundleListener() {
            @Override
            public void bundleChanged(BundleEvent event) {
               if(event.getType() == Bundle.ACTIVE){
                  if(event.getBundle().getSymbolicName().equals("bundle.to.base.off.here")){
                     String t = getTitle();
                     if(t != null){
                        setTitle(t);
                     }
                  }
               }
            }
         });
      }
      WorkspaceStatusLineContributionItem.addToAllViews();
   }
   
   private void setTitle(final String title){
      Display.getDefault().asyncExec(new Runnable() {
         @Override
         public void run() {
            try {
               IEclipsePreferences node = InstanceScope.INSTANCE.getNode("org.eclipse.ui.ide");
               String current = node.get("WORKSPACE_NAME", "");
               Matcher matcher = Pattern.compile(".*?\\..*?\\.\\d{4}_\\d{2}_\\d{2}_\\d+?").matcher(current);
               if(current.length() == 0 || matcher.matches()){
                  node.put("WORKSPACE_NAME", title);
               }
            } catch (Throwable ex) {
               OseeLog.log(SetTitleBar.class, Level.SEVERE, ex);
            }
         }
      });
   }
   
   private String getTitle(){
      Bundle b = Platform.getBundle("bundle.to.base.off.here");
      if(b != null){
         Dictionary<String, String> headers = b.getHeaders();
         String title = headers.get("Implementation-Version");
         if(title == null){
            title = headers.get("Bundle-Version");
         }
         if(title != null){
            return title;
         }
      }
      return null;
   }

}
