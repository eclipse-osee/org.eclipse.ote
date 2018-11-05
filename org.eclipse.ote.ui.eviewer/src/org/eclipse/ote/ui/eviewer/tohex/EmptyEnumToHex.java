package org.eclipse.ote.ui.eviewer.tohex;

import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.EmptyEnum_Element;

public class EmptyEnumToHex implements IToHex{

   @Override
   public String toHex(DiscreteElement<?> element) {
      String hexString;
      hexString = ((EmptyEnum_Element)element).getValue().toString().toUpperCase();
      return hexString;
   }
}
