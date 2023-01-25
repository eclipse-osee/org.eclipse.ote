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

import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;

public class GET_INET_ADDRESS_REQ extends OteEventMessage {

   public static String TOPIC = "ote/message/addressreq";
   
   public final EnumeratedElement<SOCKET_ID> SOCKET_ID;   
   
	public GET_INET_ADDRESS_REQ() {
		super("GET_INET_ADDRESS_REQ", TOPIC, 1);
		SOCKET_ID = createEnumeratedElement("SOCKET_ID", 1, SOCKET_ID.class);
		
		getHeader().RESPONSE_TOPIC.setValue(GET_INET_ADDRESS_RESP.TOPIC);
	}
	
}
