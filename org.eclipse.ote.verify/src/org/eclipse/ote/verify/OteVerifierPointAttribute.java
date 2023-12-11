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

import org.eclipse.osee.framework.jdk.core.type.DoublePoint;

/**
 * @author Michael P. Masterson
 */
public class OteVerifierPointAttribute extends OteVerifierAttribute<DoublePoint> {

   private static final DoublePoint SENTINEL_DP = new DoublePoint(Double.MIN_VALUE, Double.MIN_VALUE);

   public OteVerifierPointAttribute(String name, boolean isRequired) {
      super(name, isRequired);
   }

   @Override
   public DoublePoint sentinelValue() {
      return SENTINEL_DP;
   }

}
