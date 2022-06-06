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

import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * This is intended to be used as a support class wrapping any HTTP exceptions (like Host Not Found)
 * when issuing REST requests. This will make it easier for test script verifications.
 * 
 * @author Michael P. Masterson
 */
public class OteRestResponseException extends OteRestResponse {

   private final Exception ex;

   /**
    * @param ex
    * @param response
    */
   public OteRestResponseException(Exception ex) {
      super(null);
      this.ex = ex;
   }

   @Override
   public void verifyResponseCode(ITestAccessor accessor, Status code) {
      accessor.getTestScript().logTestPoint(false, "verifyResponseCode", code.toString(),
                                            "Exception thrown: " + ex.getLocalizedMessage());
   }

   @Override
   public void verifyResponseFamily(ITestAccessor accessor, Family expectedFamily) {
      accessor.getTestScript().logTestPoint(false, "verifyResponseFamily",
                                            expectedFamily.toString(),
                                            "Exception thrown: " + ex.getLocalizedMessage());
   }

   @Override
   public <T> T getContents(Class<T> clazz) {
      return null;
   }

   @Override
   public Response getResponse() {
      return super.getResponse();
   }

   @Override
   public void verifyContentsContains(ITestAccessor accessor, String subString) {
      accessor.getTestScript().logTestPoint(false, "verifyContentsContains", subString,
                                            "Exception thrown: " + ex.getLocalizedMessage());
   }

   @Override
   public <T> void verifyContentsEquals(ITestAccessor accessor, T expected) {
      accessor.getTestScript().logTestPoint(false, "verifyContentsEquals", expected.toString(),
                                            "Exception thrown: " + ex.getLocalizedMessage());
   }

}
