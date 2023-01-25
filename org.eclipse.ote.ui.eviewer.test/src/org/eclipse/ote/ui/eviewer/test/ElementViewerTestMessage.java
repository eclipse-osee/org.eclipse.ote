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
package org.eclipse.ote.ui.eviewer.test;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;

public class ElementViewerTestMessage extends Message {

   public static final int _BYTE_SIZE = 16;
   public static final int _MESSAGE_ID = 1;

   public ElementViewerTestMessage() {
      super(ElementViewerTestMessage.class.getSimpleName(), _BYTE_SIZE, 0, true, 0, 0);
      byte[] data = "test data".getBytes();
      MessageData messageData = new ElementViewerTestMessageData(data, data.length, 0);
   }
   
}
