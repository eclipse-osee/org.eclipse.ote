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

import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.Operation;
import org.eclipse.ote.verify.OteVerifierAttribute;
import org.eclipse.ote.verify.OteVerifierObjectAttribute;
import org.eclipse.ote.verify.OteVerifierStringAttribute;

/**
 * @author Leonel Pena
 * @param <T>
 */
public class OteDisplayButtonVerifier<T extends OteDisplayButtonVerifier<T>> extends OteDisplayObjectVerifier<T> {

   private final OteVerifierObjectAttribute color;
   private final OteVerifierStringAttribute firstLineLabel;
   private final OteVerifierStringAttribute secondLineLabel;

   public OteDisplayButtonVerifier() {
      this.firstLineLabel = new OteVerifierStringAttribute("First Line Label", OteVerifierAttribute.REQUIRED);
      this.secondLineLabel = new OteVerifierStringAttribute("Second Line Label", OteVerifierAttribute.OPTIONAL);

      this.color = new OteVerifierObjectAttribute("Color", OteVerifierAttribute.OPTIONAL);
   }

   /**
    * @return the color
    */
   public OteVerifierObjectAttribute getColor() {
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
   public OteVerifierStringAttribute getFirstLineLabel() {
      return firstLineLabel;
   }

   /**
    * @return the label on the second line
    */
   public OteVerifierStringAttribute getSecondLineLabel() {
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
