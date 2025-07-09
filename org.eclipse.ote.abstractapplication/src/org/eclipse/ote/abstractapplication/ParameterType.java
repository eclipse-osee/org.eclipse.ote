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
 * Enumeration used to define if a command line parameter is optional or required.
 * 
 * @author Loren K. Ashley
 */

public enum ParameterType {

   /**
    * The command line parameter is optional.
    */

   OPTIONAL,

   /**
    * The command line parameter is required.
    */

   REQUIRED;

   /**
    * Predicate to determine if the enumeration member is {@link #OPTIONAL}.
    * 
    * @return <code>true</code> when the enumeration member is {@link #OPTIONAL}; otherwise, <code>false</code>.
    */

   boolean isOptional() {
      return this == OPTIONAL;
   }

   /**
    * Predicate to determine if the enumeration member is {@link #REQUIRED}.
    * 
    * @return <code>true</code> when the enumeration member is {@link #REQUIRED}; otherwise, <code>false</code>.
    */

   boolean isRequired() {
      return this == REQUIRED;
   }

}
