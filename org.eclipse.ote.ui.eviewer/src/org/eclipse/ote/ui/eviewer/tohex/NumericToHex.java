package org.eclipse.ote.ui.eviewer.tohex;

import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.NumericElement;


public class NumericToHex implements IToHex {

   @Override
   public String toHex(DiscreteElement<?> element) {
      String hexString;
      long rawData = ((NumericElement<?>)element).getNumericBitValue();
      hexString = "0x";
      hexString += Long.toHexString(rawData).toUpperCase();
      return hexString;
   }

}
