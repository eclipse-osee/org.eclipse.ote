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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * Wrapper class to generic {@link Response} objects provided via a REST request. This class
 * provides an API intended to simplify test script verifications.
 * 
 * @author Michael P. Masterson
 */
public class OteRestResponse {

   private final Response response;

   public OteRestResponse(Response response) {
      this.response = response;
   }

   /**
    * Verifies the response code from the REST request is exactly equal to the code parameter
    * 
    * @param accessor For logging
    * @param code Expected status code
    */
   public void verifyResponseCode(ITestAccessor accessor, Status code) {

      StatusType statusInfo = response.getStatusInfo();
      int actualCode = statusInfo.getStatusCode();
      int expectedCode = code.getStatusCode();
      accessor.getTestScript().logTestPoint(actualCode == expectedCode, "verifyResponseCode",
                                            Integer.toString(expectedCode),
                                            Integer.toString(actualCode));
   }

   /**
    * Verifies the response code family from the REST request is exactly equal to the expectedFamily
    * parameter
    * 
    * @param accessor For logging
    * @param expectedFamily Expected status code family
    */
   public void verifyResponseFamily(ITestAccessor accessor, Family expectedFamily) {
      Family actualFamily = response.getStatusInfo().getFamily();
      accessor.getTestScript().logTestPoint(actualFamily == expectedFamily, "verifyResponseFamily",
                                            expectedFamily.toString(), actualFamily.toString());
   }

   /**
    * @param <T>
    * @param clazz Type expected in the response. Must be compatible with the MediaType of the REST
    *           request or a Runtime Exception will be thrown.
    * @return The contents (body) of the REST request as the type parameter
    */
   public <T> T getContents(Class<T> clazz) {
      return response.readEntity(clazz);
   }

   /**
    * @return The raw {@link Response} object this class contains
    */
   public Response getResponse() {
      return response;
   }

   /**
    * Logs a test point verifying that the string contents of the REST Response contains the
    * substring
    * 
    * @param accessor For Logging
    * @param subString
    */
   public void verifyContentsContains(ITestAccessor accessor, String subString) {
      String content = getContents(String.class);
      boolean matches = content.contains(subString);
      accessor.getTestScript().logTestPoint(matches, "verifyContentsContains",
                                            "Contains '" + subString + "'",
                                            matches ? "FOUND" : "NOT FOUND");
   }

   /**
    * Logs a test point verifying that the string contents of the REST Response is exactly equal to
    * the expected string.
    * 
    * @param accessor For Logging
    * @param expected
    */
   public <T> void verifyContentsEquals(ITestAccessor accessor, T expected) {
      Object actual = response.readEntity(expected.getClass());
      accessor.getTestScript().logTestPoint(expected.equals(actual), "verifyContentsEquals",
                                            expected.toString(), actual.toString());
   }

}
