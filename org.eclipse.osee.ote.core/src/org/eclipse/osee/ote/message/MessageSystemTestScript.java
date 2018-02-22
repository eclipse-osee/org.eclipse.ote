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
package org.eclipse.osee.ote.message;

import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.enums.ScriptTypeEnum;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IExecutionUnitManagement;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class MessageSystemTestScript extends TestScript implements ITestAccessor {

   private final ITestEnvironmentMessageSystemAccessor msgSysEnvironment;

   public MessageSystemTestScript(TestEnvironment environment, IUserSession callback, ScriptTypeEnum scriptType, boolean isBatchable) {
      super(environment, callback, scriptType, isBatchable);
      msgSysEnvironment = (ITestEnvironmentMessageSystemAccessor) environment;
   }

   @Override
   public IMessageManager<?, ?> getMsgManager() {
      return msgSysEnvironment.getMsgManager();
   }

   @Override
   public boolean isPhysicalTypeAvailable(DataType mux) {
      return msgSysEnvironment.isPhysicalTypeAvailable(mux);
   }

   @Override
   public void associateObject(Class<?> c, Object obj) {
      msgSysEnvironment.associateObject(c, obj);
   }

   @Override
   public Object getAssociatedObject(Class<?> c) {
      return msgSysEnvironment.getAssociatedObject(c);
   }

   public Object getAssociatedObject() {
      return msgSysEnvironment.getAssociatedObjects();
   }

   @Override
   public IExecutionUnitManagement getExecutionUnitManagement() {
      return msgSysEnvironment.getExecutionUnitManagement();
   }

   @Override
   public ITestStation getTestStation() {
      return msgSysEnvironment.getTestStation();
   }

   @Override
   public ITestLogger getLogger() {
      return msgSysEnvironment.getLogger();
   }

   @Override
   public ITimerControl getTimerCtrl() {
      return msgSysEnvironment.getTimerCtrl();
   }

   @Override
   public IScriptControl getScriptCtrl() {
      return msgSysEnvironment.getScriptCtrl();
   }

   @Override
   public ICancelTimer setTimerFor(ITimeout listener, int time) {
      return msgSysEnvironment.setTimerFor(listener, time);
   }

   @Override
   public final void onScriptSetup() {
      msgSysEnvironment.onScriptSetup();
   }

   @Override
   public final void onScriptComplete() throws InterruptedException {
      msgSysEnvironment.onScriptComplete();
   }

   @Override
   public long getEnvTime() {
      return msgSysEnvironment.getEnvTime();
   }

   //   public ITestPointTally getAttachedTestPointTally(TestScript script) {
   //      return msgSysEnvironment.getAttachedTestPointTally(script);
   //   }

   //   public EnvironmentType getEnvironmentType() {
   //      return msgSysEnvironment.getEnvironmentType();
   //   }
   @Override
   public void abortTestScript() {
      msgSysEnvironment.abortTestScript();
   }

   @Override
   public boolean addTask(EnvironmentTask task) {
      return msgSysEnvironment.addTask(task);
   }
}
