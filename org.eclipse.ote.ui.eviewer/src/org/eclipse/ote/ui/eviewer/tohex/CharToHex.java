package org.eclipse.ote.ui.eviewer.tohex;

import org.eclipse.osee.ote.message.elements.DiscreteElement;


public class CharToHex implements IToHex {

   @Override
   public String toHex(DiscreteElement<?> element) {
      String hexString;
      Character rawData = (Character) element.getValue();
      Long charToLong = (long)rawData;
      hexString = "0x";
      hexString += Long.toHexString(charToLong);
      return hexString;
   }

}
