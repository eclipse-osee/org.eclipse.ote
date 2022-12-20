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

   public OteRemoteTerminalResponse(String stdOut) {
      this.stdOut = stdOut;
   }

   /**
    * Verifies the standard output of the command issued to the remote terminal is
    * exactly equal to the expected parameter
    * 
    * @param accessor For logging
    * @param expected Expected output
    */
   public void verifyStandardOut(ITestAccessor accessor, String expected) {
      accessor.getTestScript().logTestPoint(stdOut.equals(expected), "verifyStandardOutput", expected, stdOut);
   }

   /**
    * Verifies the standard output of the command issued to the remote terminal
    * contains the expected substring parameter
    * 
    * @param accessor  For logging
    * @param subString Expected substring
    */
   public void verifyStandardOutContains(ITestAccessor accessor, String subString) {
      boolean contains = stdOut.contains(subString);
      accessor.getTestScript().logTestPoint(contains, "verifyStandardOutputContains", subString, stdOut);
   }

   /**
    * Returns the standard output after a command is issued to the open remote
    * terminal
    * 
    * @return Standard output string
    */
   public String getStandardOutput() {
      return stdOut;
   }
}
