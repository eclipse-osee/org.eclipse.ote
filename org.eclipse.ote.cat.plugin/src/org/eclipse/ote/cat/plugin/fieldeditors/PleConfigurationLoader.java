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

package org.eclipse.ote.cat.plugin.fieldeditors;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * A {@link StringButtonFieldEditor} with a read only text box for selecting a PLE Configuration from the PLE
 * Configuration Cache folder.
 * 
 * @author Loren K. Ashley
 */

public class PleConfigurationLoader extends StringButtonFieldEditor {

   /**
    * Label for the {@link StringButtonFieldEditor} button.
    */

   private static String buttonText = "Load";

   /**
    * Creates a new {@link PleConfigurationLoader} {@link FieldEditor}.
    * 
    * @param preferenceStoreName the name used to access the preference value in the preference store.
    * @param fieldEditorLabel the label for the field editor.
    * @param parentComposite the {@link Composite} to attach this field editor to.
    */

   public PleConfigurationLoader(String preferenceStoreName, String fieldEditorLabel, Composite parentComposite) {
      super(preferenceStoreName, fieldEditorLabel, parentComposite);
      final Text text = this.getTextControl();
      text.setEditable(false);
      this.setChangeButtonText(PleConfigurationLoader.buttonText);
   }

   /**
    * Presents the user with a dialog to select a PLE Configuration from the PLE Configuration Cache folder.
    * 
    * @return the display string for the {@link StringButtonFieldEditor} text box.
    */

   @Override
   protected String changePressed() {
      //TODO:
      return "ABC";
   }

}
