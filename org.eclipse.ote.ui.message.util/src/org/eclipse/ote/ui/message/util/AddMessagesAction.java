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

package org.eclipse.ote.ui.message.util;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ote.message.lookup.MessageLookupResult;
import org.eclipse.ote.ui.message.util.MessageSelectionDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ken J. Aguilar
 */
public class AddMessagesAction extends Action {


	private final MessageSelectComposite composite;
	
   public AddMessagesAction(MessageSelectComposite composite) {
      super("Select Messages", IAction.AS_PUSH_BUTTON);
      setToolTipText("Select messages to append to the list of messages that will be recorded");
      this.composite = composite;
      setImageDescriptor(Images.ADD.getImageDescriptor());
      
   }

   @Override
   public void run() {
      Shell shell = Displays.getActiveShell();
      MessageSelectionDialog msgSelectionDialog = new MessageSelectionDialog(shell);
      msgSelectionDialog.setMultipleSelection(true);
      if (msgSelectionDialog.open() == Window.OK) {
         Object[] result = (Object[])msgSelectionDialog.getResult();
         ArrayList<MessageLookupResult> list = new ArrayList<MessageLookupResult>(result.length);
         for (Object item : result) {
        	 MessageLookupResult lookupResult = (MessageLookupResult) item;
        	 list.add(lookupResult);
         }
         composite.addMessages(list);
      }

   }

}
