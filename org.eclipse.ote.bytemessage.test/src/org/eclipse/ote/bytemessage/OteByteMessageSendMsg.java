/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.bytemessage;

import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.ote.bytemessage.OteByteMessage;


public class OteByteMessageSendMsg  extends OteByteMessage {

   public static final String TOPIC_VALUE = "ote/send";
   
   public StringElement TOPIC;
   public IntegerElement CODE;

   public OteByteMessageSendMsg() {
      super("send", TOPIC_VALUE, 0, 0);
      TOPIC = new StringElement(this, "TOPIC", getDefaultMessageData(), 0, 0, 8*64);
      CODE = new IntegerElement(this, "MESSAGE_ID", getDefaultMessageData(), 64, 0, 31);
   }

}
