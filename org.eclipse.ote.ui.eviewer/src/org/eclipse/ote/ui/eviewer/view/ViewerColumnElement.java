package org.eclipse.ote.ui.eviewer.view;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class ViewerColumnElement extends ViewerColumn {
   private final Image activeImg;
   private final Image inactive;
   private final Image duplicate;

   private ColumnElement columnElement;
   private boolean duplicateName = false;


   ViewerColumnElement(TableViewer table, int index, ElementPath path) {
      super(table, index, path.toString(), 125);

      activeImg = null;
      inactive = Activator.getDefault().getImageRegistry().get("INACTIVE_PNG");
      duplicate = Activator.getDefault().getImageRegistry().get("DUPLICATE_PNG");


      columnElement = new ColumnElement(this, path);
   }

   @Override
   public TableViewerColumn createColumn(TableViewer table) {
      TableViewerColumn column = new TableViewerColumn(table, SWT.LEFT);
      column.getColumn().setText(getName());
      column.getColumn().setToolTipText(getName());
      column.getColumn().setMoveable(true);
      column.getColumn().setImage(activeImg);
      column.setLabelProvider(new ColumnLabelProvider() {

         @Override
         public String getToolTipText(Object element) {
            return getToolTip();
         }

         @Override
         public String getText(Object element) {
            ElementUpdate update = (ElementUpdate) element;
            Object value = update.getValue(ViewerColumnElement.this);
            return value != null ? value.toString().intern() : "?";
         }

         @Override
         public Color getBackground(Object element) {
            ElementUpdate update = (ElementUpdate) element;
            return update.isChanged(ViewerColumnElement.this) ? Displays.getSystemColor(SWT.COLOR_GREEN) : null;
         }

         @Override
         public int getToolTipDisplayDelayTime(Object object) {
            return 500;
         }

      });
      return column;
   }

   public ColumnElement getColumnElement() {
      return columnElement;
   }

   @Override
   public Object getValue() {
      return columnElement.getValue();
   }

   @Override
   public String getVerboseName() {
      return columnElement.getVerboseName();
   }

   public boolean isDuplicateName() {
      return duplicateName;
   }

   public void setDuplicateName(boolean duplicateName) {
      this.duplicateName = duplicateName;
      if(isActive()){
         getColumn().setImage(duplicateName ? duplicate : activeImg);
      }
      columnElement.setToolTip();
   }

   /**
    * @param active the active to set
    */
   @Override
   public void setActive(boolean active) {
      super.setActive(active);
      getColumn().setImage(active ? activeImg : inactive);
   }



}
