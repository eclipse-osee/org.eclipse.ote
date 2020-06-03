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
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.ConfigurationItem;
import org.eclipse.osee.ote.OTETestEnvironmentClient;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.endpoint.OteEndpointUtil;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.ui.builder.OTEPackagingBuilder;
import org.eclipse.osee.ote.ui.test.manager.connection.ScriptManager;
import org.eclipse.osee.ote.ui.test.manager.connection.ScriptQueue;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.models.ScriptModel.TestFileData;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.eclipse.ote.test.manager.editor.OteTestManagerEditor;
import org.eclipse.ote.test.manager.internal.OteTestManagerPlugin;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteScriptManager extends ScriptManager {


   @Override
   public void abortScript(boolean isBatchAbort) {
      if (commandHandle != null) {
         if (isBatchAbort) {
            commandHandle.cancelAll(true);
         } else {
            commandHandle.cancelSingle(true);
         }
      }
   }

   private ICommandHandle commandHandle;

   public OteScriptManager(TestManagerEditor testManager, StructuredViewer scriptTableViewer) {
      super(testManager, scriptTableViewer);
   }

   @Override
   public void addTestsToQueue(List<ScriptTask> scripts) {
      OteTestManagerPlugin.getInstance().getOteConsoleService().popup();
      ScriptQueue runScripts = new OteScriptQueue(scripts, getTestManagerEditor());
      new Thread(runScripts, "Run OTE Scripts").start();
   }

   @Override
   protected OteTestManagerEditor getTestManagerEditor() {
      return (OteTestManagerEditor) super.getTestManagerEditor();
   }

   private Configuration getBundleConfiguration(String classServer, File otePackagingFolder) throws Exception {
      Configuration configuration = new Configuration();
      File[] jars = otePackagingFolder.listFiles(new FilenameFilter(){
         @Override
         public boolean accept(File arg0, String arg1) {
            return arg1.endsWith(".jar");
         }
      });
      for (File jar : jars) {
         try{
            ConfigurationItem item = new ConfigurationItem(classServer + jar.getName(), "1.0", jar.getName(), Lib.fileToString(new File(jar.getAbsolutePath() + ".md5")), false);
            configuration.addItem(item);
         } catch (IOException ex) {
            OseeLog.log(OTERunTestsCommandFactory.class, Level.SEVERE, ex);
         }
      }
      return configuration;
   }

   public void runScripts(List<TestFileData> runthese) throws URISyntaxException, InterruptedException, ExecutionException, IOException {
      PropertyStore global = new PropertyStore();
      List<IPropertyStore> scriptsToRun = new ArrayList<>();
      OTERunTestsCommandFactory.buildGlobalAndScriptProperties(global,
            scriptsToRun,
            getSessionKey(),
            getTestManagerEditor().getPropertyStore(),
            getTestManagerEditor().getScriptClassServer().getClassServerPath(),
            getTestManagerEditor().getTestManagerModel().getUUTs(),
            getTestManagerEditor().getTestManagerModel().getDistributionStatement(),
            runthese);
      OTETestEnvironmentClient testClient = new OTETestEnvironmentClient(ServiceUtility.getService(OteUdpEndpoint.class), OteEndpointUtil.getAddress((String)getTestManagerEditor().getConnector().getProperty("oteUdpEndpoint", "")));
      commandHandle = testClient.runScripts(getSessionKey(), global, scriptsToRun, OTEPackagingBuilder.isOTEBuilderActive(), OTEPackagingBuilder.getWorkspaceArchiveFolder(), null, getTestManagerEditor().getConnector().getUniqueServerId(), null);
   }

}
