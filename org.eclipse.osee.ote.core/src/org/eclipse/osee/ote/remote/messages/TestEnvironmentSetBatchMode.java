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

import org.eclipse.osee.ote.message.elements.BooleanElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;

public class TestEnvironmentSetBatchMode extends OteEventMessage {

   public static String TOPIC = "ote/message/setbatchmode";
   
   private static int SIZE = 1;
   
   public BooleanElement SET_BATCH_MODE;
   
	public TestEnvironmentSetBatchMode() {
		super("TestEnvironmentSetBatchMode", TOPIC, SIZE);
		SET_BATCH_MODE = new BooleanElement(this, "SET_BATCH_MODE", getDefaultMessageData(), 0, 0, 7);
	}

   public TestEnvironmentSetBatchMode(byte[] bytes) {
      this();
      setBackingBuffer(bytes);
   }

}
