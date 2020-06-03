/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.ote.bytemessage;

import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.ote.bytemessage.OteByteMessage;


public class OteByteMessageResponse  extends OteByteMessage {

   public static final String TOPIC_VALUE = "ote/response";
   
   public StringElement TOPIC;
   public IntegerElement CODE;

   public OteByteMessageResponse() {
      super("response", TOPIC_VALUE, 0, 0);
      TOPIC = new StringElement(this, "TOPIC", getDefaultMessageData(), 0, 0, 8*64);
      CODE = new IntegerElement(this, "MESSAGE_ID", getDefaultMessageData(), 64, 0, 31);
   }

}
