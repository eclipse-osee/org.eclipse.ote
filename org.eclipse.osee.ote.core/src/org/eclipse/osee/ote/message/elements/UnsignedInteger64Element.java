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

package org.eclipse.osee.ote.message.elements;

import java.math.BigInteger;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;

/**
 * @author Michael P. Masterson
 */
public class UnsignedInteger64Element extends NumericElement<BigInteger> {

   /**
    * @param msg
    * @param elementName
    * @param messageData
    * @param byteOffset
    * @param msb must be 0
    * @param lsb must be 63
    */
   public UnsignedInteger64Element(Message msg, String elementName, MessageData messageData,
         int byteOffset, int msb) {
      super(msg, elementName, messageData, byteOffset, msb, msb + 63);
   }

   @Override
   public long getNumericBitValue() {
      return getValue().longValue();
   }

   @Override
   public void setValue(BigInteger obj) {
      getMsgData().getMem().setBigInt(obj, byteOffset, msb, lsb);
   }

   @Override
   public BigInteger getValue() {
      return getMsgData().getMem().getUnsigned64(byteOffset, msb, lsb);
   }

   @Override
   public String toString(BigInteger obj) {
      return obj.toString();
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      this.set(accessor, new BigInteger(value));

   }

   @Override
   public BigInteger valueOf(MemoryResource mem) {
      return mem.getUnsigned64(byteOffset, msb, lsb);
   }

   @Override
   public BigInteger elementMask(BigInteger value) {
      return value;
   }

   @Override
   protected Element getNonMappingElement() {
      return this;
   }
   
    public void setLong(long value) {
       this.setValue(BigInteger.valueOf(value));
    }
    
    public void setNoLog(long value) {
       setValue(BigInteger.valueOf(value));
    }

}
