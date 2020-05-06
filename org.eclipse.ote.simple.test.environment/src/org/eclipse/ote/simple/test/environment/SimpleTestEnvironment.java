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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.ScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.timer.RealTime;
import org.eclipse.ote.simple.io.manager.SimpleMessageManager;

/**
 * @author Andy Jury
 */
public class SimpleTestEnvironment extends MessageSystemTestEnvironment {

   public SimpleTestEnvironment(IRuntimeLibraryManager runtimeLibManager) {
      super(new SimpleTestEnvironmentFactory(new RealTime(), new ScriptControl(), new SimpleTestStation(), runtimeLibManager));
   }

   @Override
   public boolean isPhysicalTypeAvailable(DataType physicalType) {
      return true;
   }

   @Override
   public Set<? extends DataType> getDataTypes() {
      return new HashSet<>();
   }

   @Override
   protected TestScript instantiateScriptClass(Class<?> scriptClass, IUserSession connection) {
      return null;
   }

   @Override
   public void singleStepEnv() {
      // Intentionally empty block
   }

   @Override
   public Object getModel(String modelClassName) {
      return null;
   }

   @Override
   protected void loadExternalDrivers() {
      // Intentionally empty block
   }
   
   @Override
   public SimpleMessageManager getMsgManager() {
      return (SimpleMessageManager) super.getMsgManager();
   }
}
