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
import org.eclipse.osee.ote.core.framework.command.ITestServerCommand;
import org.eclipse.osee.ote.ui.test.manager.models.ScriptModel.TestFileData;
import org.eclipse.osee.ote.ui.test.manager.pages.contributions.TestManagerStorageKeys;
import org.eclipse.osee.ote.version.FileVersion;
import org.eclipse.osee.ote.version.FileVersionInformation;
import org.eclipse.ote.test.manager.editor.OteTestManagerEditor;
import org.eclipse.ote.test.manager.pages.StorageKeys;
import org.eclipse.ote.test.manager.uut.selector.IUutItem;


/**
 * @author Roberto E. Escobar
 */
public class OldSchoolFactory {

   private OldSchoolFactory() {
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

   public static ITestServerCommand getOteRunTestScriptsCmd(UUID uuid, OteTestManagerEditor testManagerEditor, List<TestFileData> runthese) {
      final IPropertyStore propertyStore = testManagerEditor.getPropertyStore();
      IPropertyStore globalProperties = new PropertyStore("global");
      globalProperties.put(OteRunTestsKeys.classpath.name(),
         new String[] {testManagerEditor.getScriptClassServer().getClassServerPath()});
      List<IPropertyStore> scriptData = new ArrayList<>();

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

      for (TestFileData testdata : runthese) {
         IPropertyStore store = new PropertyStore(OldSchoolFactory.class.getSimpleName());
         scriptData.add(store);

         store.put("StorageEnabled", propertyStore.getBoolean(TestRunStorageKey.STORAGE_ENABLED));
         store.put("LastBranchSelectedId", propertyStore.get(TestRunStorageKey.SELECTED_BRANCH_ID));
         store.put("DemoExecutedBy", propertyStore.getArray(StorageKeys.DEMONSTRATION_EXECUTED_BY));
         store.put("DemoWitnesses", propertyStore.getArray(StorageKeys.DEMONSTRATION_WITNESSES));
         store.put("DemoBuildId", propertyStore.get(StorageKeys.DEMONSTRATION_BUILD));
         store.put("DemoNotes", propertyStore.get(StorageKeys.DEMONSTRATION_NOTES));
         store.put("FormalTestType", propertyStore.get(StorageKeys.FORMAL_TEST_TYPE));

         File javaFile = new File(testdata.rawFileName);
         ScriptVersionConfig scriptConfigVersionInfo = testdata.getVersionInfo(fileVersions.get(javaFile));

         store.put(OteRunTestsKeys.classpath.name(), testManagerEditor.getScriptClassServer().getClassServerPath());
         store.put(OteRunTestsKeys.testClass.name(), testdata.name);
         store.put(OteRunTestsKeys.serverOutfilePath.name(), testdata.outFile);
         store.put(OteRunTestsKeys.clientOutfilePath.name(), testdata.outFile);
         store.put(OteRunTestsKeys.version_lastAuthor.name(), scriptConfigVersionInfo.getLastAuthor());
         store.put(OteRunTestsKeys.version_lastModificationDate.name(),
            scriptConfigVersionInfo.getLastModificationDate());
         if (scriptConfigVersionInfo.getLocation() != null) {
            store.put(OteRunTestsKeys.version_location.name(), scriptConfigVersionInfo.getLocation());
         }
         store.put(OteRunTestsKeys.version_modifiedFlag.name(), scriptConfigVersionInfo.getModifiedFlag());
         store.put(OteRunTestsKeys.version_repositoryType.name(), scriptConfigVersionInfo.getRepositoryType());
         store.put(OteRunTestsKeys.version_revision.name(), scriptConfigVersionInfo.getLastChangedRevision());
         store.put(OteRunTestsKeys.uut_debug.name(), propertyStore.getBoolean(StorageKeys.DEBUG_OPTIONS));

         List<String> uutPartitions = new ArrayList<String>();
         List<String> uutPaths = new ArrayList<String>();
         List<String> uutRates = new ArrayList<String>();
         for (IUutItem item : testManagerEditor.getTestManagerModel().getUUTs()) {
            if (item.isSelected()) {
               uutPartitions.add(item.getPartition());
               uutPaths.add(item.getPath());
               uutRates.add(item.getRate());
            }
            String su = item.getPartition();
            store.put("default_" + getDefaultParentClass(su), item.getPath());
         }
         store.put(OteRunTestsKeys.uut_partition_array.name(), uutPartitions.toArray(new String[uutPartitions.size()]));
         store.put(OteRunTestsKeys.uut_paths_array.name(), uutPaths.toArray(new String[uutPaths.size()]));
         store.put(OteRunTestsKeys.uut_rate_array.name(), uutRates.toArray(new String[uutRates.size()]));

         // For compatibility with older test runners
         if (uutPaths.size() > 0) {
            store.put(OteRunTestsKeys.uut_path.name(), uutPaths.get(0));
         } else {
            // Doesn't matter as long as the first char is a $. 
            store.put(OteRunTestsKeys.uut_path.name(), "$$$Default");
         }

         store.put(OteRunTestsKeys.distributionStatement.name(),
            testManagerEditor.getTestManagerModel().getDistributionStatement());

         String consoleFilePath = propertyStore.get(StorageKeys.SERVER_OUTPUT_FILE_PATH_STORAGE_KEY);
         String consoleOption = propertyStore.get(StorageKeys.SERVER_OUTPUT_SELECTION_STORAGE_KEY);
         store.put(OteRunTestsKeys.uut_output_path.name(), getUutConsoleOption(consoleOption, consoleFilePath));
         store.put("logging.level", propertyStore.get(TestManagerStorageKeys.LOGGING_LEVEL_KEY));
      }
      return null;
   }

   private static String getDefaultParentClass(String su) {
      String parentClass = su.toUpperCase().substring(0, 1) + su.toLowerCase().substring(1);
      parentClass += "TestScript";
      return parentClass;
   }
}
