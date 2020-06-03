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

package org.eclipse.osee.ote.remote.messages;

import java.io.IOException;

import org.eclipse.osee.ote.message.commands.UnSubscribeToMessage;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class SerializedUnSubscribeMessage extends SerializedClassMessage<UnSubscribeToMessage> {

	public static final String EVENT = "ote/message/unsubscribeserial";
	
	public SerializedUnSubscribeMessage() {
		super(EVENT);
	}
	
	public SerializedUnSubscribeMessage(UnSubscribeToMessage commandAdded) throws IOException {
		super(EVENT, commandAdded);
	}
	
	public SerializedUnSubscribeMessage(byte[] bytes){
		super(bytes);
	}
}
