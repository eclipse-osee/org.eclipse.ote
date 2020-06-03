/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.ote.ui.eviewer.action;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.ote.ui.eviewer.view.ViewerColumnElement;
import org.eclipse.ote.ui.message.util.MessageElementSelectionDialog;
import org.eclipse.ote.ui.message.util.MessageSelectionDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken J. Aguilar
 * @author Andrew M. Finkbeiner
 */
public class AddHeaderElementAction extends Action {

   private final ElementContentProvider elementContentProvider;

   public AddHeaderElementAction(ElementContentProvider elementContentProvider) {
      super("Add Header Element", IAction.AS_PUSH_BUTTON);
      this.elementContentProvider = elementContentProvider;
      setImageDescriptor(Activator.getImageDescriptor("OSEE-INF/images/add.png"));
   }

   @Override
   public void run() {
      Shell shell = Displays.getActiveShell();
      List<ViewerColumnElement> cols = elementContentProvider.getElementColumns();
      List<String> messageClassesToUse = new ArrayList<String>();
      for(ViewerColumnElement col:cols){
         String messageClassName = col.getColumnElement().getMessageClassName();
         if(!messageClassesToUse.contains(messageClassName)){
            messageClassesToUse.add(messageClassName);
         }
      }
      MessageSelectionDialog msgSelectionDialog = new MessageSelectionDialog(shell, messageClassesToUse);
      if (msgSelectionDialog.open() == Window.OK) {
         Object[] result = (Object[])msgSelectionDialog.getResult();
         String msgClassName = (String) result[0];
         try {
            MessageElementSelectionDialog msgElementSelectionDialog =
                  new MessageElementSelectionDialog(shell, msgClassName, true);
            msgElementSelectionDialog.setMultipleSelection(true);
            msgElementSelectionDialog.setIgnoreCase(true);
            msgElementSelectionDialog.open();
            Object[] elementresult = msgElementSelectionDialog.getResult();
            if (elementresult != null) {
               for (Object r : elementresult) {
                  Element element = (Element) r;
                  elementContentProvider.add(new ElementPath(element.getElementPath()));
               }

            }
         } catch (IllegalStateException ex) {
            MessageDialog.openError(shell, "No Dictionary", "A message libary has not been loaded yet");
         } catch (Exception ex) {
            OseeLog.log(AddHeaderElementAction.class, Level.SEVERE, "exception opening element selection dialog", ex);
            MessageDialog.openError(shell, "Exception",
                  "An exception has ocurred while trying to access the message elements. See Error Log for details");
         }
      }

   }

}
