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

import org.eclipse.osee.ote.core.environment.status.TestPointUpdate;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class TestPointUpdateMessage extends SerializedClassMessage<TestPointUpdate> {

	public static final String EVENT = "ote/status/testPointUpdate";
	
	public TestPointUpdateMessage() {
		super(EVENT);
	}

	public TestPointUpdateMessage(TestPointUpdate testPointUpdate) throws IOException {
		super(EVENT, testPointUpdate);
	}

	public TestPointUpdateMessage(byte[] bytes){
		super(bytes);
	}
}
