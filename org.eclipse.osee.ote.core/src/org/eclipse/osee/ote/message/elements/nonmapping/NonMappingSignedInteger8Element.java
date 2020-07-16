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
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.SignedInteger8Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
@SuppressWarnings("unused")
public class NonMappingSignedInteger8Element extends SignedInteger8Element {

   public NonMappingSignedInteger8Element(DiscreteElement<Byte> element) {
      super(element.getMessage(), element.getElementName(), element.getMsgData(), element.getByteOffset(),
         element.getMsb(), element.getLsb());
      for (Object obj : element.getElementPath()) {
         this.getElementPath().add(obj);
      }
   }

   public boolean check(ITestAccessor accessor, byte value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, byte value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, byte minValue, byte maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, byte maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, byte minValue, boolean minInclusive, byte maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, boolean minInclusive, byte maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

      public boolean checkNot(ITestAccessor accessor, byte value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, byte value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, byte minValue, byte maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, byte maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, byte minValue, boolean minInclusive, byte maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, boolean minInclusive, byte maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, byte value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, byte value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public void checkPulse(ITestAccessor accessor, byte value) throws InterruptedException {

      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, byte pulsedValue, byte nonPulsedValue) throws InterruptedException {
      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, CheckGroup checkGroup, byte pulsedValue, int nonPulsedValue) throws InterruptedException {
      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, byte pulsedValue, byte nonPulsedValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, CheckGroup checkGroup, byte pulsedValue, byte nonPulsedValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
   }

   public boolean checkRange(ITestAccessor accessor, byte minValue, byte maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, byte maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, byte minValue, boolean minInclusive, byte maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, boolean minInclusive, byte maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, byte value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, byte value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, byte minValue, byte maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, byte maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, byte minValue, boolean minInclusive, byte maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, boolean minInclusive, byte maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public int checkMaintain(ITestAccessor accessor, byte value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, byte value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNot(ITestAccessor accessor, byte value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, byte value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, byte maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, boolean minInclusive, byte maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, byte minValue, byte maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, byte minValue, boolean minInclusive, byte maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, byte maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, byte minValue, boolean minInclusive, byte maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Byte get(ITestEnvironmentAccessor accessor) {
      throwNoMappingElementException();
      return 0;
   }

   public void set(ITestEnvironmentAccessor accessor, byte value) {
      throwNoMappingElementException();
   }

   @Override
   public void setAndSend(ITestEnvironmentAccessor accessor, Byte value) {
      throwNoMappingElementException();
   }

   public void setNoLog(ITestEnvironmentAccessor accessor, byte value) {
      throwNoMappingElementException();
   }

   public Byte waitForValue(ITestEnvironmentAccessor accessor, Byte value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public Byte waitForNotValue(ITestEnvironmentAccessor accessor, Byte value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Byte waitForRange(ITestEnvironmentAccessor accessor, Byte minValue, Byte maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Byte waitForRange(ITestEnvironmentAccessor accessor, Byte minValue, boolean minInclusive, Byte maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Byte waitForNotRange(ITestEnvironmentAccessor accessor, Byte minValue, Byte maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Byte waitForNotRange(ITestEnvironmentAccessor accessor, Byte minValue, boolean minInclusive, Byte maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
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

   @Override
   public Byte getValue() {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public void setValue(Byte value) {
      throwNoMappingElementException();
   }

   @Override
   public Byte valueOf(MemoryResource mem) {
      throwNoMappingElementException();
      return 0;
   }

}
