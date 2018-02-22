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
