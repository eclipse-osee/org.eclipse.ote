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

package org.eclipse.osee.ote.message.elements.nonmapping;

import java.math.BigInteger;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.UnsignedBigInteger64Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
@SuppressWarnings("unused")
public class NonMappingUnsignedBigIntegerElement extends UnsignedBigInteger64Element {

   /**
    * Copy constructor.
    * @param element 
    */
   public NonMappingUnsignedBigIntegerElement(UnsignedBigInteger64Element element) {
      super(element.getMessage(), element.getElementName(), element.getMsgData(), element.getByteOffset(),
         element.getMsb(), element.getLsb());
      for (Object obj : element.getElementPath()) {
         this.getElementPath().add(obj);
      }
   }

   public NonMappingUnsignedBigIntegerElement(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb);
   }

   @Override
   public String toString(BigInteger obj) {
      throwNoMappingElementException();
      return null;
   }

   @Override
   public void setValue(BigInteger value) {
      throwNoMappingElementException();
   }

   @Override
   public BigInteger getValue() {
      throwNoMappingElementException();
      return BigInteger.ZERO;
   }
   
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, BigInteger value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, BigInteger minValue, boolean minInclusive, BigInteger maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }


   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, BigInteger value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, BigInteger value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public void checkPulse(ITestAccessor accessor, BigInteger value) throws InterruptedException {

      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, int pulsedValue, int nonPulsedValue) throws InterruptedException {
      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, CheckGroup checkGroup, int pulsedValue, int nonPulsedValue) throws InterruptedException {
      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, int pulsedValue, int nonPulsedValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, CheckGroup checkGroup, int pulsedValue, int nonPulsedValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, BigInteger minValue, boolean minInclusive, BigInteger maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, BigInteger value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, BigInteger minValue, boolean minInclusive, BigInteger maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public BigInteger checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, BigInteger value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return BigInteger.ZERO;
   }

   public BigInteger checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, BigInteger minValue, boolean minInclusive, BigInteger maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return BigInteger.ZERO;
   }

   public BigInteger checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, BigInteger minValue, boolean minInclusive, BigInteger maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return BigInteger.ZERO;
   }

   @Override
   public BigInteger get(ITestEnvironmentAccessor accessor) {
      throwNoMappingElementException();
      return BigInteger.ZERO;
   }

   public void set(ITestEnvironmentAccessor accessor, BigInteger value) {
      throwNoMappingElementException();
   }

   @Override
   public void setAndSend(ITestEnvironmentAccessor accessor, BigInteger value) {
      throwNoMappingElementException();
   }

   public void setNoLog(ITestEnvironmentAccessor accessor, BigInteger value) {
      throwNoMappingElementException();
   }

   public BigInteger waitForValue(ITestEnvironmentAccessor accessor, BigInteger value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return BigInteger.ZERO;
   }

   public BigInteger waitForNotValue(ITestEnvironmentAccessor accessor, BigInteger value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return BigInteger.ZERO;
   }

   @Override
   public BigInteger waitForRange(ITestEnvironmentAccessor accessor, BigInteger minValue, BigInteger maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return BigInteger.ZERO;
   }

   @Override
   public BigInteger waitForRange(ITestEnvironmentAccessor accessor, BigInteger minValue, boolean minInclusive, BigInteger maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return BigInteger.ZERO;
   }

   @Override
   public BigInteger waitForNotRange(ITestEnvironmentAccessor accessor, BigInteger minValue, BigInteger maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return BigInteger.ZERO;
   }

   @Override
   public BigInteger waitForNotRange(ITestEnvironmentAccessor accessor, BigInteger minValue, boolean minInclusive, BigInteger maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return BigInteger.ZERO;
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      throwNoMappingElementException();
   }

   @Override
   public boolean isNonMappingElement() {
      return true;
   }
}
