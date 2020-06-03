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

package org.eclipse.osee.ote.message.mock;

import org.eclipse.osee.ote.message.elements.IEnumValue;

public enum TestEnum implements IEnumValue<TestEnum> {
   VAL_0,
   VAL_1,
   VAL_2,
   VAL_3,
   VAL_4,
   VAL_5,
   VAL_6,
   VAL_7,
   VAL_8,
   VAL_9,
   VAL_10;

   @Override
   public TestEnum getEnum(int value) {
      if (value < 0 || value >= values().length) {
         throw new IllegalArgumentException("no enum matching value of " + value);
      }
      return values()[value];
   }

   @Override
   public int getIntValue() {
      return ordinal();
   }

}
