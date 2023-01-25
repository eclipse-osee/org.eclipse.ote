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

import java.io.IOException;

import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class SerializedRequestRemoteTestEnvironment  extends SerializedClassMessage<RequestRemoteTestEnvironment> {

   public static final String TOPIC = "ote/message/serialrequesttestenv";

   public SerializedRequestRemoteTestEnvironment() {
      super(TOPIC);
      
      getHeader().RESPONSE_TOPIC.setValue(SerializedConnectionRequestResult.EVENT);
   }
   
   public SerializedRequestRemoteTestEnvironment(RequestRemoteTestEnvironment commandAdded) throws IOException {
      super(TOPIC, commandAdded);
   }
   
   public SerializedRequestRemoteTestEnvironment(byte[] bytes){
      super(bytes);
   }
}
