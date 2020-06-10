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

package org.eclipse.ote.io.mux.test.mocks;

import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.IMessageDataVisitor;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;

/**
 * @author Michael P. Masterson
 */
public class MockMessageData extends MessageData {
   

   private IMessageHeader header;

   public MockMessageData(Message msg, String typeName, String name, int dataByteSize, DataType type) {
      super(typeName, 
            name, 
            dataByteSize + MockMessageHeader.HEADER_BYTE_SIZE,
            MockMessageHeader.HEADER_BYTE_SIZE, 
            type);
      header = new MockMessageHeader(msg, getMem().slice(0,MockMessageHeader.HEADER_BYTE_SIZE));
      initializeDefaultHeaderValues();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.data.MessageData#getMsgHeader()
    */
   @Override
   public IMessageHeader getMsgHeader() {
      // TODO Auto-generated method stub
      return this.header;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.data.MessageData#visit(org.eclipse.osee.ote.message.data.IMessageDataVisitor)
    */
   @Override
   public void visit(IMessageDataVisitor visitor) {
      // TODO Auto-generated method stub

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.data.MessageData#initializeDefaultHeaderValues()
    */
   @Override
   public void initializeDefaultHeaderValues() {
      // TODO Auto-generated method stub

   }

}
