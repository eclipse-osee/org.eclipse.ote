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

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ken J. Aguilar
 */
public final class RowUpdate {
   private final Object[] values;
   private final BitSet deltaSet;
   private final HashMap<ViewerColumn, Integer> valueMap;

   RowUpdate(HashMap<ViewerColumn, Integer> valueMap, List<ViewerColumn> allColumns) {
      this.valueMap = valueMap;
      int size = allColumns.size();
      values = new Object[size];
      this.deltaSet = new BitSet(size);
      for (int i = 0; i < size; i++) {
         ViewerColumn col = allColumns.get(i);
         values[i] = col.getValue();
         deltaSet.set(i);
      }
   }

   private RowUpdate(Object[] values, BitSet deltaSet, HashMap<ViewerColumn, Integer> valueMap) {
      super();
      this.values = values;
      this.deltaSet = deltaSet;
      this.valueMap = valueMap;
   }

   public RowUpdate next(HashMap<ViewerColumn, Integer> valueMap, List<ViewerColumn> allColumns) {
      int size = allColumns.size();
      // if the value map is not equal then a change in the columns (ordering, adding, etc) has occurred
      if (valueMap == this.valueMap) {
         // no column changes so we can clone and then find deltas
         Object[] newValues = this.values.clone();
         BitSet newDeltaSet = new BitSet(size);
         for (int i = 0; i < size; i++) {
            ViewerColumn col = allColumns.get(i);
            if (col instanceof ViewerColumnElement) {
               ViewerColumnElement elementCol = (ViewerColumnElement) col;
               if (elementCol.getColumnElement().getAndClearUpdateState()) {
                  Object value = elementCol.getValue();
                  newValues[i] = value;
                  // even though a update flag is set, the value may have
                  // reverted to the same value of the last visual update
                  // which can happen in inactive columns
                  if (value != null) {
                     newDeltaSet.set(i, !value.equals(values[i]));
                  }
               }
            } else {
               newValues[i] = col.getValue();
            }
         }
         return new RowUpdate(newValues, newDeltaSet, valueMap);
      } else {
         Object[] newValues = new Object[size];
         BitSet newDeltaSet = new BitSet(size);
         for (int i = 0; i < size; i++) {
            ViewerColumn col = allColumns.get(i);
            Object value = col.getValue();
            newValues[i] = value;
            if (value != null && !value.equals(getValue(col))) {
               newDeltaSet.set(i);
            }
         }
         return new RowUpdate(newValues, newDeltaSet, valueMap);
      }
   }

   //   public void setValue(ViewerColumn column, Object value) {
   //      Integer index = valueMap.get(column);
   //      values[index] = value;
   //   }

   public Object getValue(ViewerColumn column) {
      Integer index = valueMap.get(column);
      if (index == null || index >= values.length) {
         return "invalid index: " + index;
      } else {
         return values[index];
      }

   }

   public boolean isChanged(ViewerColumn column) {
      Integer index = valueMap.get(column);
      return index != null ? deltaSet.get(index) : false;
   }

}
