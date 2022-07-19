/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ote.core.environment;

import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * This interface is generally meant to provide test specific API. It is preferred to provide this
 * rather than access to the entire test environment.
 *
 * @author Michael P. Masterson
 */
public interface OteApi {
   /**
    * Logs a test point to output results.
    * 
    * @param isPassed True if passing point
    * @param testPointName Human readable test point name
    * @param expected Human readable expected value
    * @param actual Human readable actual value
    */
   void logTestPoint(boolean isPassed, String testPointName, String expected, String actual);

   /**
    * Logs a test point to output results.
    * 
    * @param isPassed True if passing point
    * @param testPointName Human readable test point name
    * @param expected Expected value as NamedId
    * @param actual Actual value as NamedId
    */
   void logTestPoint(boolean isPassed, String testPointName, NamedId expected, NamedId actual);

   /**
    * This method will display the message input to the console
    * 
    * @param promptMessage
    */
   void prompt(String promptMessage);

   /**
    * This method will display the message input to the console. It also prompts & enables the user
    * with a dialog box to interact with DTE.
    * 
    * @param promptMessage
    */
   String promptInput(String promptMessage);

   /**
    * This method will display the message input to the console. It also pauses the script running,
    * and will prompt the user with a dialog box to continue on with the running of the script.
    * 
    * @param promptMessage
    */
   void promptPause(String promptMessage);

   /**
    * This method will display the message input to the console. It will also prompt the user with a
    * dialog box to input whether the condition passed or failed.
    * 
    * @param promptMessage
    */
   void promptPassFail(String promptMessage);

   /**
    * Pauses the test thread for provided milliseconds
    * 
    * @param milliseconds time to pause
    */
   void testWait(int milliseconds);

}
