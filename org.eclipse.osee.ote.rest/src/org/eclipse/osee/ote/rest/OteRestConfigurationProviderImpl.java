/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ote.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;


/**
 * @author Michael P. Masterson
 */
public abstract class OteRestConfigurationProviderImpl implements OteRestConfigurationProvider {

   private static final String CONFIG_PROPERTY = "ote.rest.config";
   private final Map<String, URI> map = new HashMap<>();
   private static final String configurationFile = System.getProperty(CONFIG_PROPERTY);

   @Override
   public URI getBaseUri(String id) {
      if (map.isEmpty()) {
         lateLoadConfigFile();
      }
      return map.get(id);
   }


   public void putUri(String id, String path) {
      this.map.put(id, URI.create(path));
   }

   private void lateLoadConfigFile() {
      InputStream stream;
      if (Strings.isValid(configurationFile)) {
         try {
            stream = new FileInputStream(configurationFile);
         } catch (FileNotFoundException ex) {
            throw new OseeCoreException(ex, "Error reading command line REST config file %s = %s", CONFIG_PROPERTY, configurationFile);
         }
      } else {
         stream = loadStream();
      }

      parseAndAddConfigStream(stream);
   }


   protected abstract InputStream loadStream();

   private void parseAndAddConfigStream(InputStream stream) {

      Properties prop = new Properties();
      try {
         prop.load(stream);
         prop.forEach((k, v) -> map.put(k.toString(), URI.create(v.toString())));
      } catch (IOException ex) {
         throw new OseeCoreException(ex, "Error reading file at %s", configurationFile);
      }
   }

}
