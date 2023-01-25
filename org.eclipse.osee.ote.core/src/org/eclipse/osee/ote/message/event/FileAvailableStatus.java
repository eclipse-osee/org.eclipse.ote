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
package org.eclipse.osee.ote.message.event;

import org.eclipse.osee.ote.message.elements.BooleanElement;
import org.eclipse.osee.ote.message.elements.StringElement;

public class FileAvailableStatus extends OteEventMessage{
   public static final int _BYTE_SIZE = 251;
   public static final String _TOPIC = "ote/server/FileAvailableStatus";
   public static final int _MESSAGE_ID = -1;

   public final StringElement FILE;
   public final BooleanElement EXISTS;

   public FileAvailableStatus() {
      super(FileAvailableRequest.class.getSimpleName(), _TOPIC, _BYTE_SIZE);

      int currentOffset = 0;
      int bitLength;
      FILE = new StringElement(this, "FILE", getDefaultMessageData(), currentOffset, bitLength=(8*250)); currentOffset+=bitLength;
      EXISTS = new BooleanElement(this, "EXISTS", getDefaultMessageData(), currentOffset, bitLength=1); currentOffset+=bitLength;

      if (currentOffset > _BYTE_SIZE*8) {
         throw new IllegalStateException("Total size of elements exceeds defined message size");
      }
   }

}
