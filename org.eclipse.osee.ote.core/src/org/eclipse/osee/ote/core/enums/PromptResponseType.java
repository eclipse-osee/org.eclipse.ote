/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.core.enums;

/**
 * @author Andrew M. Finkbeiner
 */
public enum PromptResponseType {

   NONE,
   /**
    * Wait for the a response from the user confirming that they have started the debug uut.
    */
   UUT_DEBUG_RESPONSE,
   /**
    * Pause script execution until a response is recieved from a client.
    */
   SCRIPT_PAUSE,
   PASS_FAIL,
   SCRIPT_STEP,
   USER_INPUT,
   YES_NO;

}
