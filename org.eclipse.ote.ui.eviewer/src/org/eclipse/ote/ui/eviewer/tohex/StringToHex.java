package org.eclipse.ote.ui.eviewer.tohex;

import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.StringElement;

public class StringToHex implements IToHex{

   @Override
   public String toHex(DiscreteElement<?> element) {
      String hexString;
      hexString = ((StringElement)element).getValue();
      return hexString;
   }

}
