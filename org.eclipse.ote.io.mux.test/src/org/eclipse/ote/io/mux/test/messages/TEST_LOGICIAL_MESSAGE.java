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

package org.eclipse.ote.io.mux.test.messages;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.ote.io.GenericOteIoType;
import org.eclipse.ote.io.mux.test.mocks.MockMessageData;
import org.eclipse.ote.io.mux.test.mocks.TestType;


/**
 * 
 * @author Michael P. Masterson
 */
public class TEST_LOGICIAL_MESSAGE extends Message {
   private static final int BYTE_SIZE = 64;
   private static final int BYTE_OFFSET = 0;
   private static final boolean IS_SCHED = true;
   private static final int PHASE = 0;
   private static final double RATE = 2;

   public StringElement MULTI_MSG_ELEMENT;
   public IntegerElement ONLY_HERE;


   public TEST_LOGICIAL_MESSAGE() {
      super("TEST_LOGICIAL_MESSAGE", BYTE_SIZE, BYTE_OFFSET, IS_SCHED, PHASE, RATE);
      MessageData messageData = new MockMessageData(this, this.getClass().getName(), getName(), getDefaultByteSize(), TestType.TEST);
      setDefaultMessageData(messageData);
      messageData.setScheduled(true);
      int bitSize = 32 * 8 - 1;
      MULTI_MSG_ELEMENT = new StringElement(this, "MULTI_MSG_ELEMENT", messageData, 0, 0, bitSize);
      ONLY_HERE = new IntegerElement(this, "ONLY_HERE", messageData, 32, 0, 32);

      addElements(MULTI_MSG_ELEMENT, ONLY_HERE);

	  //set up message stuff
      setMemSource(TestType.TEST);

   }
   
   @Override
   public void switchElementAssociation( Collection<? extends Message> messages ) {
      MULTI_MSG_ELEMENT = MULTI_MSG_ELEMENT.switchMessages(messages);
      ONLY_HERE = ONLY_HERE.switchMessages(messages);

   }

   @SuppressWarnings("unchecked")
   @Override
   public Map<DataType, Class<? extends Message>[]> getAssociatedMessages() {
      Map<DataType, Class<? extends Message>[]> o = new LinkedHashMap<DataType, Class<? extends Message>[]>();
      o.put(GenericOteIoType.MUX, new Class[]{TEST_MUX_MSG_T.class});
      o.put(GenericOteIoType.MUX, new Class[]{TEST_MUX_MSG_R.class});

      return o;
   }
   
}