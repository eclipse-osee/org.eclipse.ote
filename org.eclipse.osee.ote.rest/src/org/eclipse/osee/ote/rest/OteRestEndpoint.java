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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.ote.rest.multipart.FileStreamPart;
import org.eclipse.osee.ote.rest.multipart.MultiPartMessage;
import org.eclipse.osee.ote.rest.multipart.MultipartMessageBodyWriter;

/**
 * Provides generic REST request methods for use by OTE API implementations. All REST requests will be careful to not
 * propagate HTTP exceptions from the JaxRS calls and instead wrap them in {@link OteResResponseException}.
 *
 * @author Michael P. Masterson
 */
public abstract class OteRestEndpoint {
   protected final JaxRsApi jaxRsApi;
   protected final URI baseUri;

   /**
    * @param jaxRsApi
    * @param uri Base path for this endpoint
    */
   public OteRestEndpoint(JaxRsApi jaxRsApi, URI uri) {
      this.jaxRsApi = jaxRsApi;
      this.baseUri = uri;
   }

   /**
    * @param api
    * @param endpointId
    */
   public OteRestEndpoint(OteRestApiBase api, String endpointId) {
      this(api.zzz_getJaxRsApi(), api.zzz_getRestConfig().getBaseUri(endpointId));
   }

   /**
    * This method should never throw a RuntimeException caused by HTTP issues contacting the target URI. All such
    * exceptions are wrapped in a {@link OteResResponseException} for ease of testing.
    *
    * @param target Full path to REST target
    * @param mediaType Use constants defined in {@link javax.ws.rs.core.MediaType}
    * @return Working {@link OteRestResponse} if no exceptions while performing GET, otherwise an
    * {@link OteResResponseException} that fails every verification gracefully.
    */
   protected OteRestResponse performGetRequest(URI target, String mediaType) {
      Response response;
      OteRestResponse retVal;
      try {
         response = jaxRsApi.newTargetUrl(target.toString()).request(mediaType).get();

         retVal = new OteRestResponse(response);
      } catch (RuntimeException ex) {
         retVal = new OteRestResponseException(ex);
      }
      return retVal;
   }

   /**
    * This method should never throw a RuntimeException caused by HTTP issues contacting the target URI. All such
    * exceptions are wrapped in a {@link OteResResponseException} for ease of testing.
    *
    * @param target Full path to REST target
    * @param input Data to post
    * @param fileName The simple name of the file represented in the input stream
    * @param mediaType The MIME type to be declared in the HTTP Header
    * @return Working {@link OteRestResponse} if no exceptions while performing POST, otherwise an
    * {@link OteResResponseException} that fails every verification gracefully.
    */
   protected OteRestResponse performPostFile(URI target, InputStream input, String fileName, String mediaType) {
      Response response;
      OteRestResponse retVal;

      try {
         MultiPartMessage mpMsg = new MultiPartMessage();
         mpMsg.addPart(new FileStreamPart("file", fileName, mediaType, input));

         WebTarget webTarget = jaxRsApi.newTargetUrl(target.toString());
         webTarget.register(MultipartMessageBodyWriter.class);

         Builder builder = webTarget.request();
         response = builder.post(Entity.entity(mpMsg, MediaType.MULTIPART_FORM_DATA));
         retVal = new OteRestResponse(response);
      } catch (RuntimeException ex) {
         System.err.println(ex.getLocalizedMessage());
         ex.printStackTrace();
         retVal = new OteRestResponseException(ex);
      }
      return retVal;
   }

   /**
    * Utility method for building up a multi-step URI path.
    *
    * @param args
    * @return
    */
   protected URI buildUri(String... args) {
      UriBuilder builder = UriBuilder.fromUri(baseUri);
      for (String arg : args) {
         builder.path(arg);
      }
      return builder.build();
   }
}
