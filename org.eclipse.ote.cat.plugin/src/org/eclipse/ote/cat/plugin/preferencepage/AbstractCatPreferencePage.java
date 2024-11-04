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

import java.util.Arrays;
import java.util.Objects;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ote.cat.plugin.CatPlugin;
import org.eclipse.ote.cat.plugin.exception.CatErrorCode;
import org.eclipse.ote.cat.plugin.exception.CatPluginException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * A base class for CAT Plug-in preference pages.
 * 
 * @author Loren K. Ashley
 */

public class AbstractCatPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

   /**
    * Saves the preference page descriptor.
    */

   protected final PreferencePage preferencePage;

   /**
    * Set <code>true</code> when the preference page has successfully initialized. When <code>false</code> the method
    * {@link #createFieldEditors} will not attempt the creation of the page's field editors.
    */

   protected boolean pageInitialized;

   protected CatPreferences preferenceValues;

   /**
    * Creates a new empty {@link FieldEditorPreferencePage} with {@link #GRID} layout.
    * 
    * @param preferencePage the preferences {@link PreferencePage} being created.
    */

   AbstractCatPreferencePage(PreferencePage preferencePage) {
      super(GRID);
      assert Objects.nonNull(preferencePage) : "Parameter \"preferencePage\" cannot be null.";
      this.preferencePage = preferencePage;
      this.pageInitialized = false;
   }

   /**
    * {@inheritDoc}
    * <p>
    * Obtains and sets the base class with the {@link IPreferenceStore} implementation from the {@link CatPlugin}
    * instance.
    */

   @Override
   public void init(IWorkbench workbench) {

      try {
         IPreferenceStore preferenceStore = CatPlugin.getInstancePreferenceStore();
         this.setPreferenceStore(preferenceStore);
         this.preferenceValues = Preference.getPreferenceValues(this.preferencePage);
         this.pageInitialized = true;
      } catch (Exception e) {
         //@formatter:off
         CatPluginException initException =
            new CatPluginException
                   (
                      CatErrorCode.PreferencePageError,
                        "Failed to initialize CAT Plugin preference page." + "\n"
                      + "   Page: " + this.preferencePage.getPageTitle()   + "\n",
                      e
                   );
         //@formatter:on
         initException.log();
      }
   }

   /**
    * Creates the {@link FieldEditor} instances for the preference page using the factories provided by the members of
    * the enumeration {@link Preference}.
    * <p>
    * {@inheritDoc}
    */

   @Override
   protected void createFieldEditors() {

      if (!this.pageInitialized) {
         return;
      }

      try {
         Composite parentComposite = this.getFieldEditorParent();

         //@formatter:off
         Arrays
            .stream( Preference.values() )
            .filter( ( preference ) -> preference.isOnPage( this.preferencePage ) )
            .map( ( preference ) -> preference.createFieldEditor( parentComposite ) )
            .forEach( this::addField );
         //@formatter:on

      } catch (Exception e) {
         //@formatter:off
         CatPluginException createFieldEditorsException =
            new CatPluginException
                   (
                      CatErrorCode.PreferencePageError,
                        "Failed to create field editors for CAT Plugin preference page." + "\n"
                      + "   Page: " + this.preferencePage.getPageTitle()                 + "\n",
                      e
                   );
         //@formatter:on
         createFieldEditorsException.log();
         this.pageInitialized = false;
      }
   }

}
