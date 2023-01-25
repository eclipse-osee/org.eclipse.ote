/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.ote.ui.eviewer.tohex;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.elements.CharElement;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.EmptyEnum_Element;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.elements.NumericElement;
import org.eclipse.osee.ote.message.elements.StringElement;

public class ToHexFactory {
   
   public IToHex getHexConverter(DiscreteElement<?> elementType){
      if(elementType instanceof EnumeratedElement){
         return new EnumToHex();
      }
      else if (elementType instanceof NumericElement<?>){
         return new NumericToHex();
      }else if (elementType instanceof EmptyEnum_Element){
         return new EmptyEnumToHex();
      }else if (elementType instanceof StringElement){
         return new StringToHex();
      }else if (elementType instanceof CharElement){
         return new CharToHex();
      }else{
         OseeLog.log(ToHexFactory.class, Level.SEVERE, "ERROR: " + elementType.getDescriptiveName() + " was not handled correctly");
         return new InaccessibleToHex();
      }
   }
}
