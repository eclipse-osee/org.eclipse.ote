package org.eclipse.ote.ui.eviewer.view;

import java.text.DecimalFormat;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;

public class ViewerColumnLong extends ViewerColumn {

   long value;
   DecimalFormat format;

   ViewerColumnLong(TableViewer table, int index, String name, String tooltip) {
      super(table, index, name, 75);
      setToolTip(tooltip);
      value = 0;
      format = new DecimalFormat("#,###");
   }

   @Override
   public Object getValue() {
      return value;
   }

   public void setLong(long value) {
      this.value = value;
   }

   public long getLong() {
      return value;
   }

   @Override
   public TableViewerColumn createColumn(TableViewer table) {
      TableViewerColumn column = new TableViewerColumn(table, SWT.LEFT);
      column.getColumn().setText(getName());
      column.getColumn().setMoveable(false);
      column.setLabelProvider(new ColumnLabelProvider() {

         @Override
         public String getToolTipText(Object element) {
            return getToolTip();
         }

         @Override
         public String getText(Object element) {
            try {
               RowUpdate update = (RowUpdate) element;
               Long value = (Long) update.getValue(ViewerColumnLong.this);
               return format.format(value);
            } catch (Exception e) {
               return "?";
            }
         }

         @Override
         public int getToolTipDisplayDelayTime(Object object) {
            return 500;
         }

      });



      return column;
   }

   public static void main(String[] args) {
      DecimalFormat f = new DecimalFormat("#,###");
      System.out.println(f.format(999991234.56));
      System.out.println(f.format(1234.56));
      System.out.println(f.format(123.456));
      System.out.println(f.format(12.3456));
   }


}