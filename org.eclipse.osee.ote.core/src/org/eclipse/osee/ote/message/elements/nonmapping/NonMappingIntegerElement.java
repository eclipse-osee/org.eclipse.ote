/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Andy Jury
 */
@SuppressWarnings("unused")
public class NonMappingIntegerElement extends IntegerElement {

   /**
    * Copy constructor.
    * @param element 
    */
   public NonMappingIntegerElement(IntegerElement element) {
      super(element.getMessage(), element.getElementName(), element.getMsgData(), element.getByteOffset(),
         element.getMsb(), element.getLsb());
      for (Object obj : element.getElementPath()) {
         this.getElementPath().add(obj);
      }
   }

   public NonMappingIntegerElement(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb);
   }

   public NonMappingIntegerElement(Message message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   public NonMappingIntegerElement(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   @Override
   public String toString(Integer obj) {
      throwNoMappingElementException();
      return null;
   }

   @Override
   public void setValue(Integer value) {
      throwNoMappingElementException();
   }

   @Override
   public Integer getValue() {
      throwNoMappingElementException();
      return 0;
   }
   
   public boolean check(ITestAccessor accessor, int value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, int value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, int minValue, int maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, int value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, int value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, int minValue, int maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public void checkPulse(ITestAccessor accessor, int value) throws InterruptedException {

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

   public boolean checkRange(ITestAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public int checkMaintain(ITestAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNot(ITestAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Integer get(ITestEnvironmentAccessor accessor) {
      throwNoMappingElementException();
      return 0;
   }

   public void set(ITestEnvironmentAccessor accessor, int value) {
      throwNoMappingElementException();
   }

   @Override
   public void setAndSend(ITestEnvironmentAccessor accessor, int value) {
      throwNoMappingElementException();
   }

   public void setNoLog(ITestEnvironmentAccessor accessor, int value) {
      throwNoMappingElementException();
   }

   public int waitForValue(ITestEnvironmentAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int waitForNotValue(ITestEnvironmentAccessor accessor, int value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public int waitForRange(ITestEnvironmentAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public int waitForRange(ITestEnvironmentAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public int waitForNotRange(ITestEnvironmentAccessor accessor, int minValue, int maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public int waitForNotRange(ITestEnvironmentAccessor accessor, int minValue, boolean minInclusive, int maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
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
