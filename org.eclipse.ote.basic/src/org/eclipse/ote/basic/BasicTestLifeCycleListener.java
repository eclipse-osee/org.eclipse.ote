/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.ote.basic;

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
import org.eclipse.osee.ote.core.framework.outfile.xml.FormalityLevelRecord;
import org.eclipse.osee.ote.core.framework.outfile.xml.SystemInfo;
import org.eclipse.osee.ote.core.framework.outfile.xml.TestPointResults;
import org.eclipse.osee.ote.core.framework.outfile.xml.TimeSummary;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public final class BasicTestLifeCycleListener implements ITestLifecycleListener {

   private Date startTime;

   public BasicTestLifeCycleListener() {
   }

   @Override
   public IMethodResult postDispose(IEventData eventData, TestEnvironment env) {
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult postInstantiation(IEventData eventData, TestEnvironment env) {
      logFormalityLevel(eventData);
      eventData.getTest().addTestRunListener(new BasicTestRunListener(env));
      eventData.getTest().addScriptSummary(env.getRuntimeManager());
      startTime = new Date();
      return new MethodResultImpl(ReturnCode.OK);
   }

   private void logFormalityLevel(IEventData eventData) {
      String formalityLevel;
      String buildId;
      String[] runnerNames;
      String[] witnessNames;
      String notes;
      runnerNames = eventData.getProperties().getArray("DemoExecutedBy");
      witnessNames = eventData.getProperties().getArray("DemoWitnesses");
      buildId = eventData.getProperties().get("DemoBuildId");
      notes = eventData.getProperties().get("DemoNotes");
      formalityLevel = eventData.getProperties().get("FormalTestType");

      if (runnerNames != null && witnessNames != null && formalityLevel != null) {
         FormalityLevelRecord record = new FormalityLevelRecord(formalityLevel, buildId,
                                                                runnerNames, witnessNames, notes);
         eventData.getTest().getLogger().log(record);
      }
   }

   /**
    * The contract we're assuming is that preDispose is too late for messaging to still be done
    * after the conclusion of the script running. To do that use postRun from ITestRunListener.
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

         OseeLog.log(BasicTestLifeCycleListener.class, OteLevel.TEST_EVENT,
            String.format("%s Pass[%d] Fail[%d] interactive[%d] Aborted[%b]",
               eventData.getTest().getClass().getSimpleName(), eventData.getTest().getPasses(),
               eventData.getTest().getFails(), eventData.getTest().getInteractives(), eventData.getTest().isAborted()));

         env.onScriptComplete();
      } catch (InterruptedException ex) {
         result = new MethodResultImpl(ReturnCode.ERROR);
         result.addStatus(new BaseStatus(BasicTestLifeCycleListener.class.getName(), Level.SEVERE, ex));
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

      TestPointResults testPointResults =
         new TestPointResults(test.getPasses(), test.getFails(), test.getInteractives(), test.isAborted());
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
      ((MessageSystemTestEnvironment) env).notifyPreInstantiationListeners();
      return new MethodResultImpl(ReturnCode.OK);
   }

}
