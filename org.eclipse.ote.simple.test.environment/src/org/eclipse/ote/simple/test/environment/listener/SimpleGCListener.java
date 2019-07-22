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

import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.OteLevel;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.ITestLifecycleListener;
import org.eclipse.osee.ote.core.framework.MethodResultImpl;
import org.eclipse.osee.ote.core.framework.ReturnCode;
import org.eclipse.osee.ote.core.framework.event.IEventData;

/**
 * @author Andy Jury
 */
public class SimpleGCListener implements ITestLifecycleListener {
   /**
    * @return MB of used memory
    */
   private static long getUsedMemory() {
      return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000;
   }

   private long preinstantiationMem;

   @Override
   public IMethodResult preInstantiation(IEventData eventData, TestEnvironment env) {
      preinstantiationMem = getUsedMemory();
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult postInstantiation(IEventData eventData, TestEnvironment env) {
      OseeLog.log(getClass(), OteLevel.TEST_EVENT, "Test server memory at script start: " + preinstantiationMem + "MB");
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult preDispose(IEventData eventData, TestEnvironment env) {
      sendMemoryLeakInformationToClient(eventData.getTest());
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult postDispose(IEventData eventData, TestEnvironment env) {
      Runtime.getRuntime().gc();
      return new MethodResultImpl(ReturnCode.OK);
   }

   private void sendMemoryLeakInformationToClient(TestScript test) {
      @SuppressWarnings("unchecked")
      List<Object> tests = GCHelper.getGCHelper().getInstancesOfType(TestScript.class);
      if (tests.size() > 2) {
         String message = buildMemoryLeakString(tests, test);
         try {
            test.prompt(message);
         } catch (InterruptedException ex) {
            OseeLog.log(SimpleGCListener.class, Level.SEVERE, "Failed to send prompt data back to client.\n" + message);
         }
      }
   }

   private String buildMemoryLeakString(List<Object> tests, TestScript test) {
      StringBuilder sb = new StringBuilder();
      sb.append("Tests that are likely memory leaks:\n");
      for (Object obj : tests) {
         if (obj != test) {
            sb.append("\t");
            sb.append(obj.toString());
            sb.append("\n");
         }
      }
      sb.append("For more information type: 'objinst -gc'\n");
      return sb.toString();
   }

}
