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
package org.eclipse.ote.io.mux;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.OteMessage;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

/**
 * @author Michael P. Masterson
 * @param <M> Concrete MuxMessage type that this class is wrapping
 */
public class OteMuxMessage<M extends MuxMessage> extends OteMessage<M> {

   public OteMuxMessage(Class<M> sourceMessageClass, IMessageRequestor<Message> requestor) {
      super(sourceMessageClass, requestor);
   }

   public MuxReceiveTransmit getTransmitFlag() {
      return getMessageToRead().getTransmitFlag();
   }

   public short getSubAddress() {
      return getMessageToRead().getSubAddress();
   }

   public short getTerminalNumber() {
      return getMessageToRead().getTerminalNumber();
   }

   public short getChannelNumber() {
      return getMessageToRead().getChannelNumber();
   }

   public byte getWordCount() {
      return getMessageToRead().getWordCount();
   }

   public void setWordCount(int wc) {
      getMessageToWrite().setWordCount(wc);
   }
}
