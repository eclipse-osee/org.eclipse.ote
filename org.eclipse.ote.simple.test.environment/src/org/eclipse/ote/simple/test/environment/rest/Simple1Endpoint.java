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
package org.eclipse.ote.simple.test.environment.rest;

import java.io.InputStream;
import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.rest.OteRestConfigurationProvider;
import org.eclipse.osee.ote.rest.OteRestEndpoint;
import org.eclipse.osee.ote.rest.OteRestResponse;

/**
 * This is an example of an OteRestEndpoint implementation. These implementations should provide separate REST requests
 * for each sub-path of the base URI.
 *
 * @author Michael P. Masterson
 */
public class Simple1Endpoint extends OteRestEndpoint {
   public static final String ENDPOINT_ID = "SimpleEndpoint1";

   public Simple1Endpoint(JaxRsApi jaxRsApi, OteRestConfigurationProvider restConfig) {
      super(jaxRsApi, restConfig.getBaseUri(ENDPOINT_ID));
   }

   /**
    * @param accessor For logging
    * @return The response containing the result of a GET request for DataOne
    */
   public OteRestResponse getDataOne(ITestAccessor accessor) {
      accessor.getLogger().methodCalledOnObject(accessor, this.getClass().getSimpleName(), new MethodFormatter());

      URI target = UriBuilder.fromUri(baseUri).path("otemaster").path("servers").build();
      String mediaType = MediaType.APPLICATION_JSON;

      OteRestResponse retVal = super.performGetRequest(target, mediaType);

      accessor.getLogger().methodEnded(accessor);
      return retVal;
   }

   public OteRestResponse postFile(ITestAccessor accessor, InputStream input) {
      accessor.getLogger().methodCalledOnObject(accessor, this.getClass().getSimpleName(),
         new MethodFormatter().add(input));

      URI target = UriBuilder.fromUri(baseUri).path("ote").path("post_it").build();

      OteRestResponse retVal = super.performPostFile(target, input, "fakeFileName", MediaType.APPLICATION_OCTET_STREAM);

      accessor.getLogger().methodEnded(accessor);
      return retVal;
   }
}
