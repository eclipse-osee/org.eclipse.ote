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

import org.eclipse.osee.ote.core.framework.command.ITestCommandResult;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class RunTestsGetCommandResultResp extends SerializedClassMessage<ITestCommandResult> {

	public static final String TOPIC = "ote/message/runtests/commandresultresp";
	
	public RunTestsGetCommandResultResp() {
		super(TOPIC);
	}
	
	public RunTestsGetCommandResultResp(ITestCommandResult commandAdded) throws IOException {
		super(TOPIC, commandAdded);
	}
	
	public RunTestsGetCommandResultResp(byte[] bytes){
		super(bytes);
	}
}
