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

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * An extension of the {@link DirectoryFieldEditor} that immediately saves the selected directory in the preference
 * store upon change to make the value available to other field editors.
 * 
 * @author Loren K. Ashley
 */

public class DirectoryAutoStore extends DirectoryFieldEditor {

   /**
    * Creates a directory field editor that immediately saves the selected directory in the preference store.
    * 
    * @param preferenceStoreName the name used to access the preference value in the preference store.
    * @param fieldEditorLabel the label for the field editor.
    * @param parentComposite the {@link Composite} to attach this field editor to.
    */

   public DirectoryAutoStore(String preferenceStoreName, String fieldEditorLabel, Composite parentComposite) {
      super(preferenceStoreName, fieldEditorLabel, parentComposite);
      this.setValidateStrategy(StringFieldEditor.VALIDATE_ON_FOCUS_LOST);
   }

   /**
    * Saves the selected directory in the preference store.
    * <p>
    * {@inheritDoc}
    */

   protected void valueChanged() {
      this.doStore();
   }
}
