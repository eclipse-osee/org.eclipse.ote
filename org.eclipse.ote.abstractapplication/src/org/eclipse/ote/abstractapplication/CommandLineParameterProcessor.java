/*********************************************************************
 * Copyright (c) 2025 Boeing
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.abstractapplication;

import org.eclipse.jdt.annotation.NonNull;

/**
 * A functional interface for the method used to process a command line argument and then pass the processed value to
 * the {@link Application}.
 * 
 * @author Loren K. Ashley
 */

@FunctionalInterface
public interface CommandLineParameterProcessor {
   /**
    * @param application the {@link Application} whose command line arguments are being processed.
    * @param commandLineParameterToken the token for the command line argument to be processed.
    * @param message a {@link StringBuilder} that processing errors may be appended to.
    * @return {@code true} when the command line argument processing succeeded; otherwise, {@code false}.
    */
   boolean process(@NonNull Application application, @NonNull CommandLineParameterToken commandLineParameterToken, @NonNull StringBuilder message);
}
