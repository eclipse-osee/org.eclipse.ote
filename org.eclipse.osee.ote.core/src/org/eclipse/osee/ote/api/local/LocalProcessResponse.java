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

import java.util.Arrays;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
public class LocalProcessResponse {

   public final static int OK_CODE = 0;

   private final String[] command;
   private final String output;
   private final String error;
   private final int exitCode;

   public LocalProcessResponse(String[] command, String output, String error, int exitCode) {
      this.command = command;
      this.output = output;
      this.error = error;
      this.exitCode = exitCode;
   }

   /**
    * @return The command array as a string
    */
   public String getCommandStr() {

      return Arrays.toString(this.command);
   }

   /**
    * @return the output
    */
   public String getOutput() {
      return output;
   }

   /**
    * @return the error
    */
   public String getError() {
      return error;
   }

   /**
    * @return the exitCode
    */
   public int getExitCode() {
      return exitCode;
   }

   /**
    * Verifies the exit code from the local process is exactly equal to the code parameter
    * 
    * @param accessor For logging
    * @param expectedCode Expected status code
    */
   public void verifyExitCode(ITestAccessor accessor, int expectedCode) {

      accessor.getTestScript().logTestPoint(exitCode == expectedCode,
                                            "verifyExitCode:" + getCommandStr(),
                                            Integer.toString(expectedCode),
                                            Integer.toString(exitCode));
   }

   private String surroundInQuotes(String str) {
      return String.format("\"%s\"", str);
   }

   /**
    * Verifies the output stream from the local process contains the provided string
    * 
    * @param accessor For logging
    * @param str Substring to match in the output stream
    */
   public void verifyOutputStreamContains(ITestAccessor accessor, String str) {

      if (Strings.isInValid(this.output)) {
         accessor.getTestScript().logTestPoint(false, "verifyOutputStreamContains:" + getCommandStr(),
                                               surroundInQuotes(str), surroundInQuotes(output));
      }
      else {

         accessor.getTestScript().logTestPoint(this.output.contains(str),
                                               "verifyOutputStreamContains:" + getCommandStr(),
                                               surroundInQuotes(str), surroundInQuotes(output));
      }
   }

   /**
    * Verifies the error stream from the local process contains the provided string
    * 
    * @param accessor For logging
    * @param str Substring to match in the error stream
    */
   public void verifyErrorStreamContains(ITestAccessor accessor, String str) {
      if (Strings.isInValid(this.error)) {
         accessor.getTestScript().logTestPoint(false,
                                               "verifyErrorStreamContains:" + getCommandStr(),
                                               surroundInQuotes(str), surroundInQuotes(error));
      }
      else {

         accessor.getTestScript().logTestPoint(this.error.contains(str),
                                               "verifyErrorStreamContains:" + getCommandStr(),
                                               surroundInQuotes(str),
                                               surroundInQuotes(error));
      }
   }
}
