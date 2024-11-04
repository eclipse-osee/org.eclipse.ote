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

package org.eclipse.ote.cat.plugin.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * A class of static methods for accessing the Extension Registry.
 * 
 * @author Loren K. Ashley
 */

public class Extensions {

   /**
    * Gets the attribute value for the attribute specified by <code>attributeName</code> from the
    * <code>configurationElement</code>.
    * 
    * @param configurationElement the configuration element to obtain the specified attribute value from.
    * @param attributeName the name of the attribute to get a value for.
    * @return the named attribute's value from the <code>configurationElement</code>.
    * @throws OseeCoreException when:
    * <ul>
    * <li>The <code>configurationElement</code> does not have an attribute named <code>attributeName</code>.</li>
    * <li>An error occurred accessing the <code>configurationElement</code>'s attributes.</li>
    * </ul>
    */

   public static String getAttribute(IConfigurationElement configurationElement, String attributeName) {
      String option = null;
      Exception optionCause = null;
      try {
         option = configurationElement.getAttribute(attributeName);
      } catch (Exception e) {
         optionCause = e;
      }
      if (Objects.isNull(option)) {
         //@formatter:off
         OseeCoreException cannotDetermineCommandLineOptionNameForDefaults =
            new OseeCoreException
                   (
                        "Failed to get Attribute value from then Configuration Element." + "\n"
                      + "   Configuration Element: " + configurationElement.getName()    + "\n"
                      + "   Attribute Name:        " + attributeName                     + "\n",
                      optionCause // <- might be null
                   );
         //@formatter:on
         throw cannotDetermineCommandLineOptionNameForDefaults;
      }
      return option;
   }

   /**
    * Gets all Configuration Elements from the Extension with the name specified by
    * <code>configurationElementName</code>.
    * 
    * @param extension the {@link IExtension} to search for {@link IConfigurationElement}s.
    * @param configurationElementName the name of the Configuration Elements to get.
    * @param expectedCount when greater than or equal to 0, the expected number of Configuration Elements.
    * @return a {@link List} containing the {@link IConfigurationElement}'s of the {@link IExtension} with the name
    * <code>configurationElementName</code>.
    * @throws OseeCoreException when:
    * <ul>
    * <li>When a error occurs obtaining Configuration Elements from the Extension.</li>
    * <li>When <code>expectedCount</code> is greater than or equal to 0 and the number of Configuration Elements found
    * with the name <code>configurationElementName</code> does not equal the <code>expectedCount</code>.</li>
    * </ul>
    */

   public static List<IConfigurationElement> getConfigurationElements(IExtension extension, String configurationElementName, int expectedCount) {

      IConfigurationElement[] configurationElements = null;
      Exception configurationElementsCause = null;

      try {
         configurationElements = extension.getConfigurationElements();
      } catch (Exception e) {
         configurationElementsCause = e;
      }

      if (Objects.isNull(configurationElements)) {
         //@formatter:off
         OseeCoreException osgiConfigurationElementsNotFound =
            new OseeCoreException
                   (
                        "Configuration Elements of the Extension were not found."                         + "\n"
                      + "   Extension:                  " + extension.getExtensionPointUniqueIdentifier() + "\n"
                      + "   Configuration Element Name: " + configurationElementName                      + "\n"
                      + "   Expected Count:             " + expectedCount                                 + "\n",
                      configurationElementsCause
                   );
         //@formatter:on
         throw osgiConfigurationElementsNotFound;
      }

      List<IConfigurationElement> configurationElementsList = new LinkedList<>();
      Exception configurationElementCause = null;

      try {
         for (int i = 0; i < configurationElements.length; i++) {
            if (Objects.nonNull(configurationElements[i]) && configurationElementName.equals(
               configurationElements[i].getName())) {
               configurationElementsList.add(configurationElements[i]);
               break;
            }
         }
      } catch (Exception e) {
         configurationElementCause = e;
      }

      if ((expectedCount >= 0) && (configurationElementsList.size() != expectedCount)) {
         //@formatter:off
         OseeCoreException cannotFindInitializerConfigurationElement =
            new OseeCoreException
                   (
                        "An unexpected number of Configuration Elements were found for the extension."    + "\n"
                      + "   Actual Count:               " + configurationElements.length                  + "\n"
                      + "   Extension:                  " + extension.getExtensionPointUniqueIdentifier() + "\n"
                      + "   Configuration Element Name: " + configurationElementName                      + "\n"
                      + "   Expected Count:             " + expectedCount                                 + "\n",
                      configurationElementCause // <- might be null
                   );
         //@formatter:on
         throw cannotFindInitializerConfigurationElement;
      }

      return configurationElementsList;
   }

   /**
    * Finds all the extensions provided by a bundle for an extension point.
    * 
    * @param extensionPointIdentifier the identifier of the extension point to get the extensions for.
    * @param bundleSymbolicName the identifier of the bundle to get the provided extensions for.
    * @param expectedCount when greater than or equal to 0, the expected number of extensions to be found.
    * @return a {@link List} of the found extensions. An empty list is returned when no extensions are found.
    * @throws OseeCoreException when:
    * <ul>
    * <li>The extension registry is not available or an error occurs when obtaining it.</li>
    * <li>The extension point cannot be found or an error occurs searching for the extension point.</li>
    * <li>An error occurs obtaining the extensions for the extension point.</li>
    * <li><code>expectedCount</code> is greater than or equal to 0, and <code>expectedCount</code> s from the
    * <code>bundleSymbolicName</code> bundle were not found.</li>
    * </ul>
    */

   public static List<IExtension> getExtensions(String extensionPointIdentifier, String bundleSymbolicName, int expectedCount) {

      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

      if (Objects.isNull(extensionRegistry)) {
         //@formatter:off
         OseeCoreException osgiExtensionRegistryNotAvailable =
            new OseeCoreException
                   (
                        "The Extension Registry is not available. Unable to find extensions for the extenion point." + "\n"
                      + "   Extension Point:      " + extensionPointIdentifier                                       + "\n"
                      + "   Bundle Symbolic Name: " + bundleSymbolicName                                             + "\n"
                      + "   Expected Count:       " + expectedCount                                                  + "\n"
                   );
         //@formatter:on
         throw osgiExtensionRegistryNotAvailable;
      }

      IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint(extensionPointIdentifier);

      if (Objects.isNull(extensionPoint)) {
         //@formatter:off
         OseeCoreException osgiExtensionPointNotFound =
            new OseeCoreException
                   (
                        "The Extension Point was not found."                    + "\n" 
                      + "   Extension Point:      " + extensionPointIdentifier  + "\n"
                      + "   Bundle Symbolic Name: " + bundleSymbolicName        + "\n"
                      + "   Expected Count:       " + expectedCount             + "\n"
                   );
         //@formatter:on
         throw osgiExtensionPointNotFound;
      }

      IExtension[] extensions = null;
      Exception extensionCause = null;

      extensionPoint.getExtensions();

      try {
         extensions = extensionPoint.getExtensions();
      } catch (Exception e) {
         extensionCause = e;
      }

      if (Objects.isNull(extensions)) {
         //@formatter:off
         OseeCoreException osgiExtensionNotFound =
            new OseeCoreException
                   (
                        "An Extension was not found for the Extension Point."   + "\n" 
                      + "   Extension Point:      " + extensionPointIdentifier  + "\n"
                      + "   Bundle Symbolic Name: " + bundleSymbolicName        + "\n"
                      + "   Expected Count:       " + expectedCount             + "\n",
                      extensionCause // <- might be null
                   );
         //@formatter:on
         throw osgiExtensionNotFound;
      }

      List<IExtension> extensionsWithNamespace = new LinkedList<>();

      for (int i = 0; i < extensions.length; i++) {
         if (extensions[i].getNamespaceIdentifier().equals(bundleSymbolicName)) {
            extensionsWithNamespace.add(extensions[i]);
         }
      }

      if ((expectedCount >= 0) && (extensionsWithNamespace.size() != expectedCount)) {
         //@formatter:off
         OseeCoreException unexpectedNumberOfExtensionsException =
            new OseeCoreException
                   (
                        "An unexpected number of extensions are provided by the bundle." + "\n"
                      + "   Actual Count:         " + extensionsWithNamespace.size()     + "\n"
                      + "   Extension Point:      " + extensionPointIdentifier           + "\n"
                      + "   Bundle Symbolic Name: " + bundleSymbolicName                 + "\n"
                      + "   Expected Count:       " + expectedCount                      + "\n"
                   );
         //@formatter:on
         throw unexpectedNumberOfExtensionsException;
      }

      return extensionsWithNamespace;
   }

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private Extensions() {
   }

}
