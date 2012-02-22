/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.view;

import lba.ote.ui.eviewer.Activator;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.client.msg.core.ISubscriptionListener;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
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

   private final TableViewerColumn column;
   private final String message;
   private final ElementPath path;
   private DiscreteElement<?> element;
   private boolean active = true;
   private volatile Object lastValue = null;

   private final Image activeImg;
   private final Image inactive;
   private final String text;
   private volatile int index;
   private final TableViewer table;

   ElementColumn(TableViewer table, final int index, ElementPath path) {
      super();
      activeImg = Activator.getDefault().getImageRegistry().get("ACTIVE_PNG");
      inactive = Activator.getDefault().getImageRegistry().get("INACTIVE_PNG");
      this.table = table;
      this.path = path;
      column = new TableViewerColumn(table, SWT.LEFT);

      message = path.getMessageClass();
      text = getMessageName(message) + "." + path.toString();
      column.getColumn().setText(text);
      column.getColumn().setWidth(125);
      column.getColumn().setMoveable(true);
      column.getColumn().setToolTipText(text);
      column.getColumn().setImage(activeImg);
      column.setLabelProvider(new ColumnLabelProvider() {

         @Override
         public String getText(Object element) {
            if (column.getColumn().isDisposed()) {
               return "?";
            }
            ElementUpdate update = (ElementUpdate) element;
            Object value = update.getValue(ElementColumn.this);
            return value != null ? value.toString().intern() : "?";

         }

         @Override
         public Color getBackground(Object element) {
            if (column.getColumn().isDisposed()) {
               return Displays.getSystemColor(SWT.COLOR_RED);
            }
            ElementUpdate update = (ElementUpdate) element;
            return update.getDeltaSet().get(index) ? Displays.getSystemColor(SWT.COLOR_GREEN) : null;
         }

      });
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
      Object value = element.getValue();
      if (lastValue == null || !value.equals(lastValue)) {
         lastValue = value;
         return true;
      }
      return false;
   }

   public Object getValue() {
      return lastValue;
   }

   public String getName() {
      return text;
   }

   public String getMessageClassName() {
      return message;
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
   }

   @Override
   public void subscriptionNotSupported(IMessageSubscription subscription) {
      element = null;
   }

   @Override
   public void subscriptionResolved(IMessageSubscription subscription) {
      element = (DiscreteElement<?>) subscription.getMessage().getElementByPath(path);
      column.getColumn().setToolTipText(
         String.format("%s\nByte Offset: %d\nMSB: %d\nLSB: %d", text, element.getByteOffset(), element.getMsb(),
            element.getLsb()));
      lastValue = element.getValue();
   }

   @Override
   public void subscriptionUnresolved(IMessageSubscription subscription) {
      element = null;
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
