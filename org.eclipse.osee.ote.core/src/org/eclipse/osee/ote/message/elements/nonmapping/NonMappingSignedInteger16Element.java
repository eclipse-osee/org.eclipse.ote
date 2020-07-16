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
import org.eclipse.osee.ote.message.elements.SignedInteger16Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
@SuppressWarnings("unused")
public class NonMappingSignedInteger16Element extends SignedInteger16Element {

   public NonMappingSignedInteger16Element(DiscreteElement<Short> element) {
      super(element.getMessage(), element.getElementName(), element.getMsgData(), element.getByteOffset(),
         element.getMsb(), element.getLsb());
      for (Object obj : element.getElementPath()) {
         this.getElementPath().add(obj);
      }
   }

   public boolean check(ITestAccessor accessor, short value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, short value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, short minValue, short maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, short maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

      public boolean checkNot(ITestAccessor accessor, short value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, short value) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, short minValue, short maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, short maxValue) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive) {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, short value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, short value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public void checkPulse(ITestAccessor accessor, short value) throws InterruptedException {

      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, short pulsedValue, short nonPulsedValue) throws InterruptedException {
      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, CheckGroup checkGroup, short pulsedValue, int nonPulsedValue) throws InterruptedException {
      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, short pulsedValue, short nonPulsedValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
   }

   public void checkPulse(ITestAccessor accessor, CheckGroup checkGroup, short pulsedValue, short nonPulsedValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
   }

   public boolean checkRange(ITestAccessor accessor, short minValue, short maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, short maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, short value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, short value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, short minValue, short maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, short maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return false;
   }

   public int checkMaintain(ITestAccessor accessor, short value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, short value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNot(ITestAccessor accessor, short value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, short value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, short maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, short minValue, short maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainRange(ITestAccessor accessor, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, short maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public int checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, short minValue, boolean minInclusive, short maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Short get(ITestEnvironmentAccessor accessor) {
      throwNoMappingElementException();
      return 0;
   }

   public void set(ITestEnvironmentAccessor accessor, short value) {
      throwNoMappingElementException();
   }

   @Override
   public void setAndSend(ITestEnvironmentAccessor accessor, Short value) {
      throwNoMappingElementException();
   }

   public void setNoLog(ITestEnvironmentAccessor accessor, short value) {
      throwNoMappingElementException();
   }

   public Short waitForValue(ITestEnvironmentAccessor accessor, Short value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public Short waitForNotValue(ITestEnvironmentAccessor accessor, Short value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Short waitForRange(ITestEnvironmentAccessor accessor, Short minValue, Short maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Short waitForRange(ITestEnvironmentAccessor accessor, Short minValue, boolean minInclusive, Short maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Short waitForNotRange(ITestEnvironmentAccessor accessor, Short minValue, Short maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Short waitForNotRange(ITestEnvironmentAccessor accessor, Short minValue, boolean minInclusive, Short maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
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
   public Short getValue() {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public void setValue(Short value) {
      throwNoMappingElementException();
   }

   @Override
   public Short valueOf(MemoryResource mem) {
      throwNoMappingElementException();
      return 0;
   }

}
