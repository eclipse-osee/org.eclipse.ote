/*********************************************************************
* Copyright (c) 2020 Boeing
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

package org.eclipse.ote.simple.io;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;

/**
 * @author Michael P. Masterson
 */
public class SimpleMessageType extends Message<MessageSystemTestEnvironment, SimpleMessageData, SimpleMessageType>{

   public SimpleMessageType(String name, int defaultByteSize, int defaultOffset, boolean isScheduled, int phase,
      double rate) {
      super(name, defaultByteSize, defaultOffset, isScheduled, phase, rate);
   }

}
