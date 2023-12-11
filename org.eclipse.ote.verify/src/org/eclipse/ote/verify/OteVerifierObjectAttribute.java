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

import org.eclipse.osee.framework.jdk.core.type.BaseId;

/**
 * @author Michael P. Masterson
 */
public class OteVerifierObjectAttribute extends OteVerifierAttribute<Object> {

   /**
    * @param name
    * @param isRequired
    */
   public OteVerifierObjectAttribute(String name, boolean isRequired) {
      super(name, isRequired);
   }

   @Override
   public Object sentinelValue() {
      return BaseId.SENTINEL;
   }

}
