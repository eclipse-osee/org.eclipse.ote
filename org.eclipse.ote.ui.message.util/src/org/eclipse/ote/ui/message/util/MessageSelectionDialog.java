/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.message.util;

import java.util.ArrayList;
import java.util.logging.Level;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.eclipse.osee.ote.message.MessageSink;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ken J. Aguilar
 */
public class MessageSelectionDialog extends ElementListSelectionDialog {

   public MessageSelectionDialog(Shell parent) {
      super(parent, new ILabelProvider() {

         @Override
         public Image getImage(Object element) {
            return null;
         }

         @Override
         public String getText(Object element) {
            String msgName = (String) element;
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

      });

      ServiceTracker tracker =
         new ServiceTracker(FrameworkUtil.getBundle(getClass()).getBundleContext(), MessageDefinitionProvider.class.getName(), null);
      tracker.open(true);
      try {
    	 Object[] dictionaries = tracker.getServices();
    	 final ArrayList<String> messages = new ArrayList<String>(4096);
    	 for(Object dictionary:dictionaries){
	         ((MessageDefinitionProvider)dictionary).generateMessageIndex(new MessageSink() {
	            @Override
	            public void absorbElement(String elementName) {
	            }
	            @Override
	            public void absorbMessage(String messageName) {
	               messages.add(messageName);
	            }
	            @Override
	            public void absorbProvider(String providerName) {
	            }
	         });
    	 }
         setElements(messages.toArray());
      } catch (Exception e) {
         OseeLog.log(MessageSelectionDialog.class, Level.SEVERE, "failed to generate message listing", e);
      } finally {
         tracker.close();
      }
      setMessage("Select a message. Use * as the wild card character");
      setTitle("Message Selection");
   }
}