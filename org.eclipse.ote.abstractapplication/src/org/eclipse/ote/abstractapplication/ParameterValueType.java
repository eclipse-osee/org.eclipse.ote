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
 * Enumeration used to define a command line parameter as not having a value, having an optional value, or having a
 * required value.
 * 
 * @author Loren K. Ashley
 */

public enum ParameterValueType {

   /**
    * Command line parameter does not have a value.
    */

   NONE,

   /**
    * Command line parameter has an optional value.
    */

   OPTIONAL,

   /**
    * Command line parameter has a required value.
    */

   REQUIRED;

   /**
    * Predicate to determine if the enumeration member is {@link #NONE}.
    * 
    * @return <code>true</code> when the enumeration member is {@link #NONE}; otherwise, <code>false</code>.
    */

   public boolean isNone() {
      return this == NONE;
   }

   /**
    * Predicate to determine if the enumeration member is {@link #OPTIONAL}.
    * 
    * @return <code>true</code> when the enumeration member is {@link #OPTIONAL}; otherwise, <code>false</code>.
    */

   public boolean isOptional() {
      return this == OPTIONAL;
   }

   /**
    * Predicate to determine if the enumeration member is {@link #REQUIRED}.
    * 
    * @return <code>true</code> when the enumeration member is {@link #REQUIRED}; otherwise, <code>false</code>.
    */

   public boolean isRequired() {
      return this == REQUIRED;
   }

   /**
    * Predicate to determine if the enumeration member is {@link #OPTIONAL} or {@link #REQUIRED}.
    * 
    * @return <code>true</code> when the enumeration member is {@link #OPTIONAL} or {@link #REQUIRED}; otherwise,
    * <code>false</code>.
    */

   public boolean isOptionalOrIsRequired() {
      return (this == OPTIONAL) || (this == REQUIRED);
   }

}
