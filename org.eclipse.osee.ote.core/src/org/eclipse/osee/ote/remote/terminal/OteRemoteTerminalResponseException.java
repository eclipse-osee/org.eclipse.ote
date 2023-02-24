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
public class OteRemoteTerminalResponseException extends OteRemoteTerminalResponse {

   private final Exception ex;

   public OteRemoteTerminalResponseException(Exception ex) {
      super();
      this.ex = ex;
   }

   @Override
   public void verifyStandardOut(ITestAccessor accessor, String expected) {
      accessor.getTestScript().logTestPoint(false, "verifyStandardOut", expected,
            "Exception thrown: " + ex.getLocalizedMessage());
   }

   @Override
   public void verifyStandardOutContains(ITestAccessor accessor, String subString) {
      accessor.getTestScript().logTestPoint(false, "verifyStandardOutContains", subString,
            "Exception thrown: " + ex.getLocalizedMessage());
   }

   @Override
   public void verifyStandardError(ITestAccessor accessor, String expected) {
      accessor.getTestScript().logTestPoint(false, "verifyStandardError", expected,
            "Exception thrown: " + ex.getLocalizedMessage());
   }

   @Override
   public void verifyStandardErrorContains(ITestAccessor accessor, String subString) {
      accessor.getTestScript().logTestPoint(false, "verifyStandardErrorContains", subString,
            "Exception thrown: " + ex.getLocalizedMessage());
   }

   @Override
   public void verifyExitCode(ITestAccessor accessor, int expected) {
      accessor.getTestScript().logTestPoint(false, "verifyExitCode", String.valueOf(expected),
            "Exception thrown: " + ex.getLocalizedMessage());
   }
}
