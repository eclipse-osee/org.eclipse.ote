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

import java.util.Collection;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.ote.io.GenericOteIoType;

/**
 * 
 * @author Michael P. Masterson
 */
public class MuxMessage extends Message implements IMuxMessage {
   public MuxMessage(String name, int defaultByteSize, int defaultByteOffset, boolean isScheduled,
         int phase, double rate) {
      super(name, defaultByteSize, defaultByteOffset, isScheduled, phase, rate);
   }

   private MuxData getMuxDataSource() {
      MuxData retVal = (MuxData) getActiveDataSource(GenericOteIoType.MUX);
      return retVal;
      
   }

   @Override
   public MuxReceiveTransmit getTransmitFlag() {
      return getMuxDataSource().getReceiveTransmitFlag();
   }


   @Override
   public short getSubAddress() {
      return (short) getMuxDataSource().getSubaddressNumber();
   }

   @Override
   public short getTerminalNumber() {
      return (short) getMuxDataSource().getRemoteTerminalNumber();
   }

   public short getChannelNumber() {
      return (short) getMuxDataSource().getChannelNumber();
   }

   @Override
   public byte getWordCount() {
      return (byte) getMuxDataSource().getWordCount();
   }

   public void setWordCount(int wc) {
      getMuxDataSource().setWordCount(wc);
   }

   @Override
   public void destroy() {
      if (isWriter()) {
         zeroize();
         send();
      }
      super.destroy();
   }

   /**
    * If the list of associated messages includes both R and T messages, this will only return true if the 
    * current element is a member of a Writer T message or of a Reader R message.  This prevents an element
    * that is intended to be written to from being matched to an element in an R message and vice versa. 
    */
   @Override
   public boolean isValidElement(Element currentElement, Element proposedElement) {
      Message msg = currentElement.getMessage();
      Collection<?> fromMsgAssociations = msg.getMessageTypeAssociation(GenericOteIoType.MUX);
      if (fromMsgAssociations != null && fromMsgAssociations.size() > 1) {
         boolean hasR = false;
         boolean hasT = false;
         for(Object o : fromMsgAssociations) {
            if(o instanceof MuxMessage) {
               MuxMessage sourceMux = (MuxMessage)o;
               if(sourceMux.getTransmitFlag() == MuxReceiveTransmit.TRANSMIT) {
                  hasT = true;
               } else {
                  hasR = true;
               }
            }
         }
         
         if (hasR && hasT) {
            if (msg.isWriter()) {
               if (this.getTransmitFlag() == MuxReceiveTransmit.TRANSMIT) {
                  return true;
               }
               else {
                  return false;
               }
            }
            else {
               if (this.getTransmitFlag() == MuxReceiveTransmit.RECEIVE) {
                  return true;
               }
               else {
                  return false;
               }
            }
         }
      }
      return true;
   }

}
