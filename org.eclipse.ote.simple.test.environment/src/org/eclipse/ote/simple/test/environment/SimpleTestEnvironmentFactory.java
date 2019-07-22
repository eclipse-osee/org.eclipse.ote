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

package org.eclipse.ote.simple.test.environment;

import org.eclipse.osee.ote.core.environment.ReportDataControl;
import org.eclipse.osee.ote.core.environment.interfaces.IEnvironmentFactory;
import org.eclipse.osee.ote.core.environment.interfaces.IReportData;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.framework.BaseCommandContextFactory;
import org.eclipse.osee.ote.core.framework.BaseRunManager;
import org.eclipse.osee.ote.core.framework.BaseTestLifecycleListenerProvider;
import org.eclipse.osee.ote.core.framework.ICommandContextFactory;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.osee.ote.core.framework.ITestLifecycleListenerProvider;
import org.eclipse.osee.ote.core.framework.command.BaseCommandManager;
import org.eclipse.osee.ote.core.framework.command.ICommandManager;
import org.eclipse.osee.ote.core.framework.event.BaseEventDataProvider;
import org.eclipse.osee.ote.core.framework.event.IEventDataProvider;
import org.eclipse.osee.ote.core.framework.testrun.BaseTestRunListenerProviderFactory;
import org.eclipse.osee.ote.core.framework.testrun.BaseTestRunManager;
import org.eclipse.osee.ote.core.framework.testrun.ITestFactory;
import org.eclipse.osee.ote.core.framework.testrun.ITestResultCollectorFactory;
import org.eclipse.osee.ote.core.framework.testrun.ITestRunListenerProviderFactory;
import org.eclipse.osee.ote.core.framework.testrun.ITestRunManager;
import org.eclipse.osee.ote.core.log.TestLogger;
import org.eclipse.ote.simple.test.environment.listener.SimpleGCListener;
import org.eclipse.ote.simple.test.environment.listener.SimpleTestLifeCycleListener;

/**
 * @author Andy Jury
 */
public class SimpleTestEnvironmentFactory implements IEnvironmentFactory {

   private final ITimerControl timerCtrl;
   private final IScriptControl scriptCtrl;
   private final IReportData reportData;
   private final ITestLogger testLogger;

   private final ICommandContextFactory cmdContextFactory;
   private final ICommandManager cmdManager;
   private final IRunManager runManager;
   private final IRuntimeLibraryManager runtimeManager;
   private final ITestStation station;

   public SimpleTestEnvironmentFactory(ITimerControl timerCtrl, IScriptControl scriptControl, ITestStation station, IRuntimeLibraryManager runtimeManager) {

      this.timerCtrl = timerCtrl;
      this.scriptCtrl = scriptControl;
      this.station = station;
      this.runtimeManager = runtimeManager;
      this.reportData = new ReportDataControl();
      this.testLogger = new TestLogger();
      this.cmdManager = new BaseCommandManager();
      this.cmdContextFactory = new BaseCommandContextFactory();
      this.runManager = createRunManager();

      timerCtrl.setRunManager(runManager);
      runManager.addListener(new SimpleTestLifeCycleListener());
      runManager.addListener(new SimpleGCListener());
   }

   private BaseRunManager createRunManager() {
      ITestRunManager testRunManager = createTestRunManager();
      // Create test life-cycle manager
      IEventDataProvider eventDataProvider = new BaseEventDataProvider();
      ITestLifecycleListenerProvider lifeCycleListenerProvider = new BaseTestLifecycleListenerProvider(eventDataProvider);
      ITestResultCollectorFactory resultCollectorFactory = new SimpleTestResultCollectorFactory();
      return new BaseRunManager(testRunManager, lifeCycleListenerProvider, resultCollectorFactory);
   }

   private ITestRunManager createTestRunManager() {
      ITestFactory testFactory = new SimpleTestFactory(runtimeManager);
      ITestRunListenerProviderFactory baseTestRunListenerProviderFactory = new BaseTestRunListenerProviderFactory();
      return new BaseTestRunManager(testFactory, baseTestRunListenerProviderFactory);
   }

   @Override
   public ITimerControl getTimerControl() {
      return timerCtrl;
   }

   @Override
   public IScriptControl getScriptControl() {
      return scriptCtrl;
   }

   @Override
   public IReportData getReportDataControl() {
      return reportData;
   }

   @Override
   public ITestLogger getTestLogger() {
      return testLogger;
   }

   @Override
   public ICommandContextFactory getCommandContextFactory() {
      return cmdContextFactory;
   }

   @Override
   public ICommandManager getCommandManager() {
      return cmdManager;
   }

   @Override
   public IRunManager getRunManager() {
      return runManager;
   }

   @Override
   public ITestStation getTestStation() {
      return station;
   }

   @Override
   public IRuntimeLibraryManager getRuntimeManager() {
      return runtimeManager;
   }

}
