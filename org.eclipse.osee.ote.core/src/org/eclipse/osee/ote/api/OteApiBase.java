/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ote.api;

import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.ote.core.environment.OteApi;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * @author Michael P. Masterson
 */
public class OteApiBase implements OteApi {

   private TestEnvironment testEnv;
   private LoggerApiBase loggerApi;

   @Override
   public void logTestPoint(boolean isPassed, String testPointName, String expected, String actual) {
      testEnv.getRunManager().getCurrentScript().logTestPoint(isPassed, testPointName, expected, actual);
   }

   @Override
   public void logTestPoint(boolean isPassed, String testPointName, NamedId expected, NamedId actual) {
      logTestPoint(isPassed, testPointName, expected.getName(), actual.getName());
   }
   
   @Override
   public void prompt(String promptMessage) {
      testEnv.prompt(promptMessage);
   }

   @Override
   public String promptInput(String promptMessage) {
      return testEnv.promptInput(promptMessage);
   }

   @Override
   public void promptPause(String promptMessage) {
      testEnv.promptPause(promptMessage);
   }

   @Override
   public void promptPassFail(String promptMessage) {
      testEnv.promptPassFail(promptMessage);
   }

   @Override
   public void testWait(int milliseconds) {
      testEnv.testWait(milliseconds);
   }

   public void zzz_bindTestEnv(TestEnvironment testEnv) {
      this.testEnv = testEnv;
      this.loggerApi = new LoggerApiBase(testEnv);
   }
   
   protected TestEnvironment getTestEnv() {
      return testEnv;
   }
   
   @Override
   public LoggerApiBase logger() {
      return loggerApi;
   }
   
}
