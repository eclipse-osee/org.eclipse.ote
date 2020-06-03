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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.elements.RecordMap;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ken J. Aguilar
 */
public class MessageElementSelectionDialog extends ElementListSelectionDialog {

   public MessageElementSelectionDialog(Shell parent, Message<?, ?, ?> msg) {
      this(parent, msg, null, false);
   }
   
   public MessageElementSelectionDialog(Shell parent, Message<?, ?, ?> msg, ElementFilter filter) {
      this(parent, msg, filter, false);
   }
   
   public MessageElementSelectionDialog(Shell parent, String msg, boolean headerOnly) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      this(parent, getMessage(msg), null, headerOnly);
   }

   public MessageElementSelectionDialog(Shell parent, String msg, ElementFilter filter) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalStateException {
      this(parent, getMessage(msg), filter, false);
   }

   public MessageElementSelectionDialog(Shell parent, Message<?, ?, ?> msg, ElementFilter filter, boolean headerOnly) {
      super(parent, new LabelProvider());
      LinkedList<Element> topLevelElements = new LinkedList<Element>();
      LinkedList<Element> filterElements = new LinkedList<Element>();
      if(headerOnly){
         IMessageHeader header = msg.getActiveDataSource().getMsgHeader();
         Collections.addAll(topLevelElements, header.getElements());
      } else {
         msg.getAllElements(topLevelElements);
      }

      process(filter, topLevelElements, filterElements);
      setElements(filterElements.toArray());
      setMessage("Select a message element. Use * as the wild card character");
      setTitle("Message Element Selection");
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private static Message<?, ?, ?> getMessage(String msg) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      ServiceTracker tracker =
         new ServiceTracker(FrameworkUtil.getBundle(MessageElementSelectionDialog.class).getBundleContext(), MessageDefinitionProvider.class.getName(),
            null);
      tracker.open(true);
      try {
         Object[] dictionary = tracker.getServices();
         if (dictionary.length == 0) {
            throw new IllegalStateException("no dictionary loaded");
         }
         return ExportClassLoader.getInstance().loadClass(msg).asSubclass(Message.class).newInstance();
      } finally {
         tracker.close();
      }
   }

   private static final class LabelProvider implements ILabelProvider {

      @Override
      public Image getImage(Object element) {
         return null;
      }

      @Override
      public String getText(Object element) {
         Element msgElement = (Element) element;
         return new ElementPath(msgElement.getElementPath()).toString();
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

   };

   private void process(ElementFilter filter, List<Element> list, List<Element> destinationList) {
      for (Element element : list) {
         processElement(filter, element, destinationList);
      }
   }

   private void processElement(ElementFilter filter, Element element, List<Element> destinationList) {
      if (element instanceof RecordMap<?>) {
         processRecordMap(filter, (RecordMap<?>) element, destinationList);
      } else if (element instanceof RecordElement) {
         processRecordElement(filter, (RecordElement) element, destinationList);
      } else {
         if (filter == null || filter.accept(element)) {
            destinationList.add(element);
         }
      }
   }

   private void processRecordMap(ElementFilter filter, RecordMap<?> map, List<Element> destinationList) {
      for (int i = 0; i < map.length(); i++) {
         processRecordElement(filter, map.get(i), destinationList);
      }

   }

   private void processRecordElement(ElementFilter filter, RecordElement element, List<Element> destinationList) {
      if (element instanceof RecordMap<?>) {
         processRecordMap(filter, (RecordMap<?>) element, destinationList);
      } else {
         for (Element childElement : element.getElementMap().values()) {
            processElement(filter, childElement, destinationList);
         }
      }
   }

}
