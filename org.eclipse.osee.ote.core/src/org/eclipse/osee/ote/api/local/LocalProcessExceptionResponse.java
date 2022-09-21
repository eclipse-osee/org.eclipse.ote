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
package org.eclipse.osee.ote.api.local;

import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
public class LocalProcessExceptionResponse extends LocalProcessResponse {

   private final Exception exception;

   public LocalProcessExceptionResponse(Exception exception, String[] procStr, String output,
         String error,
         int exitCode) {
      super(procStr, output, error, exitCode);
      this.exception = exception;
   }

   @Override
   public void verifyExitCode(ITestAccessor accessor, int expectedCode) {
      accessor.getTestScript().logTestPoint(false, "verifyExitCode:" + getCommandStr(),
                                            Integer.toString(expectedCode),
                                            "Exception thrown: " + exception.getLocalizedMessage());
   }

   @Override
   public void verifyOutputStreamContains(ITestAccessor accessor, String str) {
      accessor.getTestScript().logTestPoint(false, "verifyOutputStreamContains:" + getCommandStr(),
                                            str,
                                            "Exception thrown: " + exception.getLocalizedMessage());
   }

   @Override
   public void verifyErrorStreamContains(ITestAccessor accessor, String str) {
      accessor.getTestScript().logTestPoint(false, "verifyErrorStreamContains:" + getCommandStr(), str,
                                            "Exception thrown: " + exception.getLocalizedMessage());
   }

}
