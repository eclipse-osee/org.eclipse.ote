/*
 * Created on Oct 30, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ken J. Aguilar
 */
public class ColumnConfiguration {

   private final ArrayList<ColumnDetails> columns;

   private final LinkedList<IColumnConfigurationListener> listeners = new LinkedList<IColumnConfigurationListener>();

   private final LinkedList<Integer> columnOrdering = new LinkedList<Integer>();

   public ColumnConfiguration(ElementContentProvider provider) {
      for (int index : provider.getViewer().getTable().getColumnOrder()) {
         columnOrdering.add(index);
      }
      columns = new ArrayList<ColumnDetails>(columnOrdering.size());

      for (ElementColumn elementColumn : provider.getColumns()) {
    	  columns.add(new ColumnDetails(elementColumn));
      }
     

   }

   public List<ColumnDetails> getColumns() {
      return columns;
   }

   public void swap(ColumnDetails colA, ColumnDetails colB) {
      int colAIndex = columns.indexOf(colA);
      int colBIndex = columns.indexOf(colB);
      Collections.swap(columns, colAIndex, colBIndex);
      Collections.swap(columnOrdering, colAIndex, colBIndex);
      notifySwapped();
   }

   public int[] getOrdering() {
      int[] ordering = new int[columnOrdering.size()];
      for (int i = 0; i < columnOrdering.size(); i++) {
         ordering[i] = columnOrdering.get(i);
      }
      return ordering;
   }

   public void moveUp(ColumnDetails column) {
      int index = columns.indexOf(column);
      if (index > 0) {
         int topIndex = index - 1;
         Collections.swap(columns, index, topIndex);
         Collections.swap(columnOrdering, index, topIndex);
         notifySwapped();
      }
   }

   public void moveTo(int sourceIndex, int destinationIndex) {
      ColumnDetails sourceCol = columns.remove(sourceIndex);
      columns.add(destinationIndex, sourceCol);

      Integer sourceOrdering = columnOrdering.remove(sourceIndex);
      columnOrdering.add(destinationIndex, sourceOrdering);
      notifySwapped();
   }

   public void moveDown(ColumnDetails column) {
      int index = columns.indexOf(column);
      if (index < columns.size() - 1) {
         int topIndex = index + 1;
         Collections.swap(columns, index, topIndex);
         Collections.swap(columnOrdering, index, topIndex);
         notifySwapped();
      }
   }

   public void activate(Collection<ColumnDetails> selection) {
      for (ColumnDetails column : selection) {
         column.setActive(true);
      }
      notifyActiveStateChanged(selection);
   }

   public void deactivate(Collection<ColumnDetails> selection) {
      for (ColumnDetails column : selection) {
         column.setActive(false);
      }
      notifyActiveStateChanged(selection);
   }

   public void addListener(IColumnConfigurationListener listener) {
      listeners.add(listener);
   }

   public void removeListener(IColumnConfigurationListener listener) {
      listeners.remove(listener);
   }

   private void notifySwapped() {
      for (IColumnConfigurationListener listener : listeners) {
         listener.changed();
      }
   }

   public void apply(ElementContentProvider provider) {

      provider.getViewer().getTable().setColumnOrder(getOrdering());
      for (ColumnDetails details : columns) {
         details.apply();
      }

   }

   private void notifyActiveStateChanged(Collection<ColumnDetails> changedColumns) {
      for (IColumnConfigurationListener listener : listeners) {
         listener.activeStateChanged(changedColumns);
      }
   }

   public int indexOf(ColumnDetails details) {
      return columns.indexOf(details);
   }

   public ColumnDetails getColumnDetails(int index) {
      return columns.get(index);
   }
}
