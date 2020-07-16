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
import org.eclipse.osee.ote.message.elements.SignedInteger32Element;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
@SuppressWarnings("unused")
public class NonMappingSignedInteger32Element extends SignedInteger32Element {

   public NonMappingSignedInteger32Element(DiscreteElement<Integer> element) {
      super(element.getMessage(), element.getElementName(), element.getMsgData(), element.getByteOffset(),
         element.getMsb(), element.getLsb());
      for (Object obj : element.getElementPath()) {
         this.getElementPath().add(obj);
      }
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
   public void setAndSend(ITestEnvironmentAccessor accessor, Integer value) {
      throwNoMappingElementException();
   }

   public void setNoLog(ITestEnvironmentAccessor accessor, int value) {
      throwNoMappingElementException();
   }

   public Integer waitForValue(ITestEnvironmentAccessor accessor, Integer value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   public Integer waitForNotValue(ITestEnvironmentAccessor accessor, Integer value, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Integer waitForRange(ITestEnvironmentAccessor accessor, Integer minValue, Integer maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Integer waitForRange(ITestEnvironmentAccessor accessor, Integer minValue, boolean minInclusive, Integer maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Integer waitForNotRange(ITestEnvironmentAccessor accessor, Integer minValue, Integer maxValue, int milliseconds) throws InterruptedException {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public Integer waitForNotRange(ITestEnvironmentAccessor accessor, Integer minValue, boolean minInclusive, Integer maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
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
   public Integer getValue() {
      throwNoMappingElementException();
      return 0;
   }

   @Override
   public void setValue(Integer value) {
      throwNoMappingElementException();
   }

   @Override
   public Integer valueOf(MemoryResource mem) {
      throwNoMappingElementException();
      return 0;
   }

}
