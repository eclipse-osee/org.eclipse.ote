/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import java.util.List;
import java.util.logging.Level;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.message.lookup.MessageLookup;
import org.eclipse.ote.message.lookup.MessageLookupResult;
import org.eclipse.ote.ui.message.util.internal.MessageUtilServiceUtility;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @author Ken J. Aguilar
 */
public class MessageSelectionDialog extends ElementListSelectionDialog {

   public MessageSelectionDialog(Shell parent) {
      super(parent,  new MessageLabelProvider());
      try {
         MessageLookup messageLookup = MessageUtilServiceUtility.getService(MessageLookup.class);
         List<MessageLookupResult> results = messageLookup.lookup("*");
    	   setElements(results.toArray());
      } catch (Exception e) {
         OseeLog.log(MessageSelectionDialog.class, Level.SEVERE, "failed to generate message listing", e);
      } 
      setMessage("Select a message. Use * as the wild card character");
      setTitle("Message Selection");
   }

   public MessageSelectionDialog(Shell parent, List<String> messageClassesToUse) {
      super(parent, new MessageLabelProvider());
      setElements(messageClassesToUse.toArray());
      setMessage("Select a message. Use * as the wild card character");
      setTitle("Message Selection");
   }
   
   private static class MessageLabelProvider implements ILabelProvider {
      @Override
      public Image getImage(Object element) {
         return null;
      }

      @Override
      public String getText(Object element) {
         String msgName;
         if(element instanceof MessageLookupResult){
            msgName = ((MessageLookupResult)element).getClassName();
         } else {
            msgName = (String)element;
         }
         String packageName = msgName.substring(0, msgName.lastIndexOf('.'));
         String type = packageName.substring(packageName.lastIndexOf('.') + 1);

         return String.format("%s [%s]", msgName.substring(msgName.lastIndexOf('.') + 1), type);
      }

      @Override
      public void addListener(ILabelProviderListener listener) {
      }

      @Override
      public void dispose() {
      }

      @Override
      public boolean isLabelProperty(Object element, String property) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener listener) {
      }

   }

}
