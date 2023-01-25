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
package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.elements.BooleanElement;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;

public class RunTestsCancel extends OteEventMessage {

   public static String TOPIC = "ote/message/runtests/cancel";
   
   private static int SIZE = 256+1;
   
   public StringElement GUID;
   public BooleanElement CANCEL_ALL;
   
	public RunTestsCancel() {
		super("MESSAGE_META_DATA_STAT", TOPIC, SIZE);
		GUID = new StringElement(this, "GUID", getDefaultMessageData(), 0, 0, OteEventMessage.sizeBytesBits(256));
		CANCEL_ALL = new BooleanElement(this, "CANCEL_ALL", getDefaultMessageData(), GUID.getByteOffset()+GUID.getBitLength()/8, 0, 7);
		
		getHeader().RESPONSE_TOPIC.setValue(BooleanResponse.TOPIC);
	}

   public RunTestsCancel(byte[] bytes) {
      this();
      setData(bytes);
   }

}
