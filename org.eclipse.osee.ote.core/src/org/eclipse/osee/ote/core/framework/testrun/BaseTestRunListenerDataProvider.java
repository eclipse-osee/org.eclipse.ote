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

package org.eclipse.osee.ote.core.framework.testrun;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.framework.event.BaseEvent;
import org.eclipse.osee.ote.core.framework.event.IEventData;

public class BaseTestRunListenerDataProvider implements ITestRunListenerDataProvider {

   public IEventData create(IPropertyStore properties, TestScript test, TestCase testCase) {
      return new BaseEvent(properties, test, testCase);
   }

   @Override
   public IEventData createOnPostRun(IPropertyStore propertyStore, TestScript test) {
      return new BaseEvent(propertyStore, test);
   }

   @Override
   public IEventData createOnPostTestCase(IPropertyStore propertyStore, TestScript test, TestCase testCase) {
      return new BaseEvent(propertyStore, test, testCase);
   }

   @Override
   public IEventData createOnPreRun(IPropertyStore propertyStore, TestScript test) {
      return new BaseEvent(propertyStore, test);
   }

   @Override
   public IEventData createOnPreTestCase(IPropertyStore propertyStore, TestScript test, TestCase testCase) {
      return new BaseEvent(propertyStore, test, testCase);
   }

}
