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
package org.eclipse.ote.test.manager.connection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.osee.framework.core.data.TestRunStorageKey;
import org.eclipse.osee.framework.core.enums.OteRunTestsKeys;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.ote.core.environment.config.ScriptVersionConfig;
import org.eclipse.osee.ote.core.framework.command.RunTestsKeys;
import org.eclipse.osee.ote.ui.test.manager.models.ScriptModel.TestFileData;
import org.eclipse.osee.ote.ui.test.manager.pages.contributions.TestManagerStorageKeys;
import org.eclipse.osee.ote.version.FileVersion;
import org.eclipse.osee.ote.version.FileVersionInformation;
import org.eclipse.ote.test.manager.pages.StorageKeys;
import org.eclipse.ote.test.manager.uut.selector.IUutItem;
import org.eclipse.ote.test.manager.uut.selector.UutItemPartition;


/**
 * @author Andrew M. Finkbeiner
 */
public class OTERunTestsCommandFactory {

   private OTERunTestsCommandFactory() {
   }

   private static String getUutConsoleOption(String consoleOption, String consoleFilePath) {
      String consoleOutput = "";
      if (Strings.isValid(consoleOption) != false) {
         if (consoleOption.equalsIgnoreCase("Console")) {
            consoleOutput = "";
         } else if (consoleOption.equalsIgnoreCase("None")) {
            consoleOutput = "/dev/null";
         } else {
            consoleOutput = consoleFilePath;
         }
      }
      return consoleOutput;
   }

   public static void buildGlobalAndScriptProperties(PropertyStore global, List<IPropertyStore> scriptsToRun, UUID uuid, IPropertyStore propertyStore, String classServer, List<UutItemPartition> uuts, String distrobutionStatement, List<TestFileData> runthese) {

      global.put(RunTestsKeys.classpath.name(), new String[] {classServer});
      global.put(RunTestsKeys.batchFailAbortMode.name(),
         propertyStore.getBoolean(TestManagerStorageKeys.ABORT_ON_FAIL_KEY));
      global.put(RunTestsKeys.batchFailPauseMode.name(),
         propertyStore.getBoolean(TestManagerStorageKeys.PAUSE_ON_FAIL_KEY));
      global.put(RunTestsKeys.printFailToConsoleMode.name(),
         propertyStore.getBoolean(TestManagerStorageKeys.PRINT_FAIL_TO_CONSOLE));

      List<File> javafiles = new ArrayList<>();
      for (TestFileData testdata : runthese) {
         File javaFile = new File(testdata.rawFileName);
         if (javaFile.exists() && javaFile.canRead()) {
            javafiles.add(javaFile);
         }
      }

      FileVersionInformation fileVersionInformation =
         org.eclipse.osee.ote.core.ServiceUtility.getService(FileVersionInformation.class);
      Map<File, FileVersion> fileVersions = fileVersionInformation.getFileVersions(javafiles);

      global.put("StorageEnabled", propertyStore.getBoolean(TestRunStorageKey.STORAGE_ENABLED));
      global.put("LastBranchSelectedId", propertyStore.get(TestRunStorageKey.SELECTED_BRANCH_ID));
      global.put("DemoExecutedBy", propertyStore.getArray(StorageKeys.DEMONSTRATION_EXECUTED_BY));
      global.put("DemoWitnesses", propertyStore.getArray(StorageKeys.DEMONSTRATION_WITNESSES));
      global.put("DemoBuildId", propertyStore.get(StorageKeys.DEMONSTRATION_BUILD));
      global.put("DemoNotes", propertyStore.get(StorageKeys.DEMONSTRATION_NOTES));
      global.put("FormalTestType", propertyStore.get(StorageKeys.FORMAL_TEST_TYPE));
      global.put(RunTestsKeys.executableDebug.name(), propertyStore.getBoolean(StorageKeys.DEBUG_OPTIONS));
      global.put(OteRunTestsKeys.uut_debug.name(), propertyStore.getBoolean(StorageKeys.DEBUG_OPTIONS));

      List<String> uutPartitions = new ArrayList<String>();
      List<String> uutPaths = new ArrayList<String>();
      List<String> uutRates = new ArrayList<String>();
      for (IUutItem item : uuts) {
         if (item.isSelected()) {
            uutPartitions.add(item.getPartition());
            uutPaths.add(item.getPath());
            uutRates.add(item.getRate());
         }
         String su = item.getPartition();
         global.put("default_" + getDefaultParentClass(su), item.getPath());
      }
      global.put(RunTestsKeys.executableArg1Array.name(), uutPartitions.toArray(new String[uutPartitions.size()]));
      global.put(RunTestsKeys.executablePathsArray.name(), uutPaths.toArray(new String[uutPaths.size()]));
      global.put(RunTestsKeys.executableArg2Array.name(), uutRates.toArray(new String[uutRates.size()]));

      global.put(OteRunTestsKeys.uut_partition_array.name(), uutPartitions.toArray(new String[uutPartitions.size()]));
      global.put(OteRunTestsKeys.uut_paths_array.name(), uutPaths.toArray(new String[uutPaths.size()]));
      global.put(OteRunTestsKeys.uut_rate_array.name(), uutRates.toArray(new String[uutRates.size()]));

      // For compatibility with older test runners
      if (uutPaths.size() > 0) {
         global.put(RunTestsKeys.executablePath.name(), uutPaths.get(0));
         global.put(OteRunTestsKeys.uut_path.name(), uutPaths.get(0));
      } else {
         // Doesn't matter as long as the first char is a $.
         global.put(RunTestsKeys.executablePath.name(), "$$$Default");
         global.put(OteRunTestsKeys.uut_path.name(), "$$$Default");
      }

      global.put(RunTestsKeys.distributionStatement.name(), distrobutionStatement);
      String consoleFilePath = propertyStore.get(StorageKeys.SERVER_OUTPUT_FILE_PATH_STORAGE_KEY);
      String consoleOption = propertyStore.get(StorageKeys.SERVER_OUTPUT_SELECTION_STORAGE_KEY);
      global.put(RunTestsKeys.executableOutputPath.name(), getUutConsoleOption(consoleOption, consoleFilePath));
      global.put(OteRunTestsKeys.uut_output_path.name(), getUutConsoleOption(consoleOption, consoleFilePath));
      global.put("logging.level", propertyStore.get(TestManagerStorageKeys.LOGGING_LEVEL_KEY));

      for (TestFileData testdata : runthese) {
         PropertyStore store = new PropertyStore(OTERunTestsCommandFactory.class.getSimpleName());
         scriptsToRun.add(store);

         File javaFile = new File(testdata.rawFileName);
         ScriptVersionConfig scriptConfigVersionInfo = testdata.getVersionInfo(fileVersions.get(javaFile));

         store.put(RunTestsKeys.classpath.name(), classServer);
         store.put(RunTestsKeys.testClass.name(), testdata.name);
         store.put(RunTestsKeys.serverOutfilePath.name(), testdata.outFile);
         store.put(RunTestsKeys.clientOutfilePath.name(), testdata.outFile);
         store.put(RunTestsKeys.version_lastAuthor.name(), scriptConfigVersionInfo.getLastAuthor());
         store.put(RunTestsKeys.version_lastModificationDate.name(), scriptConfigVersionInfo.getLastModificationDate());
         if (scriptConfigVersionInfo.getLocation() != null) {
            store.put(RunTestsKeys.version_location.name(), scriptConfigVersionInfo.getLocation());
         }
         store.put(RunTestsKeys.version_modifiedFlag.name(), scriptConfigVersionInfo.getModifiedFlag());
         store.put(RunTestsKeys.version_repositoryType.name(), scriptConfigVersionInfo.getRepositoryType());
         store.put(RunTestsKeys.version_revision.name(), scriptConfigVersionInfo.getLastChangedRevision());
      }

   }

   private static String getDefaultParentClass(String su) {
      String parentClass = su.toUpperCase().substring(0, 1) + su.toLowerCase().substring(1);
      parentClass += "TestScript";
      return parentClass;
   }

}
