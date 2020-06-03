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

import org.eclipse.osee.ote.message.event.SerializedClassMessage;
import org.eclipse.osee.ote.message.tool.SubscriptionDetails;

public class SerializedSubscriptionDetailsMessage extends SerializedClassMessage<SubscriptionDetails> {

	public static final String EVENT = "ote/message/subscribeserial";
	
	public SerializedSubscriptionDetailsMessage() {
		super(EVENT);
	}
	
	public SerializedSubscriptionDetailsMessage(SubscriptionDetails commandAdded) throws IOException {
		super(EVENT, commandAdded);
	}
	
	public SerializedSubscriptionDetailsMessage(byte[] bytes){
		super(bytes);
	}
}
