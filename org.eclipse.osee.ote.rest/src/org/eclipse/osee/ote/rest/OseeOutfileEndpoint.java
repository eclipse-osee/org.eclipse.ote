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

import java.io.InputStream;
import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.OseeClient;

/**
 * @author Nydia Delgado
 */
public class OseeOutfileEndpoint extends OteRestEndpoint {
   public OseeOutfileEndpoint(JaxRsApi jaxRsApi) {
      super(jaxRsApi, URI.create(OseeClient.getOseeApplicationServer()));
   }

   /**
    * Posts the outfile data as a JSON string to the server.
    *
    * @param jsonString the JSON string representing the outfile data
    * @return the response from the server
    */
   public OteRestResponse postJsonOutfile(String jsonString) {
      URI target = UriBuilder.fromUri(baseUri).path("orcs").path("txs").build();
      OteRestResponse retVal = performPostJsonString(target, jsonString, true);

      return retVal;
   }

   /**
    * Posts a TMO file to the server.
    *
    * @param branchId the branch ID
    * @param ciSetId the CI set ID
    * @param input the input stream of the file
    * @param tmoFilename the name of the file
    * @return the response from the server
    */
   public OteRestResponse postTmoFile(String branchId, String ciSetId, InputStream input, String tmoFilename) {
      URI target =
         UriBuilder.fromUri(baseUri).path("script").path("tmo").path(branchId).path("import").path("file").path(
            ciSetId).build();
      OteRestResponse retVal = performPostFile(target, input, tmoFilename, "application/octet-stream", true);
      return retVal;
   }

   /**
    * Gets the branch id to publish a TMO file to the server.
    *
    * @param branchId the branch ID
    * @param ciConfigId is the ID of the artifact with the current branch info
    * @param ciConfigAttributeId is the ID of the attribute that contains the branch id of where tmos should be written
    * @return the response from the server
    */
   public OteRestResponse getCiBranchId(String branchId, String ciConfigId, String ciConfigAttributeId) {
      URI target =
         UriBuilder.fromUri(baseUri).path("orcs").path("branch").path(branchId).path("artifact").path(ciConfigId).path(
            "attribute").path(ciConfigAttributeId).build();
      OteRestResponse retVal = performGetRequest(target, MediaType.APPLICATION_JSON, true);
      return retVal;
   }

}