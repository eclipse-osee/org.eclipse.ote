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

package org.eclipse.osee.ote.core.environment.status.msg;

import java.io.IOException;

import org.eclipse.osee.ote.core.environment.status.SequentialCommandBegan;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class SequentialCommandBeganMessage extends SerializedClassMessage<SequentialCommandBegan> {

	public static final String EVENT = "ote/status/sequentialCommandBegan";
	
	public SequentialCommandBeganMessage() {
		super(EVENT);
	}

	public SequentialCommandBeganMessage(SequentialCommandBegan seqCmdBegan) throws IOException {
		super(EVENT, seqCmdBegan);
	}
	
	public SequentialCommandBeganMessage(byte[] bytes){
		super(bytes);
	}

}
