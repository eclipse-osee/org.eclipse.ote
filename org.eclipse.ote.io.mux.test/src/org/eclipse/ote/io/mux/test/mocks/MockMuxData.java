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

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.IMessageDataVisitor;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.ote.io.mux.MuxData;
import org.eclipse.ote.io.mux.MuxReceiveTransmit;

/**
 * @author Michael P. Masterson
 */
public class MockMuxData extends MuxData {

   /**
    * @param msg
    * @param typeName
    * @param name
    * @param dataByteSize
    * @param channelNumber
    * @param remoteTerminalNumber
    * @param receiveTransmitFlag
    * @param subaddressNumber
    * @param type
    */
   public MockMuxData(Message msg, String typeName, String name, int dataByteSize,
         int channelNumber, int remoteTerminalNumber, MuxReceiveTransmit receiveTransmitFlag,
         int subaddressNumber, DataType type) {
      super(msg, typeName, name, dataByteSize, channelNumber, remoteTerminalNumber,
            receiveTransmitFlag, subaddressNumber, type);
      // TODO Auto-generated constructor stub
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.data.MessageData#visit(org.eclipse.osee.ote.message.data.IMessageDataVisitor)
    */
   @Override
   public void visit(IMessageDataVisitor visitor) {
      // Do Nothing
   }

}
