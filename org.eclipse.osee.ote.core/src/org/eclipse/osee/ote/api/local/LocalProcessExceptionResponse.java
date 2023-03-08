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

import org.eclipse.osee.framework.jdk.core.util.Strings;
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
   public void verifyExitCode(ITestAccessor accessor, String testPointName, int expectedCode) {
      accessor.getTestScript().logTestPoint(false,
              (Strings.isValid(testPointName) ? testPointName : "verifyExitCode:") + " " + getCommandStr(),
              Integer.toString(expectedCode),
              "Exception thrown: " + exception.getLocalizedMessage());
   }

   @Override
   public void verifyOutputStreamContains(ITestAccessor accessor, String testPointName, String str) {
      accessor.getTestScript().logTestPoint(false,
              (Strings.isValid(testPointName) ? testPointName : "verifyOutputStreamContains:") + " " + getCommandStr(),
              str,
              "Exception thrown: " + exception.getLocalizedMessage());
   }

   @Override
   public void verifyErrorStreamContains(ITestAccessor accessor, String testPointName, String str) {
      accessor.getTestScript().logTestPoint(false,
              (Strings.isValid(testPointName) ? testPointName : "verifyErrorStreamContains:") + " " + getCommandStr(),
              str,
              "Exception thrown: " + exception.getLocalizedMessage());
   }

}
