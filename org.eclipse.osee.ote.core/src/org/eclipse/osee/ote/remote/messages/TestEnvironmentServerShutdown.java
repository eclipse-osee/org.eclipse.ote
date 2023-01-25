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

import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;

public class TestEnvironmentServerShutdown extends OteEventMessage {

   public static String TOPIC = "ote/message/servershutdown";
   
   private static int SIZE = 512;
   
   public StringElement SERVER_ID;
   
	public TestEnvironmentServerShutdown() {
		super("TestEnvironmentSetBatchMode", TOPIC, SIZE);
		SERVER_ID = new StringElement(this, "SERVER_ID", getDefaultMessageData(), 0, 0, 512*8-1);
	}

   public TestEnvironmentServerShutdown(byte[] bytes) {
      this();
      setBackingBuffer(bytes);
   }

}
