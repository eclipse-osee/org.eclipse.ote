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

package org.eclipse.ote.simple.test.environment.listener;

import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.OteLevel;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.ITestLifecycleListener;
import org.eclipse.osee.ote.core.framework.MethodResultImpl;
import org.eclipse.osee.ote.core.framework.ReturnCode;
import org.eclipse.osee.ote.core.framework.event.IEventData;
import org.eclipse.osee.ote.core.framework.outfile.xml.SystemInfo;
import org.eclipse.osee.ote.core.framework.outfile.xml.TestPointResults;
import org.eclipse.osee.ote.core.framework.outfile.xml.TimeSummary;
import org.eclipse.ote.simple.test.environment.SimpleTestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public final class SimpleTestLifeCycleListener implements ITestLifecycleListener {

   private Date startTime;

   public SimpleTestLifeCycleListener() {
   }

   @Override
   public IMethodResult postDispose(IEventData eventData, TestEnvironment env) {
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult postInstantiation(IEventData eventData, TestEnvironment env) {
      eventData.getTest().addTestRunListener(new SimpleTestRunListener(env));
      eventData.getTest().addScriptSummary(env.getRuntimeManager());
      startTime = new Date();
      return new MethodResultImpl(ReturnCode.OK);
   }

   /**
    * The contract we're assuming is that preDispose is too late for messaging to still be done after the conclusion of
    * the script running. To do that use postRun from ITestRunListener.
    */

   @SuppressWarnings("deprecation")
   @Override
   public IMethodResult preDispose(IEventData eventData, TestEnvironment env) {
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);

      try {
         addTimeSummaryToScript(eventData.getTest());

         addTestPointSummaryToScriptLog(eventData.getTest());

         addSystemInfoToScriptLog(eventData.getTest());

         eventData.getTest().getLogger().log(eventData.getTest().getScriptResultRecord());

         OseeLog.log(SimpleTestEnvironment.class, OteLevel.TEST_EVENT,
            String.format("%s Pass[%d] Fail[%d] Aborted[%b]", eventData.getTest().getClass().getSimpleName(),
               eventData.getTest().getPasses(), eventData.getTest().getFails(), eventData.getTest().isAborted()));

         env.onScriptComplete();
      } catch (InterruptedException ex) {
         result = new MethodResultImpl(ReturnCode.ERROR);
         result.addStatus(new BaseStatus(SimpleTestEnvironment.class.getName(), Level.SEVERE, ex));
      }

      return result;
   }

   private void addTimeSummaryToScript(TestScript test) {
      final Date endTime = new Date();
      final long elapsedTime = endTime.getTime() - startTime.getTime();
      long seconds = elapsedTime / 1000;
      long minutes = seconds / 60;
      seconds = seconds % 60;
      long hours = minutes / 60;
      minutes = minutes % 60;
      final String elapsed = String.format("%d:%02d:%02d", hours, minutes, seconds);

      TimeSummary timeSummary = new TimeSummary(elapsedTime, startTime, endTime, elapsed);
      test.addScriptSummary(timeSummary);

   }

   private void addTestPointSummaryToScriptLog(final TestScript test) {

      TestPointResults testPointResults = new TestPointResults(test.getPasses(), test.getFails(), test.isAborted());
      test.addScriptSummary(testPointResults);

   }

   private void addSystemInfoToScriptLog(TestScript test) {

      SystemInfo systemInfo = new SystemInfo();
      test.addScriptSummary(systemInfo);

   }

   @SuppressWarnings("deprecation")
   @Override
   public IMethodResult preInstantiation(IEventData eventData, TestEnvironment env) {
      env.onScriptSetup();
      ((SimpleTestEnvironment) env).notifyPreInstantiationListeners();
      return new MethodResultImpl(ReturnCode.OK);
   }

}
