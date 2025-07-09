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

package org.eclipse.ote.abstractapplication;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Interface for application classes using the {@link CommandLineParser} to parse command line options, environment
 * variables, and properties.
 * 
 * @author Loren K. Ashley
 */

public interface Application {

   /**
    * Gets the full descriptive name for the application. Used for the application name in help/usage descriptions.
    *
    * @return the applications full descriptive name.
    */

   @NonNull
   String getName();

   /**
    * Gets a short descriptive name for the application. The short name is used in exception messages.
    *
    * @return the application's short name.
    */

   @NonNull
   String getShortName();

   /**
    * Performs command line argument, environment variable, and property parsing; and then starts the application logic.
    *
    * @param args the command line arguments.
    * @throws ApplicationSetupException when an error occurred processing the command line arguments.
    * @throws NullPointerException when {@code args} or and element of {@code args} is {@code null}.
    */

   void go(@NonNull String[] args);
}

/* EOF */
