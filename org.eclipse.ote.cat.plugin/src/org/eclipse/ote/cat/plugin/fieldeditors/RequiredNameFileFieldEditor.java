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
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * An extension of {@link FileFieldEditor} that requires the selected file to have a case insensitive specific name.
 * 
 * @author Loren K. Ashley
 */

public class RequiredNameFileFieldEditor extends FileFieldEditor {

   /**
    * Saves the required file name converted to lower case.
    */

   private final String requiredFileName;

   /**
    * Error message template for the message displayed at top of the preference page when the selected file does not
    * have the required name.
    */

   private static final String notRequiredFileNameMessage = "%s Selected file must have the name \"%s\"";

   /**
    * Error message template for the message displayed at the top of the preference page when a file with the required
    * name is not selected.
    */

   private static final String requiredFileNotSelectedMessage = "%s A file with the name \"%s\" must be selected.";

   /**
    * Error message template for the message displayed at the top of the preference page when the selected file is not a
    * regular file.
    */

   private static final String notAFileMessage = "%s Selection must be an existing regular file.";

   /**
    * Creates a extension of {@link FileFieldEditor} that requires the selected file to have a specific case insensitive
    * name.
    *
    * @param name the name of the preference this field editor works on
    * @param labelText the label text of the field editor
    * @param parent the parent of the field editor's control
    * @param requiredFileName the selected file must have this name in any case.
    */

   public RequiredNameFileFieldEditor(String name, String labelText, Composite parent, String requiredFileName) {
      super(name, labelText, true, StringButtonFieldEditor.VALIDATE_ON_FOCUS_LOST, parent);
      this.requiredFileName = requiredFileName.toLowerCase();
      String[] requiredNameArray = new String[] {this.requiredFileName};
      this.setFileExtensions(requiredNameArray);
   }

   /**
    * {@inheritDoc}
    * <p>
    * Verifies:
    * <ul>
    * <li>a selection has been made,</li>
    * <li>the selection has the required file name, and</li>
    * <li>the selection references an existing regular file.</li>
    * </ul>
    */

   @Override
   protected boolean checkState() {

      String text = this.getTextControl().getText();
      String path = Objects.nonNull(text) ? text.trim() : null;

      if (Objects.isNull(path) || path.isEmpty()) {
         String message = String.format(RequiredNameFileFieldEditor.requiredFileNotSelectedMessage, this.getLabelText(),
            this.requiredFileName);
         this.showErrorMessage(message);
         return false;
      }

      if (!path.toLowerCase().endsWith(this.requiredFileName)) {
         String message = String.format(RequiredNameFileFieldEditor.notRequiredFileNameMessage, this.getLabelText(),
            this.requiredFileName);
         this.showErrorMessage(message);
         return false;
      }

      File file = new File(path);

      if (!file.isFile()) {
         String message = String.format(RequiredNameFileFieldEditor.notAFileMessage, this.getLabelText());
         this.showErrorMessage(message);
         return false;
      }

      this.clearErrorMessage();
      return true;

   }

}
