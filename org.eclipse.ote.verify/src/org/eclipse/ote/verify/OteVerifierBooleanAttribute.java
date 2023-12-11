/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.ote.verify;

/**
 * @author Michael P. Masterson
 */
public class OteVerifierBooleanAttribute extends OteVerifierAttribute<Boolean> {

   private static final Boolean SENTINEL_BOOL = false;

   /**
    * @param name
    * @param isRequired
    */
   public OteVerifierBooleanAttribute(String name, boolean isRequired) {
      super(name, isRequired);
   }

   @Override
   public Boolean sentinelValue() {
      return SENTINEL_BOOL;
   }

}
