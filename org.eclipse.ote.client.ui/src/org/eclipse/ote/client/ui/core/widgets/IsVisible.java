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

package org.eclipse.ote.client.ui.core.widgets;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.widgets.Tree;
 
/**
 * @author Andrew M. Finkbeiner
 */
public class IsVisible implements Runnable {

   private Tree tree;
   private volatile boolean isVisible;

   public IsVisible(Tree tree){
      this.tree = tree;
   }

   public boolean isVisible() {
      return isVisible;
   }

   @Override
   public void run() {
	   try{ 
		   if(!tree.isDisposed()){
			   isVisible = tree.isVisible();
		   } else {
			   isVisible = false;
		   }
	   } catch (Throwable th){
		   isVisible = false;
		   OseeLog.log(getClass(), Level.WARNING, th);
	   }
   }

}