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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.enums.OteRunTestsKeys;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.ILoggerListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.osee.ote.core.framework.outfile.ScriptJsonOutLogHandler;
import org.eclipse.osee.ote.core.framework.outfile.ScriptStreamOutLogHandler;
import org.eclipse.osee.ote.core.framework.testrun.ITestResultCollector;
import org.eclipse.osee.ote.core.log.record.PropertyStoreRecord;
import org.eclipse.osee.ote.core.log.record.TestRecord;

/**
 * @author Andy Jury
 */
public class BasicTestResultCollector implements ITestResultCollector {
   private ILoggerListener loggerListener;
   private ScriptStreamOutLogHandler handlerStream;
   private ScriptJsonOutLogHandler jsonHandler;
   private final Matcher fileNumberMatcher = Pattern.compile("(.*)\\.(\\d+)\\.tmo").matcher("");

   @Override
   public void initialize(IPropertyStore propertyStore, TestEnvironment testEnvironment) throws Exception {
      initialize(propertyStore, testEnvironment.getOutDir(), testEnvironment.getLogger(), testEnvironment.getRunManager());
   }

   public void initialize(IPropertyStore propertyStore, File outfileDirectory, ITestLogger testLogger, IRunManager runManager) throws IOException {
      String serverOutfilePath;
      String value = propertyStore.get(OteRunTestsKeys.serverOutfileFolderOverride.name());
      final String testClass = propertyStore.get(OteRunTestsKeys.testClass.name());
      File folderPath = new File(value);
      if (folderPath.exists() && folderPath.isDirectory()) {
         int number = getHighestExistingFileNumber(folderPath, testClass);
         number++;
         if (number > 0) {
            serverOutfilePath = new File(folderPath, testClass + "." + number + ".tmo").getAbsolutePath();
         } else {
            serverOutfilePath = new File(folderPath, testClass + ".tmo").getAbsolutePath();
         }
      } else {
         serverOutfilePath = File.createTempFile(propertyStore.get(OteRunTestsKeys.testClass.name()), ".tmo", outfileDirectory).getPath();
      }
      propertyStore.put(OteRunTestsKeys.serverOutfilePath.name(), serverOutfilePath);
      String distributionStatement = propertyStore.get("distributionStatement");
      if (Strings.isValid(distributionStatement)) {
         handlerStream = new ScriptStreamOutLogHandler(new File(propertyStore.get(OteRunTestsKeys.serverOutfilePath.name())), distributionStatement);
         jsonHandler = new ScriptJsonOutLogHandler(new File(propertyStore.get(OteRunTestsKeys.serverOutfilePath.name())), distributionStatement);
      } else {
         handlerStream = new ScriptStreamOutLogHandler(new File(propertyStore.get(OteRunTestsKeys.serverOutfilePath.name())));
         jsonHandler = new ScriptJsonOutLogHandler(new File(propertyStore.get(OteRunTestsKeys.serverOutfilePath.name())));
      }
      testLogger.addHandler(handlerStream);
      testLogger.addHandler(jsonHandler);
      loggerListener = new DefaultLoggingListener(testLogger, runManager);
      try {
         TestRecord.setLocationLoggingOn(true);
         String loggingLevel = propertyStore.get("logging.level");
         if (loggingLevel.length() > 0) {
            Level levelToSet = Level.parse(loggingLevel);
            handlerStream.setLevel(levelToSet);
            jsonHandler.setLevel(levelToSet);
            if (levelToSet.intValue() >= Level.WARNING.intValue()) {
               TestRecord.setLocationLoggingOn(false);
            }
         }
      } catch (IllegalArgumentException ex) {
         OseeLog.log(BasicTestResultCollector.class, Level.SEVERE, "Unable to set outfile log level.", ex);
      }
      testLogger.log(new PropertyStoreRecord(propertyStore));
      OseeLog.registerLoggerListener(loggerListener);
   }

   private int getHighestExistingFileNumber(final File folder, final String fileName) {
      String[] files = folder.list(new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            return name.contains(fileName);
         }
      });
      if (files.length == 0) {
         return 0;
      }
      int highestNumber = 1;
      for (String file : files) {
         fileNumberMatcher.reset(file);
         if (fileNumberMatcher.find()) {
            String num = fileNumberMatcher.group(2);
            try {
               int newNum = Integer.parseInt(num);
               if (newNum > highestNumber) {
                  highestNumber = newNum;
               }
            } catch (NumberFormatException ex) {
               // we'll just use the highest detected number or 1 in this case
            }
         }
      }
      return highestNumber;
   }

   @Deprecated
   @Override
   public void dispose(TestEnvironment testEnvironment) throws Exception {
      dispose(testEnvironment.getLogger());
   }

   public void dispose(ITestLogger logger) {
      try {
         handlerStream.flushRecords();
         handlerStream.close();
         jsonHandler.flushRecords();
         jsonHandler.close();
      } catch (Throwable ex) {
         ex.printStackTrace();
      } finally {
         logger.removeHandler(handlerStream);
         logger.removeHandler(jsonHandler);
         handlerStream = null;
         jsonHandler = null;
         OseeLog.unregisterLoggerListener(loggerListener);
         loggerListener = null;
      }
   }

   private static final class DefaultLoggingListener implements ILoggerListener {
      private final ITestLogger testLogger;
      private final IRunManager runManager;

      public DefaultLoggingListener(ITestLogger testLogger, IRunManager runManager) {
         this.testLogger = testLogger;
         this.runManager = runManager;
      }

      @Override
      public void log(String loggerName, Level level, String message, Throwable th) {
         if (testLogger != null && !runManager.isAborted()) {
            testLogger.log(level, message, th);
         }
      }
   }
}
