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
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.ote.cat.plugin.CatPlugin;
import org.eclipse.ote.cat.plugin.Constants;
import org.eclipse.ote.cat.plugin.exception.CatErrorCode;
import org.eclipse.ote.cat.plugin.exception.CatPluginException;
import org.eclipse.ote.cat.plugin.fieldeditors.DirectoryAutoStoreFieldEditor;
import org.eclipse.ote.cat.plugin.fieldeditors.PleConfigurationLoaderFieldEditor;
import org.eclipse.ote.cat.plugin.fieldeditors.ProjectFieldEditor;
import org.eclipse.ote.cat.plugin.fieldeditors.RequiredNameFileFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * Enumeration of the preferences managed by the CAT Plugin for the CAT annotation processor. The enumeration members
 * are used to provide:
 * <ul>
 * <li>the preference page that will contain the preference editor for that preference,</li>
 * <li>the preference store name for the preference,</li>
 * <li>the field editor label on the preference page for the preference, and</li>
 * <li>a factory method for creating the preference page {@link FieldEditor} for the preference.</li>
 * </ul>
 * <h3>See Also:</h3>
 * <ul>
 * <li>{@link Preference#CAT_JAR CAT_JAR}</li>
 * <li>{@link Preference#JTS_PROJECTS JTS_PROJECTS}</li>
 * <li>{@link Preference#PLE_CONFIGURATION PLE_CONFIGURATION}</li>
 * <li>{@link Preference#PLE_CONFIGURATION_CACHE_FOLDER PLE_CONFIGURATION_CACHE_FOLDER}</li>
 * <li>{@link Preference#PLE_CONFIGURATION_LOADER PLE_CONFIGURATION_LOADER_PREFERENCE}</li>
 * <li>{@link Preference#SOURCE_LOCATION_METHOD SOURCE_LOCATION_METHOD}</li>
 * </ul>
 * 
 * @implNote The preference {@link FieldEditor}s will appear on the preference pages in the ordinal order of the
 * enumeration members.
 */

public enum Preference {
   //@formatter:off
   
   /**
    * This preference is used to specify the location of the CAT Jar file.
    * <dl>
    * <dt>{@link PreferencePage Page}:</dt>
    * <dd>{@link PreferencePage#CAT_SETTINGS CAT_SETTINGS}</dd>
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#catJarPreferenceStoreName}</dd>
    * </dl>
    */

   CAT_JAR
      (
         PreferencePage.CAT_SETTINGS,
         Constants.catJarPreferenceStoreName,
         "CAT Jar File:",
         CatPreferences::getCatJar,
         CatPreferences::setCatJar
      ) {

      /**
       * {@inheritDoc}
       */
      
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new RequiredNameFileFieldEditor(this.getPreferenceStoreName(),this.getFieldEditorLabel(), parentComposite, Constants.catJarDetectionName);
      }
      
      /**
       * Validates the <code>value</code> is the path of a file that can be read.
       * <p>
       * {@inheritDoc}
       * @param value the OS {@link String} path to the file to be tested.
       * @param isDefault indicates if the <code>value</code> is a default preference value.
       * @return <code>true</code> when the <code>value</code> is the path of a readable file; otherwise, <code>false</code>.
       */
      
      public boolean validateValue(String value,IsDefault isDefault) {
         
         Exception cause = null;
         
         try {
         
            if( Paths.get(value).toFile().canRead() ) {
               return true;
            }
         } catch( Exception e ) {
            cause = e;
         }

         final String formatMessage =
              "The CAT Jar file %sdoes not exist or cannot be read." + "\n"
            + "   %sCat Jar File: " + value                          + "\n"
                                                                     + "\n"
            + "%s";
         
         final String message =
            isDefault.isYes()
               ? String.format
                    (
                       formatMessage,
                       "at the default location",
                       "Default",
                       "The default value for the preference \"" + this.getPreferenceStoreName() + "\" will be ignored." + "\n"
                    )
               : String.format
                    (
                       formatMessage,
                       "", 
                       "",
                       "" 
                    );
         
         CatPluginException catJarDefaultValueException =
            new CatPluginException
                   (
                      CatErrorCode.CatJarFileError,
                      message,
                      cause
                   );

         catJarDefaultValueException.log();
         
         return false;
      }
   },

   /**
    * This preferences is used to select the Eclipse Java Projects that will be configured to use the CAT.
    * <dl>
    * <dt>{@link PreferencePage Page}:</dt>
    * <dd>{@link PreferencePage#CAT_SETTINGS CAT_SETTINGS}</dd>
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#jtsProjectsPreferenceStoreName}</dd>
    * </dl>
    */

   JTS_PROJECTS
      (
         PreferencePage.CAT_SETTINGS,
         Constants.jtsProjectsPreferenceStoreName,
         "Java Test Script Projects:",
         CatPreferences::getJtsProjectsCommaList,
         CatPreferences::setJtsProjectsCommaList
      ) {
      
      /**
       * {@inheritDoc}
       */
      
      @Override
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new ProjectFieldEditor(this.getPreferenceStoreName(), this.getFieldEditorLabel(), parentComposite);
      }
      
      /**
       * {@inheritDoc}
       */
      
      public boolean validateValue(String value, IsDefault isDefault) {
         //TODO: This will be completed when JTS project management is implemented.
         return true;
      }
   },
         
   /**
    * This preference is used to select the PLE Configuration to be used with the CAT/BAT.
    * <dl>
    * <dt>{@link PreferencePage Page}:</dt>
    * <dd>{@link PreferencePage#CAT_SETTINGS CAT_SETTINGS}</dd>
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#pleConfigurationPreferenceStoreName}</dd>
    * </dl>
    */

   PLE_CONFIGURATION
      (
         PreferencePage.CAT_SETTINGS,
         Constants.pleConfigurationPreferenceStoreName,
         "PLE Configuration:",
         CatPreferences::getPleConfiguration,
         CatPreferences::setPleConfiguration
      ) {
   
      private static final String buttonLabel = "Select";
      
      /**
       * {@inheritDoc}
       */
      
      @Override
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         final StringButtonFieldEditor stringButtonFieldEditor =
            new StringButtonFieldEditor(this.getPreferenceStoreName(), this.getFieldEditorLabel(), parentComposite) {
            protected String changePressed() {
               return "ABCDEFG";
            }
         };
         stringButtonFieldEditor.setChangeButtonText(buttonLabel);
         return stringButtonFieldEditor;
         
      }
      
      /**
       * This <code>value</code> is always accepted as it will not cause an invalid preference page error.
       * <p> 
       * {@inheritDoc}
       * @return <code>true</code>
       */
      
      public boolean validateValue(String value, IsDefault isDefault) {
         return true;
      }
   },

   /**
    * This preference is used to select the folder used to save PLE Configurations.
    * <dl>
    * <dt>{@link PreferencePage Page}:</dt>
    * <dd>{@link PreferencePage#CAT_SETTINGS CAT_SETTINGS}</dd>
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#pleConfigurationCacheFolderPreferenceStoreName}</dd>
    * </dl>
    */

   PLE_CONFIGURATION_CACHE_FOLDER
      (
         PreferencePage.CAT_SETTINGS,
         Constants.pleConfigurationCacheFolderPreferenceStoreName,
         "PLE Configuration Cache Folder:",
         CatPreferences::getPleConfigurationCacheFolder,
         CatPreferences::setPleConfigurationCacheFolder
      ) {
      
      /**
       * {@inheritDoc}
       */
      
      @Override
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new DirectoryAutoStoreFieldEditor(this.getPreferenceStoreName(),this.getFieldEditorLabel(), parentComposite);
      }
      
      /**
       * Validates the <code>value</code> is the path of a directory that can be read.
       * <p>
       * {@inheritDoc}
       * @param value the OS {@link String} path to the directory to be tested.
       * @param isDefault indicates if the <code>value</code> is a default preference value.
       * @return <code>true</code> when the <code>value</code> is the path of a directory that can be read; otherwise, <code>false</code>.
       */
      
      public boolean validateValue(String value, IsDefault isDefault) {
         
         Exception cause = null;
         String reason = null;
         
         try {
         
            File file = Paths.get(value).toFile();
            
            if( !file.canRead() ) {
               reason = "cannot be read";
            } else if( !file.isDirectory() ) {
               reason = "is not a directory";
            }
            
         } catch( Exception e ) {
            cause = e;
            reason = "cannot be evaluated";
         }
         
         if( Objects.isNull(cause) && Objects.isNull(reason)) {
            return true;
         }
         
         final String formatMessage =
              "The PLE Configuration Cache Folder %s%s."       + "\n"
            + "   %sPLE Configuration Cache Folder: " + value  + "\n"
                                                               + "\n"
            + "%s";
            
         final String message =
            isDefault.isYes()
               ? String.format
                    (
                       formatMessage, 
                       "at the default location ",
                       reason,
                       "Default",
                       "The default value for the preference \"" + this.getPreferenceStoreName() + "\" will be ignored." + "\n"                       
                    )
               : String.format
                    (
                       formatMessage, 
                       "",
                       reason,
                       "",
                       ""
                    );
            
         CatPluginException catJarDefaultValueException =
            new CatPluginException
                   (
                      CatErrorCode.PleConfigurationCacheFolderError,
                      message,
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
    * <dt>{@link PreferencePage Page}:</dt>
    * <dd>{@link PreferencePage#PLE_CONFIGURATION_LOADER PLE_CONFIGURATION_LOADER}</dd>
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#pleConfigurationLoaderPreferenceStoreName}</dd>
    * </dl>
    */

   PLE_CONFIGURATION_LOADER
      (
         PreferencePage.PLE_CONFIGURATION_LOADER, 
         Constants.pleConfigurationLoaderPreferenceStoreName,
         "OPLE Server:",
         CatPreferences::getPleConfigurationLoader,
         CatPreferences::setPleConfigurationLoader
      ) {
      
      /**
       * {@inheritDoc}
       */
      
      public FieldEditor createFieldEditorInternal(Composite parentComposite) {
         return
            new PleConfigurationLoaderFieldEditor(this.getPreferenceStoreName(), this.getFieldEditorLabel(), parentComposite );
      }
      
      /**
       * This <code>value</code> is always accepted as it will not cause an invalid preference page error.
       * <p> 
       * {@inheritDoc}
       * @return <code>true</code>
       */
      
      public boolean validateValue(String value, IsDefault isDefault) {
         return true;
      }
   },
      
   /**
    * This preference is used to select the method by which the CAT will find source code files.
    * <dl>
    * <dt>{@link PreferencePage Page}:</dt>
    * <dd>{@link PreferencePage#CAT_SETTINGS CAT_SETTINGS}</dd>
    * <dt>Preference Store Name:</dt>
    * <dd>{@value Constants#sourceLocationMethodPreferenceStoreName}</dd>
    * </dl>
    */

   SOURCE_LOCATION_METHOD
      (
         PreferencePage.CAT_SETTINGS,
         Constants.sourceLocationMethodPreferenceStoreName,
         "Source Location Method:",
         CatPreferences::getSourceLocationMethod,
         CatPreferences::setSourceLocationMethod
      ) {
      
      private final String[][] sourceLocationMethods =
         {
            { "Eclipse IDE", "Eclipse IDE" },
            { "Javac",       "Javac" },
            { "Maven",       "Maven" }
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
       * @param value the Source Location Method {@link String} to be tested.
       * @param isDefault indicates if the <code>value</code> is a default preference value.
       * @return <code>true</code> when the value is a valid Source Location Method; otherwise, <code>false</code>.
       */
      
      public boolean validateValue(String value, IsDefault isDefault) {
         for( int i = 0; i < sourceLocationMethods.length; i++ ) {
            if( sourceLocationMethods[i][0].equals( value ) ) {
               return true;
            }
         }
         
         StringBuilder validValues = new StringBuilder( 1024 );
         for( int i = 0; i < sourceLocationMethods.length; i++ ) {
            validValues.append( "      " ).append( sourceLocationMethods[i][0] ).append( "\n" );
         }
         
         final String formatMessage =
              "The Source Location Method %sis not a valid Source Location Method." + "\n"
            + "   %sSource Location Method: " + value                               + "\n"
            + "   Valid Source Location Methods: "                                  + "\n"
            + validValues.toString()
            + "%s";
         
         final String message =
            isDefault.isYes()
               ? String.format
                    (
                       formatMessage, 
                       "specified in the default preferences file ",
                       "Default",
                       "The default value for the preference \"" + this.getPreferenceStoreName() + "\" will be ignored." + "\n"
                    )
               : String.format
                    (
                       formatMessage, 
                       "",
                       "",
                       ""
                    );
            
         CatPluginException sourceLocationMethodDefaultValueException =
            new CatPluginException
                   (
                      CatErrorCode.SourceLocationMethodError,
                      message
                   );
         
         sourceLocationMethodDefaultValueException.log();
         
         return false;
      }
   };
   //@formatter:on

   /**
    * Indicator enumeration use to specify when a preference value is a default value.
    */

   enum IsDefault {

      /**
       * The preference value is not a default.
       */

      NO,

      /**
       * The preference value is a default value.
       */

      YES;

      /**
       * Predicate to determine if the {@link IsDefault} member is {@link IsDefault#YES YES}.
       * 
       * @return <code>true</code> when the member is {@link IsDefault#YES}; otherwise, <code>false</code>.
       */

      boolean isYes() {
         return this == YES;
      }
   }

   /**
    * Creates a {@link CatPreferences} and sets it with the preference value from the <code>preferencePage</code>.
    * 
    * @param preferencePage only set the preferences from this page.
    * @return a {@link CatPreferences} set with the value from the <code>preferencePage</code>.
    */

   public static CatPreferences getPreferenceValues(PreferencePage preferencePage) {
      CatPreferences catPreferences = new CatPreferences();
      for (Preference preference : Preference.values()) {
         if (preference.isOnPage(preferencePage)) {
            String value = preference.get();
            preference.preferencePojoValueSetter.accept(catPreferences, value);
         }
      }
      return catPreferences;
   }

   /**
    * Updates the preference store with the preference values from the <code>preferencesPojo</code> for the preferences
    * on the <code>preferencePage</code>.
    * 
    * @param preferencePage only the preferences from this page will be set in the preference store.
    * @param catPreferences preference values to be set.
    */

   public static void setPreferenceValues(PreferencePage preferencePage, CatPreferences catPreferences) {
      for (Preference preference : Preference.values()) {
         if (preference.isOnPage(preferencePage)) {
            String value = preference.preferencePojoValueGetter.apply(catPreferences);
            preference.set(value);
         }
      }
   }

   /**
    * Validates the preference values in the preference store for the <code>preferencePage</code>. If any of the
    * validations fail, the preference store is restored with the values from <code>originalPreferenceValues</code> for
    * the <code>preferencePage</code>.
    * 
    * @param preferencePage the preference values on this page are validated.
    * @param originalPreferenceValues restoration preference value for the page.
    * @return <code>true</code> when all values on the page are validated; otherwise, <code>false</code>.
    */

   public static boolean validateValues(PreferencePage preferencePage, CatPreferences originalPreferenceValues) {
      boolean status = true;
      for (Preference preference : Preference.values()) {
         if (preference.isOnPage(preferencePage)) {
            String value = preference.get();
            status &= preference.validateValue(value, IsDefault.NO);
         }
      }
      if (status == false) {
         Preference.setPreferenceValues(preferencePage, originalPreferenceValues);
      }
      return status;
   }

   /**
    * Saves the label used for the preference's field editor.
    */

   private final String fieldEditorLabel;

   /**
    * Saves the preference page that the preference's field editor will appear on.
    */

   private final PreferencePage page;

   /**
    * Saves the {@link Function} used to get a preference value from a {@link CatPreferences}.
    */

   private final Function<CatPreferences, String> preferencePojoValueGetter;

   /**
    * Saves the {@link BiConsumer} used to set a preference value in to a {@link PreferencePojo}.
    */

   private final BiConsumer<CatPreferences, String> preferencePojoValueSetter;

   /**
    * Saves the key (name) used to access the preference value in the {@link IPreferenceStore}.
    */

   public final String preferenceStoreName;

   /**
    * Private enumeration member constructor used to save the represented preference's parameters.
    * 
    * @param page the {@link PreferencePage} the preference's field editor is to be presented on.
    * @param preferenceStoreName the key (name) used to access the preference's value in the Cat Plugin's preference
    * store.
    * @param fieldEditorLabel the label used for the preference's field editor upon the preference page.
    * @param preferencePojoValueGetter a {@link Function} used to extract the default value for the preference from a
    * {@link CatPreferences} POJO
    */

   private Preference(PreferencePage page, String preferenceStoreName, String fieldEditorLabel, Function<CatPreferences, String> preferencePojoValueGetter, BiConsumer<CatPreferences, String> preferencePojoValueSetter) {
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
      this.preferencePojoValueGetter = preferencePojoValueGetter;
      this.preferencePojoValueSetter = preferencePojoValueSetter;

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
                      CatErrorCode.PreferencePageError,
                        "Failed to create field editor for preference." + "\n"
                      + "   Preference: " + this                        + "\n",
                      e
                   );
         //@formatter:on
         throw createFieldEditorException;
      }
   }

   /**
    * Creates the {@link FieldEditor} implementation for the CAT Plugin preference represented by the enumeration
    * member.
    * 
    * @param parentComposite the {@link Composite} of the preference page the {@link FieldEditor} will be attached to.
    * @return a {@link FieldEditor} implementation.
    */

   abstract FieldEditor createFieldEditorInternal(Composite parentComposite);

   /**
    * Gets the value of the preference as a {@link String}.
    * 
    * @return the preference value.
    * @throws CatPluginUserException when unable to get the preference value from the plug-in preference store.
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
                      CatErrorCode.InternalError,
                        "Unable to get preference from preference store." + "\n"
                      + "Preference: " + this.name()                      + "\n",
                      e
                   );
      }
      //@formatter:on
   }

   /**
    * Extracts the default value for the preference from a {@link CatPreferences} POJO.
    * 
    * @param defaultPreferencesPojo the {@link Preference} to extract a default value for.
    * @return an {@link Optional} containing the the default value for the preference when it is present in
    * <code>defaultPreference</code>; otherwise, an empty {@link Optional}.
    */

   public Optional<String> getDefault(CatPreferences defaultPreferencesPojo) {
      String defaultValue = this.preferencePojoValueGetter.apply(defaultPreferencesPojo);
      if (Objects.nonNull(defaultValue) && this.validateValue(defaultValue, IsDefault.YES)) {
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
    * Sets the {@link String} value of the preference.
    * 
    * @param value the value to be set.
    * @throws CatPluginUserException when unable to set the preference value in the preference store.
    */

   public void set(String value) {
   //@formatter:off
      try {
         
         CatPlugin
            .getInstancePreferenceStore()
            .putValue(this.getPreferenceStoreName(),value);
         
      } catch( Exception e ) {
         
         throw
            new CatPluginException
                   (
                      CatErrorCode.InternalError,
                        "Unable to get preference from preference store." + "\n"
                      + "Preference: " + this.name()                      + "\n"
                      + "Value:      " + value                            + "\n",
                      e
                   );
      }
      //@formatter:on
   }

   /**
    * Determines if the preference value is valid.
    * 
    * @param value the value for the preference to be tested.
    * @param isDefault when {@link IsDefault#YES YES} error messages will be formatted for a default value setting
    * instead of a user selected value.
    * @return <code>true</code> when the preference value OK; otherwise, <code>false</code>.
    */

   abstract boolean validateValue(String value, IsDefault isDefault);;

}
