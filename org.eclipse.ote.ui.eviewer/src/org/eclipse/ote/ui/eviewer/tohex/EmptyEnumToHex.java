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
