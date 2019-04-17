/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.client.ui.core.widgets;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.client.ui.internal.ServiceUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Andrew M. Finkbeiner
 */
public class MonitorRestLookup implements Runnable{

   private Tree tree;
   private int count;
   
   public MonitorRestLookup(Tree tree) {
      this.tree = tree;
      count = 1;
   }
   
   @Override
   public synchronized void run() {
      try {
         count++;
         IsVisible isVisible = new IsVisible(tree);
         Display.getDefault().syncExec(isVisible);
         if(isVisible.isVisible() || count % 10 == 0){
            RestLookup restLookup = ServiceUtil.getService(RestLookup.class);
            /*
             * if we fail to communicate with the master rest server wait 1 minute before trying again
             * because it is likely not running and we do not want to waste time trying to talk to a server
             * every 5 seconds that isn't running
             */
            if(!restLookup.getLatest()){
               Thread.sleep(1000 * 60);
            }
         } 
      } catch (Throwable e){
         OseeLog.log(getClass(), Level.SEVERE, e);
      }
   }

}