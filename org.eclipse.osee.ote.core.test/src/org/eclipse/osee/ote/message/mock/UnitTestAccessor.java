/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.mock;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.ReportDataControl;
import org.eclipse.osee.ote.core.environment.ScriptControl;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.command.CommandDescription;
import org.eclipse.osee.ote.core.environment.interfaces.BasicTimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IExecutionUnitManagement;
import org.eclipse.osee.ote.core.environment.interfaces.IReportData;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.environment.status.CommandEndedStatusEnum;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.osee.ote.core.log.ITestPointTally;
import org.eclipse.osee.ote.core.log.TestLogger;
import org.eclipse.osee.ote.core.log.record.TestPointRecord;
import org.eclipse.osee.ote.core.log.record.TestRecord;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;

public class UnitTestAccessor implements ITestEnvironmentMessageSystemAccessor, ITestAccessor {
   private final HashMap<EnvironmentTask, ScheduledFuture<?>> handleMap =
         new HashMap<EnvironmentTask, ScheduledFuture<?>>(32);
   private final ScheduledExecutorService executor =
         Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
   private final IScriptControl scriptCtrl = new ScriptControl();
   private final IReportData reportData = new ReportDataControl();
   private final ITestLogger testLogger = new TestLogger() {

      @Override
      public void attention(ITestEnvironmentAccessor source, String message) {

      }

      @Override
      public void debug(ITestEnvironmentAccessor source, String message, boolean timeStamp) {

      }

      @Override
      public void debug(ITestEnvironmentAccessor source, String message) {

      }

      @Override
      public void log(TestRecord record) {

      }

      @Override
      public void methodCalled(ITestEnvironmentAccessor source, MethodFormatter arguments, int methodCount) {

      }

      @Override
      public void methodCalled(ITestEnvironmentAccessor source, MethodFormatter arguments) {

      }

      @Override
      public void methodCalled(ITestEnvironmentAccessor source) {

      }

      @Override
      public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName, MethodFormatter arguments, int methodCount) {

      }

      @Override
      public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName, MethodFormatter methodFormat, Xmlizable xmlObject) {

      }

      @Override
      public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName, MethodFormatter methodFormat) {

      }

      @Override
      public void methodCalledOnObject(ITestEnvironmentAccessor source, String objectName) {

      }

      @Override
      public void methodEnded(ITestEnvironmentAccessor source) {

      }

      @Override
      public void requirement(ITestEnvironmentAccessor source, String message) {

      }

      @Override
      public void severe(ITestEnvironmentAccessor source, String message) {

      }

      @Override
      public void severe(Object source, Throwable thrown) {

      }

      @Override
      public void support(ITestEnvironmentAccessor source, String message) {

      }

      @Override
      public void testCaseBegan(TestCase testCase) {

      }

      @Override
      public void testpoint(ITestEnvironmentAccessor env, TestScript script, TestCase testCase, boolean passed, String testPointName, String exp, String act) {

      }

      @Override
      public void testpoint(ITestEnvironmentAccessor env, TestScript script, TestCase testCase, ITestPoint testPoint) {

      }

      @Override
      public void testpoint(TestPointRecord record) {

      }

      @Override
      public void warning(ITestEnvironmentAccessor source, String message) {

      }

      @Override
      public synchronized void addHandler(Handler handler) throws SecurityException {

      }

      @Override
      public void config(String msg) {

      }

      @Override
      public void entering(String sourceClass, String sourceMethod) {

      }

      @Override
      public void entering(String sourceClass, String sourceMethod, Object param1) {

      }

      @Override
      public void entering(String sourceClass, String sourceMethod, Object[] params) {

      }

      @Override
      public void exiting(String sourceClass, String sourceMethod) {

      }

      @Override
      public void exiting(String sourceClass, String sourceMethod, Object result) {

      }

      @Override
      public void fine(String msg) {

      }

      @Override
      public void finer(String msg) {

      }

      @Override
      public void finest(String msg) {

      }

      @Override
      public Filter getFilter() {
         return super.getFilter();
      }

      @Override
      public synchronized Handler[] getHandlers() {
         return super.getHandlers();
      }

      @Override
      public Level getLevel() {
         return super.getLevel();
      }

      @Override
      public String getName() {
         return super.getName();
      }

      @Override
      public Logger getParent() {
         return super.getParent();
      }

      @Override
      public ResourceBundle getResourceBundle() {
         return super.getResourceBundle();
      }

      @Override
      public String getResourceBundleName() {
         return super.getResourceBundleName();
      }

      @Override
      public synchronized boolean getUseParentHandlers() {
         return super.getUseParentHandlers();
      }

      @Override
      public void info(String msg) {
         super.info(msg);
      }

      @Override
      public boolean isLoggable(Level level) {
         return super.isLoggable(level);
      }

      @Override
      public void log(LogRecord record) {
         super.log(record);
      }

      @Override
      public void log(Level level, String msg) {
         super.log(level, msg);
      }

      @Override
      public void log(Level level, String msg, Object param1) {
         super.log(level, msg, param1);
      }

      @Override
      public void log(Level level, String msg, Object[] params) {
         super.log(level, msg, params);
      }

      @Override
      public void log(Level level, String msg, Throwable thrown) {
         super.log(level, msg, thrown);
      }

      @Override
      public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
         super.logp(level, sourceClass, sourceMethod, msg);
      }

      @Override
      public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
         super.logp(level, sourceClass, sourceMethod, msg, param1);
      }

      @Override
      public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
         super.logp(level, sourceClass, sourceMethod, msg, params);
      }

      @Override
      public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
         super.logp(level, sourceClass, sourceMethod, msg, thrown);
      }

      @Override
      public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
         super.logrb(level, sourceClass, sourceMethod, bundleName, msg);
      }

      @Override
      public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {
         super.logrb(level, sourceClass, sourceMethod, bundleName, msg, param1);
      }

      @Override
      public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {
         super.logrb(level, sourceClass, sourceMethod, bundleName, msg, params);
      }

      @Override
      public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
         super.logrb(level, sourceClass, sourceMethod, bundleName, msg, thrown);
      }

      @Override
      public synchronized void removeHandler(Handler handler) throws SecurityException {
         super.removeHandler(handler);
      }

      @Override
      public void setFilter(Filter newFilter) throws SecurityException {
         super.setFilter(newFilter);
      }

      @Override
      public void setLevel(Level newLevel) throws SecurityException {
         super.setLevel(newLevel);
      }

      @Override
      public void setParent(Logger parent) {
         super.setParent(parent);
      }

      @Override
      public synchronized void setUseParentHandlers(boolean useParentHandlers) {
         super.setUseParentHandlers(useParentHandlers);
      }

      @Override
      public void severe(String msg) {
         super.severe(msg);
      }

      @Override
      public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
         super.throwing(sourceClass, sourceMethod, thrown);
      }

      @Override
      public void warning(String msg) {
         super.warning(msg);
      }

      @Override
      protected Object clone() throws CloneNotSupportedException {
         return super.clone();
      }

      @Override
      public boolean equals(Object obj) {
         return super.equals(obj);
      }

      @Override
      protected void finalize() throws Throwable {
         super.finalize();
      }

      @Override
      public int hashCode() {
         return super.hashCode();
      }

      @Override
      public String toString() {
         return super.toString();
      }

   };

   private final ITimerControl timerCtrl = new ITimerControl() {

      @Override
      public void addTask(final EnvironmentTask task, TestEnvironment environment) {
         final ScheduledFuture<?> handle = schedulePeriodicTask(new Runnable() {
            @Override
            public void run() {

               try {
                  if (task.isRunning()) {
                     task.runOneCycle();
                  }
               } catch (Throwable ex) {
                  ScheduledFuture<?> h = handleMap.get(task);
                  if (h != null) {
                     h.cancel(false);
                  }
                  ex.printStackTrace(System.err);
               }
            }

         }, 0, (long) Math.rint(1000.0 / task.getHzRate()));
         handleMap.put(task, handle);
      }

      @Override
      public void cancelAllTasks() {
         for (ScheduledFuture<?> handle : handleMap.values()) {
            handle.cancel(false);
         }
         handleMap.clear();
      }

      @Override
      public void cancelTimers() {
         executor.shutdown();
      }

      @Override
      public void dispose() {

      }

      @Override
      public void envWait(ITimeout obj, int milliseconds) throws InterruptedException {
         synchronized (obj) {
            obj.wait(milliseconds);
         }
      }

      @Override
      public void envWait(int milliseconds) throws InterruptedException {
         envWait(new BasicTimeout(), milliseconds);
      }

      @Override
      public int getCycleCount() {
         return (int) System.currentTimeMillis() / 20;
      }

      @Override
      public long getEnvTime() {
         return System.currentTimeMillis();
      }

      @Override
      public void incrementCycleCount() {

      }

      @Override
      public void removeTask(final EnvironmentTask task) {
         ScheduledFuture<?> handle = handleMap.remove(task);
         if (handle != null) {
            handle.cancel(false);
         }
      }

      @Override
      public void setCycleCount(int cycle) {

      }

      @Override
      public ICancelTimer setTimerFor(final ITimeout objToNotify, int milliseconds) {
         objToNotify.setTimeout(false);
         final ScheduledFuture<?> handle = scheduleOneShotTask(new Runnable() {

            @Override
            public void run() {
               synchronized (objToNotify) {
                  objToNotify.setTimeout(true);
                  objToNotify.notify();
               }
            }
         }, milliseconds);

         return new ICancelTimer() {

            @Override
            public void cancelTimer() {
               handle.cancel(false);
            }
         };
      }

      @Override
      public void step() {

      }

      @Override
      public long getTimeOfDay() {
         return 0;
      }

      @Override
      public void setRunManager(IRunManager runManager) {
      }

      @Override
      public IRunManager getRunManager() {
         return null;
      }

      @Override
      public boolean isRealtime() {
         return false;
      }

   };

   public UnitTestAccessor() {

   }

   @Override
   public IMessageManager getMsgManager() {
      return null;
   }

   @Override
   public boolean isPhysicalTypeAvailable(DataType physicalType) {
      return physicalType == TestMemType.ETHERNET;
   }

   @Override
   public void abortTestScript() {

   }

   @Override
   public void abortTestScript(Throwable t) {

   }

   @Override
   public boolean addTask(final EnvironmentTask task) {
      timerCtrl.addTask(task, null);
      return true;
   }

   public void addRunnable(Runnable r) {

   }

   @Override
   public void associateObject(Class<?> c, Object obj) {

   }

   @Override
   public Object getAssociatedObject(Class<?> c) {
      return null;
   }

   @Override
   public Set<Class<?>> getAssociatedObjects() {

      return null;
   }

   public ITestPointTally getAttachedTestPointTally(TestScript script) {

      return null;
   }

   @Override
   public long getEnvTime() {
      return timerCtrl.getEnvTime();
   }

   @Override
   public IExecutionUnitManagement getExecutionUnitManagement() {
      return null;
   }

   @Override
   public ITestLogger getLogger() {
      return testLogger;
   }

   @Override
   public IScriptControl getScriptCtrl() {
      return scriptCtrl;
   }

   @Override
   public TestScript getTestScript() {
      return null;
   }

   @Override
   public ITestStation getTestStation() {
      return null;
   }

   @Override
   public ITimerControl getTimerCtrl() {
      return timerCtrl;
   }

   @Override
   public void onScriptComplete() throws InterruptedException {

   }

   @Override
   public void onScriptSetup() {

   }

   public void setSequentialCmdFinished(CommandDescription description, CommandEndedStatusEnum status) throws Exception {

   }

   @Override
   public ICancelTimer setTimerFor(ITimeout listener, int time) {
      return timerCtrl.setTimerFor(listener, time);
   }

   public ScheduledFuture<?> schedulePeriodicTask(Runnable task, long initialDelay, long period) {
      return executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
   }

   public ScheduledFuture<?> scheduleOneShotTask(Runnable task, long delay) {
      return executor.schedule(task, delay, TimeUnit.MILLISECONDS);
   }

   public void shutdown() {
      timerCtrl.cancelAllTasks();
      timerCtrl.cancelTimers();
   }

   @Override
   public TestCase getTestCase() {

      return null;
   }

   @Override
   public Set<DataType> getDataTypes() {
      return null;
   }

}
