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
import org.eclipse.osee.ote.core.framework.command.RunTests;
import org.eclipse.osee.ote.message.event.SerializedClassMessage;

public class RunTestsSerialized extends SerializedClassMessage<RunTests> {

   public static final String RUNTESTS_NAMESPACE = "ote/message/runtests/";
   public static final String TOPIC = RUNTESTS_NAMESPACE + "serialized";

   public RunTestsSerialized() {
      super(TOPIC);
   }

   public RunTestsSerialized(RunTests commandAdded) throws IOException {
      super(TOPIC, commandAdded);
   }

   public RunTestsSerialized(byte[] bytes) {
      super(bytes);
   }
}