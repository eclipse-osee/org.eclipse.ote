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

      for (ViewerColumn elementColumn : provider.getAllColumns()) {
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

   public void moveUp(List<ColumnDetails> selection) {
      int first = columns.indexOf(selection.get(0));
      int last= columns.indexOf(selection.get(selection.size() -1));
      if (first <= 0) {
         return;
      }
      Collections.rotate(columns.subList(first-1, last +1), -1);
      Collections.rotate(columnOrdering.subList(first-1, last +1), -1);
      notifySwapped();
   }

   public void moveTo(List<ColumnDetails> selection, ColumnDetails target) {
      int targetIndex = columns.indexOf(target);
      int selectionStart = columns.indexOf(selection.get(0));
      int selectionStop = columns.indexOf(selection.get(selection.size() -1));
      if (targetIndex < selectionStart) {
         // moving up the list
         Collections.rotate(columns.subList(targetIndex, selectionStop +1), selection.size());
         Collections.rotate(columnOrdering.subList(targetIndex, selectionStop +1), selection.size());
      } else {
         // moving down the list
         Collections.rotate(columns.subList(selectionStart, targetIndex +1), -selection.size());
         Collections.rotate(columnOrdering.subList(selectionStart, targetIndex +1), -selection.size());
      }
      notifySwapped();
   }

   public void moveDown(List<ColumnDetails> selection) {
      int first = columns.indexOf(selection.get(0));
      int last= columns.indexOf(selection.get(selection.size() -1));
      if (last >= columns.size() - 1) {
         return;
      }
      Collections.rotate(columns.subList(first, last +2), 1);
      Collections.rotate(columnOrdering.subList(first, last +2), 1);
      notifySwapped();
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
      provider.determineConflicts();

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
