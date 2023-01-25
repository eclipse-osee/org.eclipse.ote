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
import org.eclipse.osee.ote.message.elements.Float64Element;
import org.eclipse.osee.ote.message.elements.RealElement;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;

public class MESSAGE_META_DATA_STAT extends OteEventMessage {

   public static String TOPIC = "ote/message/metadatastat";
   
   private static int SIZE = 256+4+1;
   
   public StringElement MESSAGE;
   public BooleanElement IS_SCHEDULED;
   public RealElement RATE;
   
	public MESSAGE_META_DATA_STAT() {
		super("MESSAGE_META_DATA_STAT", TOPIC, SIZE);
		MESSAGE = new StringElement(this, "MESSAGE", getDefaultMessageData(), 0, 0, OteEventMessage.sizeBytesBits(256));
		IS_SCHEDULED = new BooleanElement(this, "IS_SCHEDULED", getDefaultMessageData(), MESSAGE.getByteOffset()+MESSAGE.getBitLength()/8, 0, 7);
		RATE = new Float64Element(this, "IS_WRITER", getDefaultMessageData(), IS_SCHEDULED.getByteOffset()+IS_SCHEDULED.getBitLength()/8, 0, 63);
	}

}
