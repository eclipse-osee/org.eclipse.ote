/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.message.condition;

import org.eclipse.osee.ote.message.elements.StringElement;

/**
 * this condition checks to see if a {@link StringElement} is empty. Empty is defined as having the first byte/character
 * of the element equal to zero.
 * 
 * @author Ken J. Aguilar
 */
public class EmptyStringCondition extends AbstractCondition implements IDiscreteElementCondition<Character> {

   public final StringElement element;
   private char lastValue;
   private final int offset;

   public EmptyStringCondition(StringElement element) {
      this.element = element;
      offset = element.getMsgData().getMem().getOffset() + element.getByteOffset();
   }

   @Override
   public Character getLastCheckValue() {
      return lastValue;
   }

   @Override
   public boolean check() {
      lastValue = (char) element.getMsgData().getMem().getData()[offset];
      return lastValue == (char) 0;
   }

}
