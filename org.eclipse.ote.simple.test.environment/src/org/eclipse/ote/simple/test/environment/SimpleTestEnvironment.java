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
import org.eclipse.ote.simple.io.SimpleDataType;
import org.eclipse.ote.simple.io.manager.SimpleMessageManager;

/**
 * @author Andy Jury
 */
public class SimpleTestEnvironment extends MessageSystemTestEnvironment {
   
   private static final Set<DataType> SUPPORTED_PHYSICAL_TYPES = new HashSet<DataType>();
   static {
        SUPPORTED_PHYSICAL_TYPES.add(SimpleDataType.SIMPLE); 
   }

   public SimpleTestEnvironment(IRuntimeLibraryManager runtimeLibManager) {
      super(new SimpleTestEnvironmentFactory(new RealTime(), new ScriptControl(), new SimpleTestStation(), runtimeLibManager));
   }

   @Override
   public boolean isPhysicalTypeAvailable(DataType physicalType) {
      return SUPPORTED_PHYSICAL_TYPES.contains(physicalType);
   }

   @Override
   public Set<? extends DataType> getDataTypes() {
      return SUPPORTED_PHYSICAL_TYPES;
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
