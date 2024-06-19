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

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.logging.Level;
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
import org.eclipse.osee.ote.properties.OtePropertiesCore;
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
   private static final String NO_PATH = "NO_OTE_OUTFILE_PATH";
   private static final String DEFAULT_BRANCH_ID = "no_osee_branch_provided";
   private static final String DEFAULT_CI_SET_ID = "no_osee_ciSet_provided";

   private final String branchId = OtePropertiesCore.oseeBranchId.getValue(DEFAULT_BRANCH_ID);
   private final String ciSetId = OtePropertiesCore.oseeCiSetId.getValue(DEFAULT_CI_SET_ID);

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
      postTmoFile(testClassName);

      return new MethodResultImpl(ReturnCode.OK);
   }

   /**
    * Posts the TMO file to the specified endpoint.
    *
    * @param testClassName the name of the test class that corresponds to the test event.
    */

   private void postTmoFile(String testClassName) {
      if (branchId.equals(DEFAULT_BRANCH_ID)) {
         OseeLog.logf(getClass(), Level.WARNING, "No OSEE Branch ID provided, TMO file will not be uploaded to OSEE.");
         return;
      }

      if (ciSetId.equals(DEFAULT_CI_SET_ID)) {
         OseeLog.logf(getClass(), Level.WARNING, "No OSEE CI Set ID provided, TMO file will not be uploaded to OSEE.");
         return;
      }

      InputStream tmoInputStream = getTmoFileInputStream(testClassName);
      if (tmoInputStream == null) {
         OseeLog.logf(getClass(), Level.WARNING,
            "TMO InputStream is null. Cannot send TMO file for test class: " + testClassName);
         return;
      }

      OseeOutfileEndpoint endpoint = new OseeOutfileEndpoint(jaxRsApi);
      OteRestResponse response = endpoint.postTmoFile(branchId, ciSetId, tmoInputStream, testClassName);

      if (PRINT_OUTPUT_TO_CONSOLE) {
         System.out.println("TMO Response Status: " + response.getResponse().getStatus());
         System.out.println("TMO Response: " + response.getContents(String.class));
      }

      if (response.getResponse().getStatus() != OK_RESPONSE_CODE) {
         OseeLog.logf(getClass(), Level.WARNING, "TMO File not sent to OSEE. Rest response contents:\n%s",
            response.getContents(String.class));
      } else {
         OseeLog.log(getClass(), Level.INFO, "TMO File sent successfully to OSEE.");
      }
   }


   /**
    * Retrieves the input stream of the TMO file associated with the given test class name.
    *
    * @param testClassName the name of the test class
    * @return the input stream of the TMO file, or null if not found or an error occurs
    */
   private InputStream getTmoFileInputStream(String testClassName) {
      String tmoFilePath = getTmoFilePath(testClassName);
      if (tmoFilePath.equals(NO_PATH)) {
         OseeLog.logf(getClass(), Level.WARNING, "No TMO file found for test class: " + testClassName);
         return null;
      }

      try {
         return testEnv.getOutDir().toPath().resolve(tmoFilePath).toUri().toURL().openStream();
      } catch (Exception e) {
         OseeLog.log(getClass(), Level.WARNING, "Error opening TMO file input stream: " + e.getMessage());
         e.printStackTrace();
         return null;
      }
   }

   private String getTmzPath(String testClassName) {
      String tmzFilePath = NO_PATH;
      File outputDir = testEnv.getOutDir();

      String[] tmzList = outputDir.list(new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            return name.endsWith("z") && name.startsWith(testClassName);
         }
      });

      // If there is more than one this will only choose the first one
      if (tmzList.length > 0) {
         tmzFilePath = Paths.get(outputDir.getAbsolutePath(), tmzList[0]).toString();
      }

      return tmzFilePath;
   }

   private String getTmoFilePath(String testClassName) {
      String tmoFilePath = NO_PATH;
      File outputDir = testEnv.getOutDir();

      String[] tmoList = outputDir.list(new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            return (name.endsWith(".tmo") || name.endsWith(".TMO")) && name.startsWith(testClassName);
         }
      });

      // If there is more than one, this will only choose the first one
      if (tmoList != null && tmoList.length > 0) {
         tmoFilePath = Paths.get(outputDir.getAbsolutePath(), tmoList[0]).toString();
      }

      return tmoFilePath;
   }
}
