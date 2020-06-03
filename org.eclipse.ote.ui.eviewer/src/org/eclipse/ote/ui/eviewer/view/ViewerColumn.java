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

package org.eclipse.ote.ui.eviewer.view;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class ViewerColumn {

   private final TableViewerColumn column;

   private final String text;
   private volatile int index;
   private volatile boolean active = true;

   private final TableViewer table;
   private String tip;
   private int columnWidth;

   ViewerColumn(TableViewer table, final int index, String text, int columnWidth) {
      super();
      this.table = table;
      this.text = text;
      setDefaultColumnWidth(columnWidth);
      tip = text;

      column = createColumn(table);
      column.getColumn().setWidth(this.columnWidth);

      this.index = table.getTable().indexOf(column.getColumn());
   }

   public abstract TableViewerColumn createColumn(TableViewer table);

   public int getDefaultColumnWidth() {
      return columnWidth;
   }

   public void setDefaultColumnWidth(int columnWidth) {
      this.columnWidth = columnWidth > 25 ? columnWidth : 25;
   }

   public void setToolTip(final String text) {
      tip = text;
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            column.getColumn().setToolTipText(text);
         }
      });
   }

   public String getToolTip() {
      return tip;
   }

   void addMoveListener(final Listener listener) {
      column.getColumn().addListener(SWT.Move, listener);
   }

   void removeMoveListener(final Listener listener) {
      column.getColumn().removeListener(SWT.Move, listener);
   }

   public abstract Object getValue();


   public String getName() {
      return text;
   }

   public String getVerboseName() {
      return text;
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
   }

}
