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

/**
 * Enumeration used to indicate whether the option value should be obtained from the command line, an environment
 * variable, or a property.
 * 
 * @author Loren K. Ashley
 */

enum OptionType {

   /**
    * Indicates the command line arguments should be tried to obtain a value for the option.
    */

   COMMAND_LINE,

   /**
    * Indicates the environment variables should be tried to obtain a value for the option.
    */

   ENVIRONMENT,

   /**
    * Indicates the properties should be tried to obtain a value for the option.
    */

   PROPERTY;
}
