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

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.UnsignedInteger64Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
@SuppressWarnings("unused")
public class NonMappingUnsignedInteger64Element extends UnsignedInteger64Element {

   /**
    * Copy constructor.
    * @param element 
    */
   public NonMappingUnsignedInteger64Element(UnsignedInteger64Element element) {
      super(element.getMessage(), element.getElementName(), element.getMsgData(), element.getByteOffset(),
         element.getMsb(), element.getLsb());
      for (Object obj : element.getElementPath()) {
         this.getElementPath().add(obj);
      }
   }

   public NonMappingUnsignedInteger64Element(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb);
   }

   @Override
   public String toString(Long obj) {
      throwNoMappingElementException();
      return null;
   }

   @Override
   public void setValue(Long value) {
      throwNoMappingElementException();
   }

   @Override
   public Long getValue() {
      throwNoMappingElementException();
      return 0l;
   }

   public boolean check(ITestAccessor accessor, long value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, long value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, long minValue, long maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, long maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, long minValue, boolean minInclusive, long maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, boolean minInclusive, long maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, long value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, long value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, long minValue, long maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, long maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, long minValue, boolean minInclusive, long maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, boolean minInclusive, long maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, long value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, long value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public void checkPulse(ITestAccessor accessor, long value) throws InterruptedException {

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

   public boolean checkRange(ITestAccessor accessor, long minValue, long maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, long maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, long minValue, boolean minInclusive, long maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, boolean minInclusive, long maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, long value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, long value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, long minValue, long maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, long maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, long minValue, boolean minInclusive, long maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, boolean minInclusive, long maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public int checkMaintain(ITestAccessor accessor, long value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, long value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNot(ITestAccessor accessor, long value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, long value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, long maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, boolean minInclusive, long maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, long minValue, long maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, long minValue, boolean minInclusive, long maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, long maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, long minValue, boolean minInclusive, long maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Long get(ITestEnvironmentAccessor accessor) {
      throwNoMappingElementException();
      return 0l;
   }

   public void set(ITestEnvironmentAccessor accessor, long value) {
      throwNoMappingElementException();
   }

   @Override
   public void setAndSend(ITestEnvironmentAccessor accessor, Long value) {
      throwNoMappingElementException();
   }

   public void setNoLog(ITestEnvironmentAccessor accessor, long value) {
      throwNoMappingElementException();
   }

   public int waitForValue(ITestEnvironmentAccessor accessor, long value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int waitForNotValue(ITestEnvironmentAccessor accessor, long value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Long waitForRange(ITestEnvironmentAccessor accessor, Long minValue, Long maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0l;
   }

   @Override
   public Long waitForRange(ITestEnvironmentAccessor accessor, Long minValue, boolean minInclusive, Long maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0l;
   }

   @Override
   public Long waitForNotRange(ITestEnvironmentAccessor accessor, Long minValue, Long maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0l;
   }

   @Override
   public Long waitForNotRange(ITestEnvironmentAccessor accessor, Long minValue, boolean minInclusive, Long maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0l;
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
