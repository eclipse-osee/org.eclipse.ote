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

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.OTESessionManager;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.config.ScriptVersionConfig;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptInitializer;
import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.MethodResultImpl;
import org.eclipse.osee.ote.core.framework.ReturnCode;
import org.eclipse.osee.ote.core.framework.command.RunTestsKeys;
import org.eclipse.osee.ote.core.framework.event.IEventData;
import org.eclipse.osee.ote.core.framework.testrun.ITestRunListener;
import org.eclipse.osee.ote.core.log.record.ScriptConfigRecord;
import org.eclipse.osee.ote.core.log.record.ScriptInitRecord;
import org.eclipse.ote.services.core.ServiceUtility;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class BasicTestRunListener implements ITestRunListener {

   private final TestEnvironment env;

   private OSEEPerson1_4 validUser = new OSEEPerson1_4("ERROR", "ERROR", "ERROR");

   public BasicTestRunListener(TestEnvironment env) {
      this.env = env;
   }

   /**
    * The contract we're assuming is that postRun will allow for messaging to
    * still be done after the conclusion of the
    * script running.
    */
   @SuppressWarnings("deprecation")
   @Override
   public IMethodResult postRun(IEventData eventData) {

      eventData.getTest().endTest();
      eventData.getTest().processScriptcompleteListeners();

      if (env.getScriptCtrl().isLocked()) {
         env.getScriptCtrl().unlock();
      }

      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult postTestCase(IEventData eventData) {
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult preRun(IEventData eventData) {
      // from TestScript
      MethodResultImpl result = new MethodResultImpl(ReturnCode.OK);
      env.getScriptCtrl().setScriptReady(true);

      ScriptConfigRecord scriptConfig = new ScriptConfigRecord(eventData.getTest());
      try {
         OTESessionManager sessionManager = ServiceUtility.getService(OTESessionManager.class);
         IUserSession session = sessionManager.getActiveUser();
         validUser = session.getUser();
      } catch (Exception ex) {
         OseeLog.log(getClass(), Level.WARNING, "Failed to get the user from the client OSEE", ex);
      }
      scriptConfig.setExecutedBy(validUser.getName(), validUser.getEmail(), validUser.getId());
      ScriptVersionConfig version = new ScriptVersionConfig(eventData.getProperties().get(RunTestsKeys.version_repositoryType.name()),
         eventData.getProperties().get(RunTestsKeys.version_location.name()),
         eventData.getProperties().get(RunTestsKeys.version_revision.name()),
         eventData.getProperties().get(RunTestsKeys.version_lastAuthor.name()),
         eventData.getProperties().get(RunTestsKeys.version_lastModificationDate.name()),
         eventData.getProperties().get(RunTestsKeys.version_modifiedFlag.name()));
      scriptConfig.setScriptVersion(version);
      eventData.getTest().getLogger().log(scriptConfig);

      env.getScriptCtrl().lock();

      try {
         eventData.getTest().getLogger().log(new ScriptInitRecord(eventData.getTest(), true));
         IScriptInitializer initializer = eventData.getTest().getScriptInitializer();
         if (initializer != null) {
            initializer.doScriptInitialProcessing();
         }
         eventData.getTest().getLogger().log(new ScriptInitRecord(eventData.getTest(), false));

      } catch (Exception ex) {
         if (result.getReturnCode() == ReturnCode.OK) {
            result.setReturnCode(ReturnCode.ERROR);
            result.addStatus(new BaseStatus(BasicTestRunListener.class.getName(), Level.SEVERE, ex));
         } else {
            result.addStatus(new BaseStatus(BasicTestRunListener.class.getName(), Level.SEVERE, ex));
         }
      }

      return result;
   }

   @Override
   public IMethodResult preTestCase(IEventData eventData) {
      return new MethodResultImpl(ReturnCode.OK);
   }

}
