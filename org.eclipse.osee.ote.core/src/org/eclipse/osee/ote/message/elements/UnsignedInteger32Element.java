/*********************************************************************
 * Copyright (c) 2020 Boeing
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

/**
 * @author Michael P. Masterson
 */
public class UnsignedInteger32Element extends UnsignedLongIntegerElement {

   public UnsignedInteger32Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb) {
      super(message, elementName, messageData, byteOffset, msb, msb + 31);
   }

}
