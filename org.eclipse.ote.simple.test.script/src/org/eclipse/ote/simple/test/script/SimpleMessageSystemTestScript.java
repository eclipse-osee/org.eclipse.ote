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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.enums.ScriptTypeEnum;
import org.eclipse.osee.ote.core.environment.OteApi;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.jini.ITestEnvironmentCommandCallback;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.MessageSystemTestScript;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;

/**
 * @author Michael P. Masterson
 */
public class SimpleMessageSystemTestScript extends MessageSystemTestScript {

   protected IMessageRequestor<Message> messageRequestor;
   protected OteApi oteApi;

   @SuppressWarnings("unchecked")
   public SimpleMessageSystemTestScript(MessageSystemTestEnvironment testEnvironment, ITestEnvironmentCommandCallback callback) {
      super(testEnvironment, null, ScriptTypeEnum.FUNCTIONAL_TEST, true);

      messageRequestor = testEnvironment.getMsgManager().createMessageRequestor(getClass().getName());
      this.oteApi = testEnvironment.getOteApi();
   }

   protected <CLASSTYPE extends Message> CLASSTYPE getMessageWriter(Class<CLASSTYPE> type) {
      return messageRequestor.getMessageWriter(type);
   }

   /**
    * Any time a requestor is created, it should be disposed of when done
    */
   @Override
   protected void dispose() {
      messageRequestor.dispose();
      super.dispose();
   }

   @Override
   protected void addMethodAsTestCase(Method method) {
      Class<?>[] parameterTypes = method.getParameterTypes();
      if (parameterTypes.length != 1) {
         throw new OseeArgumentException("Wrong method signature for test case method %s", method.getName());
      }
      if (parameterTypes[0].isAssignableFrom(this.oteApi.getClass()) || parameterTypes[0].equals(OteApi.class)) {
         addTestCase(new TestCase(this, false, false) {

            @Override
            public void doTestCase(ITestEnvironmentAccessor environment, ITestLogger logger)
                  throws InterruptedException {
               try {
                  method.invoke(getTestScript(), oteApi);
               } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                  Throwable realThrowable = ex;
                  while (realThrowable.getCause() != null) {
                     realThrowable = realThrowable.getCause();
                  }
                  ex.printStackTrace(System.err);
                  OseeCoreException.wrapAndThrow(realThrowable);
               }
            }
         });
      } else {
         System.out.println("NOT RUNNING TEST CASE BASED ON CONFIGURATION NOT MATCHING - " + method.getName());
      }
   }

   @Override
   public Set<? extends DataType> getDataTypes() {
      return null;
   }

   @Override
   public Set<Class<?>> getAssociatedObjects() {
      return null;
   }
}
