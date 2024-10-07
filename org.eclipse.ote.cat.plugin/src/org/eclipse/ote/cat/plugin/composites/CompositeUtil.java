/*********************************************************************
 * Copyright (c) 2024 Boeing
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.cat.plugin.composites;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Utility methods for classes in the package {@link org.eclispe.ote.cat.plugin.composites}.
 * 
 * @author Loren K. Ashley
 */

class CompositeUtil {

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private CompositeUtil() {
   }

   /**
    * An extension of the abstract {@link FieldEditor} class to expose the protected method
    * {@link FieldEditor#convertHorizontalDLUsToPixels convertHorizontalDLUsToPixels}.
    */

   private static final class BogusFieldEditor extends FieldEditor {

      /**
       * {@inheritDoc}
       */

      @Override
      public int convertHorizontalDLUsToPixels(Control control, int dlus) {
         return super.convertHorizontalDLUsToPixels(control, dlus);
      }

      @Override
      protected void adjustForNumColumns(int numColumns) {
      }

      @Override
      protected void doFillIntoGrid(Composite parent, int numColumns) {
      }

      @Override
      protected void doLoad() {
      }

      @Override
      protected void doLoadDefault() {
      }

      @Override
      protected void doStore() {
      }

      @Override
      public int getNumberOfControls() {
         return 0;
      }
   };

   /**
    * A static instance of the {@link BogusFieldEditor} class used to access the exposed protected method
    * <code>convertHorizontalDLUsToPixels</code> of the class {@link FieldEditor}.
    */

   private static final BogusFieldEditor bogusFieldEditor = new BogusFieldEditor();

   /**
    * Returns the number of pixels corresponding to the given number of horizontal dialog units.
    *
    * @param control the control being sized
    * @param dlus the number of horizontal dialog units
    * @return the number of pixels
    */

   static int convertHorizontalDLUsToPixels(Control control, int dlus) {
      return CompositeUtil.bogusFieldEditor.convertHorizontalDLUsToPixels(control, dlus);
   }

}
