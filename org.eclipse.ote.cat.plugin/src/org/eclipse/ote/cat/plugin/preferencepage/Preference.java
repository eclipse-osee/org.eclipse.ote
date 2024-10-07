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

package org.eclipse.ote.cat.plugin.preferencepage;

import java.util.Objects;
import java.util.Optional;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ote.cat.plugin.CatPlugin;
import org.eclipse.ote.cat.plugin.CatPluginException;
import org.eclipse.ote.cat.plugin.fieldeditors.DirectoryAutoStore;
import org.eclipse.ote.cat.plugin.fieldeditors.PleConfigurationLoader;
import org.eclipse.ote.cat.plugin.fieldeditors.Project;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Enumeration of the preferences managed by the CAT Plugin for the CAT annotation processor. The enumeration members
 * are used to provide:
 * <ul>
 * <li>the preference page that will contain the preference editor for that preference,
 * <li>
 * <li>the preference store name for the preference,
 * <li>
 * <li>the field editor label on the preference page for the preference, and</li>
 * <li>a factory method for creating the preference page {@link FieldEditor} for the preference.</li>
 * </ul>
 * 
 * @see
 * <ul>
 * <li>{@link Preferences.Preference#CAT_JAR CAT_JAR}</li>
 * <li>{@link Preferences.Preference#JTS_PROJECTS JTS_PROJECTS}</li>
 * <li>{@link Preferences.Preference#PLE_CONFIGURATION PLE_CONFIGURATION}</li>
 * <li>{@link Preferences.Preference#PLE_CONFIGURATION_CACHE_FOLDER PLE_CONFIGURATION_CACHE_FOLDER}</li>
 * <li>{@link Preferences.Preference#PLE_CONFIGURATION_LOADER PLE_CONFIGURATION_LOADER_PREFERENCE}</li>
 * <li>{@link Preferences.Preference#SOURCE_LOCATION_METHOD SOURCE_LOCATION_METHOD}</li>
 * </ul>
 * @implNote The preference {@link FieldEditor}s will appear on the preference pages in the ordinal order of the
 * enumeration members.
 */

public enum Preference {

   //@formatter:off
   
   /**
    * This preference is used to specify the location of the CAT Jar file.
    * <dl>
    * <dt>{@link Page}:</dt>
    * <dd>{@link Page#CAT CAT}</dt>
    * </dl>
    */

   CAT_JAR
      (
         PreferencePage.CAT_SETTINGS,
         "CAT_JAR_PATH",
         "CAT Jar File:"
      ) {

      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new FileFieldEditor(this.getPreferenceStoreName(),this.getFieldEditorLabel(), parentComposite);
      }
   },

   /**
    * This preference is used to select the method by which the CAT will find source code files.
    * <dl>
    * <dt>{@link Page}:</dt>
    * <dd>{@link Page#CAT CAT}</dt>
    * </dl>
    */

   SOURCE_LOCATION_METHOD
      (
         PreferencePage.CAT_SETTINGS,
         "SOURCE_LOCATION_METHOD",
         "Source Location Method:"
      ) {
      
      private final String[][] sourceLocationMethods =
         {
            { "Eclipse IDE", "A" },
            { "Javac",       "B" },
            { "Maven",       "C" }
         };

      /**
       * {@inheritDoc}
       */
      
      @Override
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return 
            new ComboFieldEditor(this.getPreferenceStoreName(), this.getFieldEditorLabel(), sourceLocationMethods, parentComposite);
      }
   },
         
   /**
    * This preferences is used to select the Eclipse Java Projects that will be configured to use the CAT.
    * <dl>
    * <dt>{@link Page}:</dt>
    * <dd>{@link Page#CAT CAT}</dt>
    * </dl>
    */

   JTS_PROJECTS
      (
         PreferencePage.CAT_SETTINGS,
         "JTS_PROJECTS",
         "Java Test Script Projects:"
      ) {
      
      /**
       * {@inheritDoc}
       */
      
      @Override
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new Project(this.getPreferenceStoreName(), this.getFieldEditorLabel(), parentComposite);
      }
   },

   /**
    * This preference is used to select the PLE Configuration to be used with the CAT/BAT.
    * <dl>
    * <dt>{@link Page}:</dt>
    * <dd>{@link Page#CAT CAT}</dt>
    * </dl>
    */

   PLE_CONFIGURATION
      (
         PreferencePage.CAT_SETTINGS,
         "PLE_CONFIGURATION",
         "PLE Configuration:"
      ) {
   
      private static final String buttonLabel = "Select";
      
      /**
       * {@inheritDoc}
       */
      
      @Override
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         final DirectoryFieldEditor directoryFieldEditor =
            new DirectoryFieldEditor(this.getPreferenceStoreName(), this.getFieldEditorLabel(), parentComposite);
         directoryFieldEditor.setChangeButtonText(buttonLabel);
         return directoryFieldEditor;
         
      }
   },
      
   /**
    * This preference is used to select the folder used to save PLE Configurations.
    * <dl>
    * <dt>{@link Page}:</dt>
    * <dd>{@link Page#PLE_CONFIGURATION_CACHE PLE_CONFIGURATION_CACHE}</dt>
    * </dl>
    */

   PLE_CONFIGURATION_CACHE_FOLDER
      (
         PreferencePage.PLE_CONFIGURATION_CACHE,
         "PLE_CONFIGURATION_CACHE_FOLDER",
         "Folder:"
      ) {
      
      /**
       * {@inheritDoc}
       */
      
      @Override
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new DirectoryAutoStore(this.getPreferenceStoreName(),this.getFieldEditorLabel(), parentComposite);
      }
   }, 

   /**
    * This preference is used save the OPLE server PLE Configurations are down loaded from and provides a dialog for
    * down loading PLE Configurations.
    * <dl>
    * <dt>{@link Page}:</dt>
    * <dd>{@link Page#PLE_CONFIGURATION_CACHE PLE_CONFIGURATION_CACHE}</dt>
    * </dl>
    */

   PLE_CONFIGURATION_LOADER
      (
         PreferencePage.PLE_CONFIGURATION_CACHE, 
         "OPLE_SERVER",
         "OPLE Server:"
      ) {
      
      /**
       * {@inheritDoc}
       */
      
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new PleConfigurationLoader(this.getPreferenceStoreName(), this.getFieldEditorLabel(), parentComposite );
      }
   };
   //@formatter:on

   /**
    * Creates the {@link FieldEditor} implementation for the CAT Plugin preference represented by the enumeration
    * member.
    * 
    * @param parentComposite the {@link Composite} of the preference page the {@link FieldEditor} will be attached to.
    * @return a {@link FieldEditor} implementation.
    */

   abstract FieldEditor createFieldEditorInternal(Composite parentComposite);

   /**
    * Saves the key (name) used to access the preference value in the {@link IPrefernceStore}.
    */

   private final String preferenceStoreName;

   /**
    * Saves the preference page that the preference's field editor will appear on.
    */

   private final PreferencePage page;

   /**
    * Saves the label used for the preference's field editor.
    */

   private final String fieldEditorLabel;

   /**
    * Predicate to determine if the preference's field editor appears on the specified <code>page</code>.
    * 
    * @param page the {@link PreferencePage} to be tested.
    * @return <code>true</code> when the preference appears on the specified <code>page</code>; otherwise,
    * <code>null</code>.
    */

   public boolean isOnPage(PreferencePage page) {
      return this.page == page;
   }

   /**
    * Private enumeration member constructor used to save the represented preference's parameters.
    * 
    * @param page the {@link PreferencePage} the preference's field editor is to be presented on.
    * @param preferenceStoreName the key (name) used to access the preference's value in the Cat Plugin's preference
    * store.
    * @param fieldEditorLabel the label used for the preference's field editor upon the preference page.
    */

   private Preference(PreferencePage page, String preferenceStoreName, String fieldEditorLabel) {
      //@formatter:off
      assert 
           Objects.nonNull(page)
         : "Preference::new, parameter \"page\" cannot be null.";
           
      assert 
           Objects.nonNull(preferenceStoreName)
         : "Preference::new, parameter \"preferenceStoreName\" cannot be null.";
           
      assert 
           Objects.nonNull(fieldEditorLabel)
         : "Preference::new, parameter \"fieldEditorLabel\" cannot be null.";
      //@formatter:on

      this.page = page;
      this.preferenceStoreName = preferenceStoreName;
      this.fieldEditorLabel = fieldEditorLabel;
   }

   /**
    * Factory method to create the {@link FieldEditor} implementation for the preference.
    * 
    * @param parentComposite the {@link Composite} the created {@link FieldEditor} is attached to.
    * @return when the {@link FieldEditor} is successfully created an {@link Optional} with the {@link FieldEditor};
    * otherwise, an empty {@link Optional}.
    */

   public FieldEditor createFieldEditor(Composite parentComposite) {

      try {
         final FieldEditor fieldEditor = this.createFieldEditorInternal(parentComposite);
         return fieldEditor;
      } catch (Exception e) {
         //@formatter:off
         CatPluginException createFieldEditorException =
            new CatPluginException
                   (
                      IStatus.ERROR,
                      "CAT Plugin Error",
                      0,
                        "Failed to create field editor for preference." + "\n"
                      + "   Preference: " + this                        + "\n",
                      e
                   );
         //@formatter:on
         throw createFieldEditorException;
      }
   }

   /**
    * Get the name used by the {@link IPreferenceStore} to access and set the preference value.
    * 
    * @return the preference store name.
    */

   public String getPreferenceStoreName() {
      return this.preferenceStoreName;
   }

   /**
    * Gets the label to be used for the preference's {@link FieldEditor}.
    * 
    * @return the field editor label.
    */

   public String getFieldEditorLabel() {
      return this.fieldEditorLabel;
   }

   /**
    * Gets the value of the preference as a {@link String}.
    * 
    * @return the preference value.
    * @throws CatPluginException when unable to get the preference value from the plug-in preference store.
    */

   public String get() {
   //@formatter:off
      try {

         return 
            CatPlugin
               .getInstancePreferenceStore()
               .getString( this.getPreferenceStoreName() );
      
      } catch( Exception e) {
         
         throw
            new CatPluginException
                   (
                      StatusManager.BLOCK | StatusManager.LOG,
                      "CAT Plugin Preference Error",
                      IStatus.ERROR,
                        "Unable to get preference."  + "\n"
                      + "Preference: " + this.name() + "\n",
                      e
                   );
      }
      //@formatter:on
   }

   /**
    * Sets the {@link String} value of the preference.
    * 
    * @param value the value to be set.
    * @throws CatPluginException when unable to set the preference value in the preference store.
    */

   public void set(String value) {
   //@formatter:off
      try {
         
         CatPlugin
            .getInstancePreferenceStore()
            .setValue(this.getPreferenceStoreName(),value);
         
      } catch( Exception e ) {
         
         throw
            new CatPluginException
                   (
                      StatusManager.BLOCK | StatusManager.LOG,
                      "CAT Plugin Preference Error",
                      IStatus.ERROR,
                        "Unable to get preference."   + "\n"
                      + "Preference: " + this.name()  + "\n"
                      + "Value:      " + value        + "\n",
                      e
                   );
      }
      //@formatter:on
   };

}
