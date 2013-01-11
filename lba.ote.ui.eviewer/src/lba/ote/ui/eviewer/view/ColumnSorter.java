package lba.ote.ui.eviewer.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

class ColumnSorter {

   private final HashMap<Integer, Integer> value;

   private final Comparator<ElementColumn> comparator = new Comparator<ElementColumn>() {

      @Override
      public int compare(ElementColumn o1, ElementColumn o2) {
         Integer val1 = value.get(o1.getIndex());
         Integer val2 = value.get(o2.getIndex());
         if (val1 == null) {
            //val1 = Integer.MAX_VALUE - 1;
            throw new IllegalStateException("no mapping for " + o1.getName());
         }
         if (val2 == null) {
            throw new IllegalStateException("no mapping for " + o2.getName());

         }
         return val1.compareTo(val2);
      }
   };

   public ColumnSorter(int[] ordering) {
      value = new HashMap<Integer, Integer>(ordering.length);
      for (int i = 0; i < ordering.length; i++) {
         value.put(ordering[i], i);
      }
   }

   public void sort(List<ElementColumn> columns) {
      Collections.sort(columns, comparator);
   }

   public int orderOf(int columnIndex) {
      return value.get(columnIndex);
   }
}