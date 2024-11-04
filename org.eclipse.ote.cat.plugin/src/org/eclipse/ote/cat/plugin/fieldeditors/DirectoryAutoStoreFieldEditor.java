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

import java.io.File;
import java.util.Objects;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * An extension of the {@link DirectoryFieldEditor} that immediately saves the selected directory in the preference
 * store upon change to make the value available to other field editors.
 * 
 * @author Loren K. Ashley
 */

public class DirectoryAutoStoreFieldEditor extends DirectoryFieldEditor {

   /**
    * Error message template for the message displayed at the top of the preference page when a directory is not
    * selected.
    */

   private static final String directoryNotSelectedMessage = "%s A directory must be selected.";

   /**
    * Error message template for the message displayed at the top of the preference page when the selected file is not a
    * regular file.
    */

   private static final String notADirectoryMessage = "%s Selection must be an existing directory.";

   /**
    * Creates a directory field editor that immediately saves the selected directory in the preference store.
    * 
    * @param preferenceStoreName the name used to access the preference value in the preference store.
    * @param fieldEditorLabel the label for the field editor.
    * @param parentComposite the {@link Composite} to attach this field editor to.
    */

   public DirectoryAutoStoreFieldEditor(String preferenceStoreName, String fieldEditorLabel, Composite parentComposite) {
      super(preferenceStoreName, fieldEditorLabel, parentComposite);
      this.setValidateStrategy(StringFieldEditor.VALIDATE_ON_FOCUS_LOST);
      this.setPropertyChangeListener(this::propertyChanged);
   }

   /**
    * Saves the selected directory in the preference store.
    * <p>
    * {@inheritDoc}
    */

   protected void propertyChanged(PropertyChangeEvent event) {
      this.doStore();
   }

   /**
    * {@inheritDoc}
    * <p>
    * Verifies:
    * <ul>
    * <li>a selection has been made, and</li>
    * <li>the selection references an existing directory.</li>
    * </ul>
    */

   @Override
   protected boolean checkState() {

      String text = this.getTextControl().getText();
      String path = Objects.nonNull(text) ? text : null;

      if (Objects.isNull(path) || path.isEmpty()) {
         String message = String.format(DirectoryAutoStoreFieldEditor.directoryNotSelectedMessage, this.getLabelText());
         this.showErrorMessage(message);
         return false;
      }

      File file = new File(path);

      if (!file.isDirectory()) {
         String message = String.format(DirectoryAutoStoreFieldEditor.notADirectoryMessage, this.getLabelText());
         this.showErrorMessage(message);
         return false;
      }

      this.clearErrorMessage();
      return true;

   }
}
