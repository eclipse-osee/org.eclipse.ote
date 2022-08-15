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

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Andy Jury
 */
public class TestRunTransactionEndpointJsonPojoTest {

   @Test
   public void testTestRunTransactionEndpointsJsonPojoTest() throws Exception {

      String path = "data/testRunTransactionEndpoint.json";
      String jsonFileString = new String(Files.readAllBytes(Paths.get(path)));

      TestRunTransactionEndpointJsonPojo testRunTransactionEndpoint = new TestRunTransactionEndpointJsonPojo();

      testRunTransactionEndpoint.setBranch("testRunBranch");
      testRunTransactionEndpoint.setTxComment("Created by ScriptJsonOutLogHandler");

      CreateArtifactsJsonPojo createArtifacts = new CreateArtifactsJsonPojo();
      createArtifacts.setTypeName("Test Run");
      createArtifacts.setName("TBD");

      AttributeJsonPojo attribute1 = new AttributeJsonPojo();
      attribute1.setTypeName("Outfile URL");
      attribute1.setValue("TBD");

      AttributeJsonPojo attribute2 = new AttributeJsonPojo();
      attribute2.setTypeName("ElapsedDate");
      attribute2.setValue("0:08:15");

      createArtifacts.addAttribute(attribute1);
      createArtifacts.addAttribute(attribute2);

      testRunTransactionEndpoint.addCreateArtifacts(createArtifacts);

      ObjectMapper objectMapper = new ObjectMapper();
      String inputJson = objectMapper.writeValueAsString(testRunTransactionEndpoint);

      assertEquals(jsonFileString, inputJson);
   }
}
