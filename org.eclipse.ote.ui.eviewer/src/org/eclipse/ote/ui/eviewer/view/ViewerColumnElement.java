/*********************************************************************
 * Copyright (c) 2023 Boeing
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
   private boolean isEnumOutputNumber = false;
   private boolean showNumbersAsHex = false;


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
            RowUpdate update = (RowUpdate) element;
            Object value = update.getValue(ViewerColumnElement.this);
            return value != null ? value.toString().intern() : "?";
         }

         @Override
         public Color getBackground(Object element) {
            RowUpdate update = (RowUpdate) element;
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
      if(isEnumOutputNumber && columnElement.getEnumText().length() > 0){
         return ColumnElement.getMessageName(columnElement.getMessageClassName()) + '.' + columnElement.getElementPath().toString() + " (" + columnElement.getEnumText()+ ")";
      } else {
         return ColumnElement.getMessageName(columnElement.getMessageClassName()) + '.' + columnElement.getElementPath().toString();
      }
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

   public boolean isEnumOutputNumber() {
      return isEnumOutputNumber;
   }
   
   public void setEnumOutputNumber(boolean isNumber){
      isEnumOutputNumber = isNumber;
   }
   
   public boolean showNumbersAsHex(){
      return showNumbersAsHex;
   }
   public void setShowNumbersAsHex(boolean showNumbersAsHex) {
      this.showNumbersAsHex = showNumbersAsHex;
   }

}
