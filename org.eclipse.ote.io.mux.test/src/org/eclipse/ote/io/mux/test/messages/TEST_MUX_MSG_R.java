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

package org.eclipse.ote.io.mux.test.messages;

import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.ote.io.GenericOteIoType;
import org.eclipse.ote.io.mux.MuxData;
import org.eclipse.ote.io.mux.MuxMessage;
import org.eclipse.ote.io.mux.MuxReceiveTransmit;

/**
 * @author Michael P. Masterson
 */
public class TEST_MUX_MSG_R extends MuxMessage {

   private static final int CHANNEL = 1;
   private static final int TERMINAL = 22;
   private static final int SUBADDRESS = 4;
   private static final int BYTE_SIZE = 32;
   private static final int BYTE_OFFSET = 0;
   private static final boolean IS_SCHED = false;
   private static final int PHASE = 0;
   private static final double RATE = 2;

   public StringElement MULTI_MSG_ELEMENT;


   public TEST_MUX_MSG_R() {
      super("TEST_MUX_MSG_1", BYTE_SIZE, BYTE_OFFSET, IS_SCHED, PHASE, RATE);
      MuxData messageData = new MuxData(this, this.getClass().getName(), getName(), getDefaultByteSize(), CHANNEL, TERMINAL, MuxReceiveTransmit.RECEIVE, SUBADDRESS, GenericOteIoType.MUX);
      setDefaultMessageData(messageData);
      messageData.setScheduled(true);
      int bitSize = 32 * 8 - 1;
      MULTI_MSG_ELEMENT = new StringElement(this, "MULTI_MSG_ELEMENT", messageData, 0, 0, bitSize);
      addElements(MULTI_MSG_ELEMENT);

      //set up message stuff
      setMemSource(GenericOteIoType.MUX);

   }

}
