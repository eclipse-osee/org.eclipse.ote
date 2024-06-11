/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ote.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.enums.OteRunTestsKeys;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.osee.ote.core.framework.ITestLifecycleListener;
import org.eclipse.osee.ote.core.framework.MethodResultImpl;
import org.eclipse.osee.ote.core.framework.ReturnCode;
import org.eclipse.osee.ote.core.framework.event.IEventData;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * @author Nydia Delgado
 */
@Component(service = {OseeOutfileSender.class}, immediate = true)
public final class OseeOutfileSender implements ITestLifecycleListener {

   private static boolean PRINT_OUTPUT_TO_CONSOLE = false;
   private static int OK_RESPONSE_CODE = 200;
   public static final String JSON_OUTFILE_FILE_NAME = "TestRunTransactionEndpoint.json";
   private static final String NO_PATH = "NO_OTE_OUTFILE_PATH";

   private TestEnvironmentInterface testEnv;
   private JaxRsApi jaxRsApi;

   @Activate
   public void activate() {
      IRunManager runManager = testEnv.getRunManager();
      runManager.addListener(this);
      System.out.println("All dependencies for OseeOutfileSender are met");
   }

   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void bindTestEnv(TestEnvironmentInterface testEnv) {
      this.testEnv = testEnv;
   }

   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void bindJaxRs(JaxRsApi jaxRsApi) {
      this.jaxRsApi = jaxRsApi;
   }

   @Override
   public IMethodResult preInstantiation(IEventData eventData, TestEnvironment env) {
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult postInstantiation(IEventData eventData, TestEnvironment env) {
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult preDispose(IEventData eventData, TestEnvironment env) {
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult postDispose(IEventData eventData, TestEnvironment env) {
      String testClassName = eventData.getProperties().get(OteRunTestsKeys.testClass.name());
      postOutfile(testClassName);
      return new MethodResultImpl(ReturnCode.OK);
   }

   private void postOutfile(String testClassName) {
      String jsonString = "";
      String tmzPath = getTmzPath(testClassName);
      try (ZipFile tmzFile = new ZipFile(tmzPath)) {
         Enumeration<? extends ZipEntry> tmzEntries = tmzFile.entries();

         while (tmzEntries.hasMoreElements()) {
            ZipEntry entry = tmzEntries.nextElement();
            InputStream input = tmzFile.getInputStream(entry);
            if (entry.getName().equals(JSON_OUTFILE_FILE_NAME)) {
               jsonString = new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.joining("\n"));
               if (PRINT_OUTPUT_TO_CONSOLE) {
                  System.out.println(String.format("Contents of %s: %s", entry.getName(), jsonString));
               }
            }
         }
      } catch (Exception ex) {
         System.err.println("Error loading TMZ file at " + tmzPath);
         ex.printStackTrace();
      }

      OseeOutfileEndpoint endpoint = new OseeOutfileEndpoint(jaxRsApi);
      OteRestResponse response = endpoint.postOutfile(jsonString);

      if (PRINT_OUTPUT_TO_CONSOLE) {
         System.out.println(
            "Response Status: " + response.getResponse().getStatusInfo() + ": " + response.getResponse().getStatus());
         System.out.println("Response: " + response.getContents(String.class));
      }

      if (response.getResponse().getStatus() != OK_RESPONSE_CODE) {
         OseeLog.logf(getClass(), Level.WARNING, "OSEE Outfile not sent. Rest response contents:\n%s",
            response.getContents(String.class));
      } else {
         OseeLog.log(getClass(), Level.INFO, "OSEE Outfile sent successfully.");
      }
   }

   private String getTmzPath(String testClassName) {
      String tmzFilePath = NO_PATH;
      File outputDir = testEnv.getOutDir();
      String[] tmzList = outputDir.list(new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            boolean retVal = name.endsWith("z") && name.startsWith(testClassName);
            return retVal;
         }
      });

      // If there is more than one this will only choose the first one
      if (tmzList.length > 0) {
         tmzFilePath = Paths.get(outputDir.getAbsolutePath(), tmzList[0]).toString();
      }

      return tmzFilePath;
   }
}
