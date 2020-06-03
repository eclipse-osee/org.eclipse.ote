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

package org.eclipse.osee.ote.core.framework.event;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;

public class BaseEvent implements IEventData {

   private final IPropertyStore propertyStore;
   private final TestScript test;
   private final TestCase testCase;

   public BaseEvent(IPropertyStore propertyStore, TestScript test) {
      this.propertyStore = propertyStore;
      this.test = test;
      this.testCase = null;
   }

   public BaseEvent(IPropertyStore propertyStore, TestScript test, TestCase testCase) {
      this.propertyStore = propertyStore;
      this.test = test;
      this.testCase = testCase;
   }

   public BaseEvent(TestScript test, TestCase testCase) {
      this.propertyStore = null;
      this.test = test;
      this.testCase = testCase;
   }

   public BaseEvent(TestScript test) {
      this.test = test;
      this.testCase = null;
      this.propertyStore = null;
   }

   @Override
   public TestScript getTest() {
      return test;
   }

   @Override
   public TestCase getTestCase() {
      return testCase;
   }

   @Override
   public IPropertyStore getProperties() {
      return propertyStore;
   }

   public String getScriptClass() {
      return propertyStore.get("classname");
   }
}
