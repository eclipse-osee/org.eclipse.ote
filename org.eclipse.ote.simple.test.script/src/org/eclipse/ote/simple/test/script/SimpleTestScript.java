/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.ote.simple.test.script;

import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.enums.ScriptTypeEnum;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.jini.ITestEnvironmentCommandCallback;
import org.eclipse.ote.simple.test.environment.SimpleTestEnvironment;

/**
 * @author Andy Jury
 */
public class SimpleTestScript extends TestScript {



   public SimpleTestScript(SimpleTestEnvironment testEnvironment, ITestEnvironmentCommandCallback callback) {

      super(testEnvironment, null, ScriptTypeEnum.FUNCTIONAL_TEST, true);


      new TestCase1(this);
      new TestCase2(this);
   }

   private class LocalSetupTestCase extends TestCase {

      protected LocalSetupTestCase(TestScript parent) {

         super(parent, false, false);
      }

      public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) throws InterruptedException {
         prompt("In the LocalSetupTestCase");
      }
   }

   protected TestCase getSetupTestCase() {

      return new LocalSetupTestCase(this);
   }

   public class TestCase1 extends TestCase {

      public TestCase1(TestScript parent) {

         super(parent);
      }

      public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) throws InterruptedException {
         prompt("In TestCase1");
         promptPause("In TestCase1");
         promptPassFail("Pass/Fail?");
      }
   }

   public class TestCase2 extends TestCase {

      public TestCase2(TestScript parent) {

         super(parent);
      }

      public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) throws InterruptedException {
         prompt("In TestCase2");
         promptPause("In TestCase2");
         promptPassFail("Pass/Fail?");
      }
   }
}
