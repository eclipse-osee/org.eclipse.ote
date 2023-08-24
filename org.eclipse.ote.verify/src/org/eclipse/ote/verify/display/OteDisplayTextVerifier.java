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

import java.awt.Point;
import org.eclipse.osee.ote.core.environment.OteInternalApi;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.Operation;
import org.eclipse.ote.verify.OteVerifierAttribute;

/**
 * @author Michael P. Masterson
 * @param <T>
 */
public class OteDisplayTextVerifier<T extends OteDisplayTextVerifier<T>> extends OteDisplayObjectVerifier<T> {

   private final OteVerifierAttribute label;
   private final OteVerifierAttribute location;
   private final OteVerifierAttribute color;

   public OteDisplayTextVerifier(OteInternalApi api) {
      super(api);
      this.label = new OteVerifierAttribute("Label", true);
      this.location = new OteVerifierAttribute("Location", true);
      this.color = new OteVerifierAttribute("Color", false);
   }

   /**
    * @return the label
    */
   public OteVerifierAttribute getLabel() {
      return label;
   }

   /**
    * @param label the label to set
    */
   public void setLabel(String label) {
      this.label.setValue(label);
   }

   /**
    * @return the location of the text
    */
   public OteVerifierAttribute getLocation() {
      return location;
   }

   /**
    * @param location the location of the text to set
    */
   public void setLocation(Point location) {
      this.location.setValue(location);
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

   @Override
   public CheckGroup verify(T actual) {
      CheckGroup cg = new CheckGroup(Operation.AND, "Display Text Check");

      CheckGroup superChecks = super.verify(actual);
      cg.addAll(superChecks.getTestPoints());

      this.addToCheckGroup(this.getLabel(), actual.getLabel(), cg);
      this.addToCheckGroup(this.getLocation(), actual.getLocation(), cg);
      this.addToCheckGroup(this.getColor(), actual.getColor(), cg);

      return cg;
   }

}
