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

package org.eclipse.osee.ote.message.elements;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;

public abstract class NumericElement<T extends Number & Comparable<T>> extends DiscreteElement<T> {

   public NumericElement(Message msg, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalMsb, int originalLsb) {
      super(msg, elementName, messageData, byteOffset, msb, lsb, originalMsb, originalLsb);
   }

   public NumericElement(Message msg, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(msg, elementName, messageData, byteOffset, msb, lsb);
   }

   public NumericElement(Message msg, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(msg, elementName, messageData, bitOffset, bitLength);
   }

   public abstract long getNumericBitValue();
}
