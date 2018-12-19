package org.eclipse.ote.ui.eviewer.tohex;

import org.eclipse.osee.ote.message.elements.DiscreteElement;

public class InaccessibleToHex implements IToHex{

   @Override
   public String toHex(DiscreteElement<?> element) {
      return " ";
   }
   
}
