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
package org.eclipse.ote.verify.display;

import org.eclipse.osee.framework.jdk.core.type.DoublePoint;
import org.eclipse.osee.ote.core.environment.OteInternalApi;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.Operation;
import org.eclipse.ote.verify.OteVerifier;
import org.eclipse.ote.verify.OteVerifierAttribute;

/**
 * @author Michael P. Masterson
 * @param <T>
 */
public class OteDisplayObjectVerifier<T extends OteDisplayObjectVerifier<T>> extends OteVerifier<T> {
   private final OteVerifierAttribute position;

   /**
    * @param api used for logging
    */
   public OteDisplayObjectVerifier(OteInternalApi api) {
      super(api);
      this.position = new OteVerifierAttribute("Position", OteVerifierAttribute.OPTIONAL);
   }

   public void setPosition(DoublePoint position) {
      this.position.setValue(position);
   }

   public OteVerifierAttribute getPosition() {
      return position;
   }

   @Override
   public CheckGroup verify(T actual) {
      CheckGroup cg = new CheckGroup(Operation.AND, "Display Object Check");
      addToCheckGroup(this.getPosition(), actual.getPosition(), cg);
      return cg;
   }

}
