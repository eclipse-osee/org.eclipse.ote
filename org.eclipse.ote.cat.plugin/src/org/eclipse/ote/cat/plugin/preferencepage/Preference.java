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

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
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
import org.eclipse.ui.part.Page;
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
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#catJarPreferenceStoreName}</dd>
    * </dl>
    */

   CAT_JAR
      (
         PreferencePage.CAT_SETTINGS,
         Constants.catJarPreferenceStoreName,
         "CAT Jar File:",
         DefaultPreferences::getCatJar
         
      ) {

      /**
       * {@inheritDoc}
       */
      
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new FileFieldEditor(this.getPreferenceStoreName(),this.getFieldEditorLabel(), parentComposite);
      }
      
      /**
       * Validates the default <code>value</code> is the path of a file that can be read.
       * <p>
       * {@inheritDoc}
       * @return <code>true</code> when the <code>value</code> is the path of a readable file; otherwise, <code>false</code>.
       */
      
      public boolean validateDefaultValue(String value) {
         
         Exception cause = null;
         
         try {
         
            if( Paths.get(value).toFile().canRead() ) {
               return true;
            }
         } catch( Exception e ) {
            cause = e;
         }
         
         CatPluginException catJarDefaultValueException =
            new CatPluginException
                   (
                      StatusManager.BLOCK | StatusManager.LOG,
                      "CAT Plugin Default Preferences",
                      IStatus.WARNING,
                        "The CAT Jar file at the default location does not exist or cannot be read."                      + "\n"
                      + "   Default Cat Jar File: " + value                                                               + "\n"
                                                                                                                          + "\n"
                      + "The default value for the preference \"" + this.getPreferenceStoreName() + "\" will be ignored." + "\n",
                      cause
                   );

         catJarDefaultValueException.log();
         
         return false;
      }
   },

   /**
    * This preference is used to select the method by which the CAT will find source code files.
    * <dl>
    * <dt>{@link Page}:</dt>
    * <dd>{@link Page#CAT CAT}</dt>
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#sourceLocationMethodPreferenceStoreName}</dd>
    * </dl>
    */

   SOURCE_LOCATION_METHOD
      (
         PreferencePage.CAT_SETTINGS,
         Constants.sourceLocationMethodPreferenceStoreName,
         "Source Location Method:",
         DefaultPreferences::getSourceLocationMethod
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
      
      /**
       * Validates the default Source Location Method is a supported Source Location Method.
       * <p>
       * {@inheritDoc}
       * @return <code>true</code> when the value is a valid Source Location Method; otherwise, <code>false</code>.
       */
      
      public boolean validateDefaultValue(String value) {
         for( int i = 0; i < sourceLocationMethods.length; i++ ) {
            if( sourceLocationMethods[i][0].equals( value ) ) {
               return true;
            }
         }
         
         StringBuilder validValues = new StringBuilder( 1024 );
         for( int i = 0; i < sourceLocationMethods.length; i++ ) {
            validValues.append( "      " ).append( sourceLocationMethods[i][0] ).append( "\n" );
         }
         
         CatPluginException sourceLocationMethodDefaultValueException =
            new CatPluginException
                   (
                      StatusManager.BLOCK | StatusManager.LOG,
                      "CAT Plugin Default Preferences",
                      IStatus.WARNING,
                        "The Source Location Method specified in the default preferences file is not a valid Source Location Method." + "\n"
                      + "   Specified Default Source Location Method: " + value                                                       + "\n"
                      + "   Valid Source Location Methods: "                                                                          + "\n"
                      + validValues.toString(),
                      null
                   );
         
         sourceLocationMethodDefaultValueException.log();
         
         return false;
      }
   },
         
   /**
    * This preferences is used to select the Eclipse Java Projects that will be configured to use the CAT.
    * <dl>
    * <dt>{@link Page}:</dt>
    * <dd>{@link Page#CAT CAT}</dt>
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#jtsProjectsPreferenceStoreName}</dd>
    * </dl>
    */

   JTS_PROJECTS
      (
         PreferencePage.CAT_SETTINGS,
         Constants.jtsProjectsPreferenceStoreName,
         "Java Test Script Projects:",
         DefaultPreferences::getJtsProjectsCommaList
      ) {
      
      /**
       * {@inheritDoc}
       */
      
      @Override
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new Project(this.getPreferenceStoreName(), this.getFieldEditorLabel(), parentComposite);
      }
      
      /**
       * {@inheritDoc}
       */
      
      public boolean validateDefaultValue(String value) {
         //TODO: This will be completed when JTS project management is implemented.
         return true;
      }
   },

   /**
    * This preference is used to select the PLE Configuration to be used with the CAT/BAT.
    * <dl>
    * <dt>{@link Page}:</dt>
    * <dd>{@link Page#CAT CAT}</dt>
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#pleConfigurationPreferenceStoreName}</dd>
    * </dl>
    */

   PLE_CONFIGURATION
      (
         PreferencePage.CAT_SETTINGS,
         Constants.pleConfigurationPreferenceStoreName,
         "PLE Configuration:",
         DefaultPreferences::getPleConfiguration
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
      
      /**
       * This <code>value</code> is always accepted as it will not cause an invalid preference page error.
       * <p> 
       * {@inheritDoc}
       * @return <code>true</code>
       */
      
      public boolean validateDefaultValue(String value) {
         return true;
      }
   },
      
   /**
    * This preference is used to select the folder used to save PLE Configurations.
    * <dl>
    * <dt>{@link Page}:</dt>
    * <dd>{@link Page#PLE_CONFIGURATION_CACHE PLE_CONFIGURATION_CACHE}</dt>
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#pleConfigurationCacheFolderPreferenceStoreName}</dd>
    * </dl>
    */

   PLE_CONFIGURATION_CACHE_FOLDER
      (
         PreferencePage.PLE_CONFIGURATION_CACHE,
         Constants.pleConfigurationCacheFolderPreferenceStoreName,
         "Folder:",
         DefaultPreferences::getPleConfigurationCacheFolder
      ) {
      
      /**
       * {@inheritDoc}
       */
      
      @Override
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new DirectoryAutoStore(this.getPreferenceStoreName(),this.getFieldEditorLabel(), parentComposite);
      }
      
      /**
       * Validates the default <code>value</code> is the path of a directory that can be read.
       * <p>
       * {@inheritDoc}
       * @return <code>true</code> when the <code>value</code> is the path of a directory that can be read; otherwise, <code>false</code>.
       */
      
      public boolean validateDefaultValue(String value) {
         
         Exception cause = null;
         
         try {
         
            File file = Paths.get(value).toFile();
            
            if( !file.canRead() ) {
               cause = new RuntimeException();
            } else if( !file.isDirectory() ) {
               cause = new RuntimeException();
            }
            
         } catch( Exception e ) {
            cause = e;
         }
         
         if( Objects.isNull(cause) ) {
            return true;
         }
         
         CatPluginException catJarDefaultValueException =
            new CatPluginException
                   (
                      StatusManager.BLOCK | StatusManager.LOG,
                      "CAT Plugin Default Preferences",
                      IStatus.WARNING,
                        "The PLE Configuration Cache Folder at the default location does not exist or cannot be read."                      + "\n"
                      + "   Default PLE Configuration Cache Folder: " + value                                             + "\n"
                                                                                                                          + "\n"
                      + "The default value for the preference \"" + this.getPreferenceStoreName() + "\" will be ignored." + "\n",
                      cause
                   );

         catJarDefaultValueException.log();
         
         return false;
      }
   }, 

   /**
    * This preference is used save the OPLE server PLE Configurations are down loaded from and provides a dialog for
    * down loading PLE Configurations.
    * <dl>
    * <dt>{@link Page}:</dt>
    * <dd>{@link Page#PLE_CONFIGURATION_CACHE PLE_CONFIGURATION_CACHE}</dt>
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#pleConfigurationLoaderPreferenceStoreName}</dd>
    * </dl>
    */

   PLE_CONFIGURATION_LOADER
      (
         PreferencePage.PLE_CONFIGURATION_CACHE, 
         Constants.pleConfigurationLoaderPreferenceStoreName,
         "OPLE Server:",
         DefaultPreferences::getPleConfigurationLoader
      ) {
      
      /**
       * {@inheritDoc}
       */
      
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new PleConfigurationLoader(this.getPreferenceStoreName(), this.getFieldEditorLabel(), parentComposite );
      }
      
      /**
       * This <code>value</code> is always accepted as it will not cause an invalid preference page error.
       * <p> 
       * {@inheritDoc}
       * @return <code>true</code>
       */
      
      public boolean validateDefaultValue(String value) {
         return true;
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
    * Determines if the default value read from the default preferences file is OK to be set as the default value. The
    * user may be presented with the option to correct the situation that makes the default value invalid. However, the
    * user cannot change the default value.
    * 
    * @param value the default value for the preference to be tested.
    * @return <code>true</code> when it is OK to set the <code>value</code> as the default value in the preference
    * store; otherwise, <code>false</code>.
    */

   abstract boolean validateDefaultValue(String value);

   /**
    * Saves the key (name) used to access the preference value in the {@link IPrefernceStore}.
    */

   public final String preferenceStoreName;

   /**
    * Saves the preference page that the preference's field editor will appear on.
    */

   private final PreferencePage page;

   /**
    * Saves the label used for the preference's field editor.
    */

   private final String fieldEditorLabel;

   /**
    * Saves the {@link Function} used to extract the preference value from a {@link DefaultPreferences} POJO.
    */

   private final Function<DefaultPreferences, String> defaultPreferenceValueExtractor;

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
    * @param defaultPreferenceValueExtractor a {@link Function} used to extract the default value for the preference
    * from a {@link DefaultPreferences} POJO
    */

   private Preference(PreferencePage page, String preferenceStoreName, String fieldEditorLabel, Function<DefaultPreferences, String> defaultPreferenceValueExtractor) {
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
      this.defaultPreferenceValueExtractor = defaultPreferenceValueExtractor;
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
    * Extracts the default value for the preference from a {@link DefaultPreferences} POJO.
    * 
    * @param defaultPreference the {@link Preference} to extract a default value for.
    * @return an {@link Optional} containing the the default value for the preference when it is present in
    * <code>defaultPreference</code>; otherwise, an empty {@link Optional}.
    */

   public Optional<String> getDefault(DefaultPreferences defaultPreference) {
      String defaultValue = this.defaultPreferenceValueExtractor.apply(defaultPreference);
      if (Objects.nonNull(defaultValue) && this.validateDefaultValue(defaultValue)) {
         return Optional.of(defaultValue);
      }
      return Optional.empty();
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
    * Get the name used by the {@link IPreferenceStore} to access and set the preference value.
    * 
    * @return the preference store name.
    */

   public String getPreferenceStoreName() {
      return this.preferenceStoreName;
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
