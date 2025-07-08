/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ote.core;

/**
 * An {@link Error} that can be thrown by a test script when it has detected that it has been requested to abort.
 * <p>
 * The use of checked exceptions for all conditions that can go awry in a test script would become unwieldy. Often, in
 * test code when something has gone wrong or cannot be completed a checked exception is wrapped into a runtime
 * exception or a new runtime exception is thrown. At higher levels in the test code a general exception catch all is
 * used to trap all exceptions to prevent the test script from being exited and instead a request for user interaction
 * is made. Then pending the result of the user interaction a test point is passed or failed and the test script is
 * allowed to continue.
 * <p>
 * An {@link Error} will not be caught by a general &quot;catch( Exception e )&quot; block. This will allow the bypass
 * of general exception catch blocks that might result in a request for user interaction and allow for a direct exit of
 * the test script.
 *
 * @author Loren K. Ashley
 */

public class TestAbortError extends Error {

   /**
    * Serialization identifier.
    */

   private static final long serialVersionUID = -5526786188448949424L;

   /**
    * Saves the name of the current thread. For test script threads the name will indicated the name of the test script.
    */

   private final String threadName;

   /**
    * Creates a new {@link TestAbortError} and saves the name of the current thread.
    */

   public TestAbortError() {
      this.threadName = Thread.currentThread().getName();
   }

   /**
    * Gets the name of the thread that created the {@link TestAbortError}.
    *
    * @return the name of the creating thread.
    */

   public String getThreadName() {
      return this.threadName;
   }

}
