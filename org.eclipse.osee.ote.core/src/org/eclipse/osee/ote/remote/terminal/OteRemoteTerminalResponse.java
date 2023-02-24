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
package org.eclipse.osee.ote.remote.terminal;

import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Nydia Delgado
 */
public class OteRemoteTerminalResponse {

   private final String stdOut;
   private final String stdErr;
   private final int exitCode;

   public OteRemoteTerminalResponse(String stdOut, String stdErr, int exitCode) {
      this.stdOut = stdOut;
      this.stdErr = stdErr;
      this.exitCode = exitCode;
   }

   public OteRemoteTerminalResponse() {
      stdOut = "";
      stdErr = "";
      exitCode = 0;
   }

   /**
    * Verifies that the standard output of the remote terminal response is exactly
    * equal to the expected parameter
    * 
    * @param accessor For logging
    * @param expected Expected output
    */
   public void verifyStandardOut(ITestAccessor accessor, String expected) {
      accessor.getTestScript().logTestPoint(stdOut.equals(expected), "verifyStandardOutput", expected, stdOut);
   }

   /**
    * Verifies that the standard output of the remote terminal response contains
    * the expected substring parameter
    * 
    * @param accessor  For logging
    * @param subString Expected substring
    */
   public void verifyStandardOutContains(ITestAccessor accessor, String subString) {
      boolean contains = stdOut.contains(subString);
      accessor.getTestScript().logTestPoint(contains, "verifyStandardOutputContains", subString, stdOut);
   }

   /**
    * Verifies that the standard error of the remote terminal response is exactly
    * equal to the expected parameter
    * 
    * @param accessor
    * @param expected
    */
   public void verifyStandardError(ITestAccessor accessor, String expected) {
      accessor.getTestScript().logTestPoint(stdErr.equals(expected), "verifyStandardError", expected, stdErr);
   }

   /**
    * Verifies that the standard error of the remote terminal response contains the
    * expected substring parameter
    * 
    * @param accessor
    * @param subString
    */
   public void verifyStandardErrorContains(ITestAccessor accessor, String subString) {
      boolean contains = stdErr.contains(subString);
      accessor.getTestScript().logTestPoint(contains, "verifyStandardErrorContains", subString, stdErr);
   }

   /**
    * Verifies that the exit code of the remote terminal response is exactly equal
    * to the expected parameter
    * 
    * @param accessor
    * @param expected
    */
   public void verifyExitCode(ITestAccessor accessor, int expected) {
      accessor.getTestScript().logTestPoint(exitCode == expected, "verifyExitCode", String.valueOf(expected),
            String.valueOf(exitCode));
   }

   /**
    * Returns the standard output of the remote terminal response
    * 
    * @return Standard output string
    */
   public String getStdOut() {
      return stdOut;
   }

   /**
    * Returns the standard error of the remote terminal response
    * 
    * @return Standard error string
    */
   public String getStdErr() {
      return stdErr;
   }

   /**
    * Returns the exit code of the remote terminal response
    * 
    * 
    * @return
    */
   public int getExitCode() {
      return exitCode;
   }
}
