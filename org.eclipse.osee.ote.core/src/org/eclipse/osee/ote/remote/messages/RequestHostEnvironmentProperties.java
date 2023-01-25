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

import org.eclipse.osee.ote.message.event.OteEventMessage;

public class RequestHostEnvironmentProperties extends OteEventMessage {

   public static String TOPIC = "ote/message/propertiesreq";
   
	public RequestHostEnvironmentProperties() {
		super("RequestHostEnvironmentProperties", TOPIC, 1);
		
		getHeader().RESPONSE_TOPIC.setValue(SerializedEnhancedProperties.EVENT);
	}
	
}
