/*********************************************************************
 * Copyright (c) 2024 Boeing
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ote.message.mock;

import org.eclipse.osee.ote.message.OteMessage;

public class MockTestOteMessage extends OteMessage<TestMessage> {
   TestMessage message = new TestMessage();

   public MockTestOteMessage() {
      super(TestMessage.class, null);
   }

   @Override
   public TestMessage getMessageToWrite() {
      return message;
   }

   @Override
   public TestMessage getMessageToRead() {
      return message;
   }

}
