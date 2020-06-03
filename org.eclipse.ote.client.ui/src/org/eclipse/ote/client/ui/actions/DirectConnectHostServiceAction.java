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

package org.eclipse.ote.client.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ote.client.ui.core.widgets.HostSelectionTable;
import org.eclipse.ote.client.ui.core.widgets.RestLookup;
import org.eclipse.ote.client.ui.core.widgets.RestLookupConnector;
import org.eclipse.ote.client.ui.internal.ServiceUtil;
import org.eclipse.swt.widgets.Display;

/**
 * @author Andrew M. Finkbeiner
 */
public class DirectConnectHostServiceAction extends Action {

   public DirectConnectHostServiceAction() {
      super("OTE Server Connect", IAction.AS_PUSH_BUTTON);
   }

   @Override
   public void run() {
      ServerConnectDialog dialog = new ServerConnectDialog(AWorkbench.getActiveShell());
      if(dialog.open() == Window.OK){
         new Thread(new Runnable(){
            @Override
            public void run() {
               RestLookup restLookup = ServiceUtil.getService(RestLookup.class);
               RestLookupConnector add = restLookup.add(dialog.getServerURI());
               if(add != null){
                  HostSelectionTable.doConnection(add);
               } else {
                  Display.getDefault().asyncExec(new Runnable(){

                     @Override
                     public void run() {
                        MessageDialog.openError(null, "Server Direct Connection", String.format("Unable to communicate and connect to Test Server [%s].", dialog.getServerURI()));
                     }});
               }
            }
         }).start();
      }
   }

}
