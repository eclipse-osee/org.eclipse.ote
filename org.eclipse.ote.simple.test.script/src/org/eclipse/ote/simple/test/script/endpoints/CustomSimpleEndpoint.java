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
package org.eclipse.ote.simple.test.script.endpoints;

import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ote.rest.OteRestEndpoint;
import org.eclipse.osee.ote.rest.OteRestResponse;
import org.eclipse.ote.simple.test.environment.SimpleOteApi;
import org.eclipse.ote.simple.test.environment.rest.Simple1Endpoint;

/**
 * @author Michael P. Masterson
 */
public class CustomSimpleEndpoint extends OteRestEndpoint {

   public CustomSimpleEndpoint(SimpleOteApi api) {
      super(api, Simple1Endpoint.ENDPOINT_ID);
   }

   public OteRestResponse getCustomData() {
      URI target = UriBuilder.fromUri(baseUri).path("random").path("path").build();
      OteRestResponse response = super.performGetRequest(target, MediaType.APPLICATION_JSON);
      return response;
   }

}
