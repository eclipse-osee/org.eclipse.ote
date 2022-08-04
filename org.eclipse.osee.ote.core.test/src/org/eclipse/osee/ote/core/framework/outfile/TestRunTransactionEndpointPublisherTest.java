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

import static org.junit.Assert.assertEquals;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

/**
 * @author Andy Jury
 */
public class TestRunTransactionEndpointPublisherTest {

   @Test
   public void testTestRunTransactionEndpointPublisher() throws IOException {

      String path = "data/testRunTransactionEndpointPublisher.json";
      String jsonFileString = new String(Files.readAllBytes(Paths.get(path)));

      TestRunTransactionEndpointJsonPojo testRunTransactionEndpoint = new TestRunTransactionEndpointJsonPojo();

      testRunTransactionEndpoint.setBranch("ote_test_run_transaction_endpoint_HARDCODED");
      testRunTransactionEndpoint.setTxComment("Created by TestRunTransactionEndpointPublisher");

      CreateArtifactsJsonPojo createArtifacts = new CreateArtifactsJsonPojo();
      createArtifacts.setTypeName("TestRun");
      createArtifacts.setName("org.eclipse.ote.simple.test.script.SimpleTestScript");

      AttributeJsonPojo attribute1 = new AttributeJsonPojo();
      attribute1.setTypeName("Elapsed Date");
      attribute1.setValue("0:01:48");
      createArtifacts.addAttribute(attribute1);

      AttributeJsonPojo attribute2 = new AttributeJsonPojo();
      attribute2.setTypeName("End Date");
      attribute2.setValue("Thu May 19 16:58:44 MST 2022");
      createArtifacts.addAttribute(attribute2);

      AttributeJsonPojo attribute3 = new AttributeJsonPojo();
      attribute3.setTypeName("Start Date");
      attribute3.setValue("Thu May 19 16:56:55 MST 2022");
      createArtifacts.addAttribute(attribute3);

      AttributeJsonPojo attribute4 = new AttributeJsonPojo();
      attribute4.setTypeName("Passed");
      attribute4.setValue("1");
      createArtifacts.addAttribute(attribute4);

      AttributeJsonPojo attribute5 = new AttributeJsonPojo();
      attribute5.setTypeName("Failed");
      attribute5.setValue("0");
      createArtifacts.addAttribute(attribute5);

      AttributeJsonPojo attribute6 = new AttributeJsonPojo();
      attribute6.setTypeName("Script Aborted");
      attribute6.setValue("true");
      createArtifacts.addAttribute(attribute6);

      AttributeJsonPojo attribute7 = new AttributeJsonPojo();
      attribute7.setTypeName("Total Test Points");
      attribute7.setValue("1");
      createArtifacts.addAttribute(attribute7);

      AttributeJsonPojo attribute8 = new AttributeJsonPojo();
      attribute8.setTypeName("OS Name");
      attribute8.setValue("Windows 10");
      createArtifacts.addAttribute(attribute8);

      AttributeJsonPojo attribute9 = new AttributeJsonPojo();
      attribute9.setTypeName("OS Architecture");
      attribute9.setValue("amd64");
      createArtifacts.addAttribute(attribute9);

      AttributeJsonPojo attribute10 = new AttributeJsonPojo();
      attribute10.setTypeName("OS Version");
      attribute10.setValue("10.0");
      createArtifacts.addAttribute(attribute10);

      AttributeJsonPojo attribute11 = new AttributeJsonPojo();
      attribute11.setTypeName("OSEE Version");
      attribute11.setValue("1.0.0.v202202031707-DEV");
      createArtifacts.addAttribute(attribute11);

      AttributeJsonPojo attribute12 = new AttributeJsonPojo();
      attribute12.setTypeName("OSEE Server Title");
      attribute12.setValue("OTE_CI");
      createArtifacts.addAttribute(attribute12);

      testRunTransactionEndpoint.setCreateArtifacts(createArtifacts);

      ObjectMapper objectMapper = new ObjectMapper();
      String inputJson = objectMapper.writeValueAsString(testRunTransactionEndpoint);

      assertEquals(jsonFileString, inputJson);
   }

}
