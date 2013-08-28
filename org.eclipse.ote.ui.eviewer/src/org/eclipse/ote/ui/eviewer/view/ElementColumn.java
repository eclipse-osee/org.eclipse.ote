/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.eviewer.view;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.client.msg.core.ISubscriptionListener;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Ken J. Aguilar
 */
public class ElementColumn implements ISubscriptionListener {

   private static final String UNKNOWN_VALUE = "???";
   private final TableViewerColumn column;
   private final String message;
   private final ElementPath path;
   private DiscreteElement<?> element;
   private volatile boolean active = true;
   private final AtomicReference<Object> lastValueReference = new AtomicReference<Object>(new Object());

   private final Image activeImg;
   private final Image inactive;
   private final Image duplicate;
   private final String text;
   private volatile int index;
   private final TableViewer table;
   private final AtomicBoolean valueUpdatedFlag = new AtomicBoolean(false);
   private String tip;
   private String verbosetext;
   private boolean duplicateName = false;

   ElementColumn(TableViewer table, final int index, ElementPath path) {
      super();
      activeImg = null;
      inactive = Activator.getDefault().getImageRegistry().get("INACTIVE_PNG");
      duplicate = Activator.getDefault().getImageRegistry().get("DUPLICATE_PNG");

      this.table = table;
      this.path = path;
      column = new TableViewerColumn(table, SWT.LEFT);

      message = path.getMessageClass();
      text = path.toString();
      verbosetext = getMessageName(message) + "." + path.toString();
      column.getColumn().setText(text);
      column.getColumn().setWidth(125);
      column.getColumn().setMoveable(true);
      column.getColumn().setToolTipText(text);
      column.getColumn().setImage(activeImg);
      column.setLabelProvider(new ColumnLabelProvider() {

         @Override
         public String getToolTipText(Object element) {
            return tip;
         }

         @Override
         public String getText(Object element) {
            ElementUpdate update = (ElementUpdate) element;
            Object value = update.getValue(ElementColumn.this);
            return value != null ? value.toString().intern() : "?";

         }

         @Override
         public Color getBackground(Object element) {
            ElementUpdate update = (ElementUpdate) element;
            return update.isChanged(ElementColumn.this) ? Displays.getSystemColor(SWT.COLOR_GREEN) : null;
         }

         @Override
         public int getToolTipDisplayDelayTime(Object object) {
            return 500;
         }

      });
      tip = text;
      this.index = table.getTable().indexOf(column.getColumn());
   }

   public ElementPath getElementPath() {
      return path;
   }

   void addMoveListener(final Listener listener) {
      column.getColumn().addListener(SWT.Move, listener);
   }

   void removeMoveListener(final Listener listener) {
      column.getColumn().removeListener(SWT.Move, listener);
   }

   protected static String getMessageName(String msgClassName) {
      return msgClassName.substring(msgClassName.lastIndexOf('.') + 1);
   }

   public void dispose() {
      element = null;
      column.getColumn().dispose();
   }

   public boolean update() {
      if(element != null){
         Object current = element.getValue();
         Object lastValue = lastValueReference.get();
         if (!current.equals(lastValue)) {
            lastValueReference.set(current);
            valueUpdatedFlag.set(true);
            return true;
         }
      }
      return false;
   }

   public Object getValue() {
      return lastValueReference.get();
   }


   public String getName() {
      return text;
   }

   public String getElementText() {
      return text;
   }

   public boolean isDuplicateName() {
      return duplicateName;
   }

   public void setDuplicateName(boolean duplicateName) {
      this.duplicateName = duplicateName;
      if(active){
         column.getColumn().setImage(duplicateName ? duplicate : activeImg);
      }
      setToolTip();
   }

   public String getVerboseName() {
      return verbosetext;
   }

   public String getMessageClassName() {
      return message;
   }

   public boolean getAndClearUpdateState() {
      return valueUpdatedFlag.getAndSet(false);
   }

   @Override
   public void subscriptionActivated(IMessageSubscription subscription) {
   }

   @Override
   public void subscriptionCanceled(IMessageSubscription subscription) {
      element = null;
   }

   @Override
   public void subscriptionInvalidated(IMessageSubscription subscription) {
      element = null;
      lastValueReference.set(UNKNOWN_VALUE);
   }

   @Override
   public void subscriptionNotSupported(IMessageSubscription subscription) {
      element = null;
      lastValueReference.set(UNKNOWN_VALUE);
   }

   @Override
   public void subscriptionResolved(IMessageSubscription subscription) {

      element = (DiscreteElement<?>) subscription.getMessage().getElementByPath(path);
      Displays.ensureInDisplayThread(new Runnable() {



         @Override
         public void run() {
            setToolTip();
         }
      });
      lastValueReference.set(element != null ? element.getValue() : UNKNOWN_VALUE);
   }

   @Override
   public void subscriptionUnresolved(IMessageSubscription subscription) {
      tip = "not found";
      element = null;
      lastValueReference.set(UNKNOWN_VALUE);

   }

   private void setToolTip(){
      String tip = "";
      if (element == null) {
         tip = "The element " + getElementPath() + " does not exist on " + getMessageClassName();
      } else {
         tip = String.format("%s.%s\nByte Offset: %d\nMSB: %d\nLSB: %d",  getMessageName(getMessageClassName()), text, element.getByteOffset(), element.getMsb(),
               element.getLsb());
         if(duplicateName){
            tip = "Note: Duplicate name in view\n" + tip;
         }
      }
      column.getColumn().setToolTipText(tip);
   }

   /**
    * @return the active
    */
   public boolean isActive() {
      return active;
   }

   /**
    * @param active the active to set
    */
   public void setActive(boolean active) {
      this.active = active;
      column.getColumn().setImage(active ? activeImg : inactive);
   }

   /**
    * returns the creation order index of this column. see {@link Table#indexOf(TableColumn)}
    * 
    * @return the column index
    */
   public int getIndex() {
      return index;
   }

   public int recheckIndex() {
      index = table.getTable().indexOf(column.getColumn());
      return index;
   }

   public TableColumn getColumn() {
      return column.getColumn();
   }

}
