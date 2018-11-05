package org.eclipse.ote.ui.eviewer.tohex;

import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;

public class EnumToHex implements IToHex{

   @Override
   public String toHex(DiscreteElement<?> element) {
      // just returns the value as a string
      @SuppressWarnings("rawtypes")
      String hexString = ((EnumeratedElement)element).valueOf().toString().toUpperCase();
      return hexString;
   }

}
