/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.message;

import java.util.Set;

import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;

/**
 * @author Andy Jury
 */
public abstract class MessageSystemTestCase extends TestCase implements ITestAccessor {

   private final ITestEnvironmentMessageSystemAccessor msgSysTestEnvironment;

   protected MessageSystemTestCase(TestScript testScript, boolean standAlone, boolean addToRunList) {
      super(testScript, standAlone, addToRunList);
      msgSysTestEnvironment = (MessageSystemTestScript) testScript;
   }

   /**
    * TestCase Constructor.
    */
   public MessageSystemTestCase(TestScript testScript, boolean standAlone) {
      this(testScript, standAlone, true);
   }

   /**
    * TestCase Constructor.
    */
   public MessageSystemTestCase(TestScript testScript) {
      this(testScript, false);
   }

   @Override
   public IMessageManager<?> getMsgManager() {
      return msgSysTestEnvironment.getMsgManager();
   }

   @Override
   public boolean isPhysicalTypeAvailable(DataType mux) {
      return msgSysTestEnvironment.isPhysicalTypeAvailable(mux);
   }

   @Override
   public void associateObject(Class<?> c, Object obj) {
      msgSysTestEnvironment.associateObject(c, obj);
   }

   @Override
   public Set<? extends DataType> getDataTypes() {
      return msgSysTestEnvironment.getDataTypes();
   }

}
