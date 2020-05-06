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

import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.enums.ScriptTypeEnum;
import org.eclipse.osee.ote.core.environment.jini.ITestEnvironmentCommandCallback;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.ote.simple.io.SimpleMessageData;
import org.eclipse.ote.simple.io.SimpleMessageType;
import org.eclipse.ote.simple.test.environment.SimpleTestEnvironment;

/**
 * @author Michael P. Masterson
 */
public class SimpleTestScriptType extends TestScript {
   
   protected IMessageRequestor<SimpleMessageData, SimpleMessageType> messageRequestor;

   public SimpleTestScriptType(SimpleTestEnvironment testEnvironment, ITestEnvironmentCommandCallback callback) {
      super(testEnvironment, null, ScriptTypeEnum.FUNCTIONAL_TEST, true);

      messageRequestor = testEnvironment.getMsgManager().createMessageRequestor(getClass().getName());
   }
   
   /**
    * Any time a requestor is created, it should be disposed of when done
    */
   @Override
   protected void dispose() {
      messageRequestor.dispose();
      super.dispose();
   }
   
}