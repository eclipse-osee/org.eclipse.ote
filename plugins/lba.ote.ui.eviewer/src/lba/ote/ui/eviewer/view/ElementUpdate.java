/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.view;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ken J. Aguilar
 */
public class ElementUpdate {
   private final Object[] values;
   private final BitSet deltaSet;
   private final HashMap<ElementColumn, Integer> valueMap;

   ElementUpdate(HashMap<ElementColumn, Integer> valueMap, List<ElementColumn> columnSet, BitSet deltaSet) {
      this.valueMap = valueMap;
      values = new Object[columnSet.size()];
      this.deltaSet = (BitSet) deltaSet.clone();
      for (int i = 0; i < columnSet.size(); i++) {
         values[i] = columnSet.get(i).getValue();
      }
   }

   public Object getValue(ElementColumn column) {
      Integer index = valueMap.get(column);
      return index != null ? values[index] : null;
   }

   public BitSet getDeltaSet() {
      return deltaSet;
   }

}
