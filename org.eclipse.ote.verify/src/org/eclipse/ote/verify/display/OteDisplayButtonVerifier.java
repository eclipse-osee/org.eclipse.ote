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

import org.eclipse.osee.ote.core.environment.OteInternalApi;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.Operation;
import org.eclipse.ote.verify.OteVerifierAttribute;

/**
 * @author Leonel Pena
 * @param <T>
 */
public class OteDisplayButtonVerifier<T extends OteDisplayButtonVerifier<T>> extends OteDisplayObjectVerifier<T> {

   private final OteVerifierAttribute color;
   private final OteVerifierAttribute firstLineLabel;
   private final OteVerifierAttribute secondLineLabel;

   public OteDisplayButtonVerifier(OteInternalApi api) {
      super(api);

      this.firstLineLabel = new OteVerifierAttribute("First Line Label", OteVerifierAttribute.REQUIRED);
      this.secondLineLabel = new OteVerifierAttribute("Second Line Label", OteVerifierAttribute.OPTIONAL);

      this.color = new OteVerifierAttribute("Color", OteVerifierAttribute.OPTIONAL);
   }

   /**
    * @return the color
    */
   public OteVerifierAttribute getColor() {
      return color;
   }

   /**
    * @param color the color to set
    */
   public void setColor(Object color) {
      this.color.setValue(color);
   }

   /**
    * @return the label on the first line
    */
   public OteVerifierAttribute getFirstLineLabel() {
      return firstLineLabel;
   }

   /**
    * @return the label on the second line
    */
   public OteVerifierAttribute getSecondLineLabel() {
      return secondLineLabel;
   }

   /**
    * @param firstLineLabel the label to set on the first line
    * @param secondLineLabel the label to set on the second line
    */
   public void setLabel(String firstLineLabel, String secondLineLabel) {
      if (firstLineLabel.equals(null)) {
         this.firstLineLabel.setValue("");
      } else {
         this.firstLineLabel.setValue(firstLineLabel);
      }

      if (secondLineLabel.equals(null)) {
         this.secondLineLabel.setValue("");
      } else {
         this.secondLineLabel.setValue(secondLineLabel);
      }
   }

   @Override
   public CheckGroup verify(T actual) {
      CheckGroup cg = new CheckGroup(Operation.AND, "Display Button Check");

      CheckGroup superChecks = super.verify(actual);
      cg.addAll(superChecks.getTestPoints());

      this.addToCheckGroup(this.getFirstLineLabel(), actual.getFirstLineLabel(), cg);
      this.addToCheckGroup(this.getSecondLineLabel(), actual.getSecondLineLabel(), cg);
      this.addToCheckGroup(this.getColor(), actual.getColor(), cg);

      return cg;
   }

}
