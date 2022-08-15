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

import java.net.URI;
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

   public OteRestResponse postOutfile(String jsonString) {
      URI target = UriBuilder.fromUri(baseUri).path("orcs").path("txs").build();
      OteRestResponse retVal = performPostJsonString(target, jsonString);

      return retVal;
   }

}