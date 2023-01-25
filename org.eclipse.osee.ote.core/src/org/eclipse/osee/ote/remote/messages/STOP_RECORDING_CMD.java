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

public class STOP_RECORDING_CMD extends OteEventMessage {

   public static String TOPIC = "ote/message/recordstop";
   
	public STOP_RECORDING_CMD() {
		super("STOP_RECORDING_CMD", TOPIC, 0);
	}

}
