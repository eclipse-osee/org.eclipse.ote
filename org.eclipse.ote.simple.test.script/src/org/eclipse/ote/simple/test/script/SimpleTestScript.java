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

package org.eclipse.ote.simple.test.script;

import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.jini.ITestEnvironmentCommandCallback;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.ote.simple.io.message.HELLO_WORLD;

/**
 * @author Andy Jury
 */
public class SimpleTestScript extends SimpleTestScriptType {
   
   HELLO_WORLD writer;
   
   public SimpleTestScript(MessageSystemTestEnvironment testEnvironment, ITestEnvironmentCommandCallback callback) {
      super(testEnvironment, callback);
      
      this.writer = getMessageWriter(HELLO_WORLD.class);

      new TestCase1(this);
      new TestCase2(this);
   }

   private class LocalSetupTestCase extends TestCase {

      protected LocalSetupTestCase(TestScript parent) {

         super(parent, false, false);
      }

      public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) {
         // This test case will fail when running in an environment with Mux
         // unless you uncomment the following line to force the message mem type
         // writer.setMemSource(SimpleDataType.SIMPLE);
         prompt("In the LocalSetupTestCase");
         writer.PRINT_ME.set(this, "TEST1");
         testWait(1000);
         writer.PRINT_ME.setNoLog("TEST2");
         testWait(1000);
         writer.PRINT_ME.setNoLog("TEST3");
         testWait(1000);
         writer.PRINT_ME.setNoLog("TEST4");
         writer.ONLY_IN_SIMPLE.set(this, 64);
         writer.send();
         testWait(1000);
      }
   }

   protected TestCase getSetupTestCase() {

      return new LocalSetupTestCase(this);
   }

   public class TestCase1 extends TestCase {

      public TestCase1(TestScript parent) {

         super(parent);
      }

      public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) {
         prompt("In TestCase1");
         promptPause("In TestCase1");
         promptPassFail("Pass/Fail?");
      }
   }

   public class TestCase2 extends TestCase {

      public TestCase2(TestScript parent) {

         super(parent);
      }

      public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger) {
         prompt("In TestCase2");
         promptPause("In TestCase2");
         promptPassFail("Pass/Fail?");
      }
   }
}
