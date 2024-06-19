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

package org.eclipse.osee.ote.core.framework.outfile;

import java.util.logging.LogRecord;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.OteLevel;
import org.eclipse.osee.ote.core.framework.outfile.xml.SystemInfo;
import org.eclipse.osee.ote.core.framework.outfile.xml.TestPointResults;
import org.eclipse.osee.ote.core.framework.outfile.xml.TimeSummary;
import org.eclipse.osee.ote.core.log.record.ScriptResultRecord;
import org.eclipse.osee.ote.properties.OtePropertiesCore;

/**
 * This class collects info from the outfile records that will be used in calling class to create the
 * TestRunTransactionEndpoint.json file.
 *
 * @author Andy Jury
 */
public class TestRunTransactionEndpointPublisher {

   private final String DEFAULT_BRANCH_NAME = "no_ote_branch_provided";
   private final TestRunTransactionEndpointJsonPojo testRunTransactionEndpointJsonPojo =
      new TestRunTransactionEndpointJsonPojo();
   private String testRunTransactionEndpointJson = new String();
   private final String branchName = OtePropertiesCore.oseeBranchId.getValue(DEFAULT_BRANCH_NAME); //This should be connected to user supplied branch in future
   private final CreateArtifactsJsonPojo createArtifacts = new CreateArtifactsJsonPojo();

   public String publish(LogRecord logRecord) {

      if (branchName.equals(DEFAULT_BRANCH_NAME)) {
         OseeLog.log(getClass(), OteLevel.WARNING, "No OTE Branch was provided to send OSEE outfile.");
      }
      testRunTransactionEndpointJsonPojo.setBranch(branchName);
      testRunTransactionEndpointJsonPojo.setTxComment("Created by TestRunTransactionEndpointPublisher");

      createArtifacts.setTypeName("Test Run");

      if (logRecord instanceof ScriptResultRecord) {
         ScriptResultRecord srr = (ScriptResultRecord) logRecord;
         createArtifacts.setName(logRecord.getMessage());

         for (XmlizableStream rec : srr.getResults()) {
            if (rec instanceof TimeSummary) {
               TimeSummary timeSummary = (TimeSummary) rec;
               addAttributeToCreateArtifacts("Elapsed Date", timeSummary.getElapsed());
               addAttributeToCreateArtifacts("End Date", Long.toString(timeSummary.getEndTime().getTime()));
               addAttributeToCreateArtifacts("Start Date", Long.toString(timeSummary.getStartTime().getTime()));
            } else if (rec instanceof TestPointResults) {
               TestPointResults testPointResults = (TestPointResults) rec;

               addAttributeToCreateArtifacts("Passed Count", Integer.toString(testPointResults.getPasses()));
               addAttributeToCreateArtifacts("Failed Count", Integer.toString(testPointResults.getFails()));
               addAttributeToCreateArtifacts("Script Aborted", Boolean.toString(testPointResults.isAborted()));
               addAttributeToCreateArtifacts("Total Test Points", Integer.toString(testPointResults.getTotal()));
            } else if (rec instanceof SystemInfo) {
               SystemInfo systemInfo = (SystemInfo) rec;

               addAttributeToCreateArtifacts("OS Name", (systemInfo.getOperatingSystemName()));
               addAttributeToCreateArtifacts("OS Architecture", (systemInfo.getOperatingSystemArchitecture()));
               addAttributeToCreateArtifacts("OS Version", (systemInfo.getOperatingSystemVersion()));
               addAttributeToCreateArtifacts("OSEE Version", (systemInfo.getOseeCodeVersion()));
               addAttributeToCreateArtifacts("OSEE Server Title", (systemInfo.getOteServerTitle()));
            }

         }
      }

      testRunTransactionEndpointJsonPojo.addCreateArtifacts(createArtifacts);

      testRunTransactionEndpointJson = JsonUtil.toJson(testRunTransactionEndpointJsonPojo);
      return testRunTransactionEndpointJson;
   }

   private void addAttributeToCreateArtifacts(String typeName, String value) {
      AttributeJsonPojo attribute = new AttributeJsonPojo();
      attribute.setTypeName(typeName);
      attribute.setValue(value);
      createArtifacts.addAttribute(attribute);

   }
}
