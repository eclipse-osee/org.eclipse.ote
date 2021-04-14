/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.ote.osgi.configuration.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.ote.osgi.configuration.OTEConfigurationProvider;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * Loads property files via input streams and provides those properties as configurations to any named service.<br>
 * <br>
 * Each property listing must include an entry specifying the name of the service being configured using the key
 * {@value #DS_COMPONENT_NAME_KEY}
 * 
 * @author Michael P. Masterson
 */
public class OTEPropertyConfiguration {

   public static final String DS_COMPONENT_NAME_KEY = "ds.component.name";

   private ConfigurationAdmin admin;
   private boolean isStarted = false;
   private final List<OTEConfigurationProvider> providers = new ArrayList<OTEConfigurationProvider>();
   private ExecutorService exec;

   public synchronized void start() {
      isStarted = true;
      exec = Executors.newSingleThreadExecutor();
      for (OTEConfigurationProvider provider : providers) {
         submitConfiguration(provider);
      }
      providers.clear();
   }

   public synchronized void stop() {
      exec.shutdown();
      isStarted = false;
   }

   public synchronized void bindConfigurationAdmin(ConfigurationAdmin admin) {
      this.admin = admin;
   }

   public synchronized void unbindConfigurationAdmin(ConfigurationAdmin admin) {
      this.admin = null;
   }

   public synchronized void addOTEConfigurationProvider(OTEConfigurationProvider provider) {
      if (isStarted) {
         submitConfiguration(provider);
      } else {
         providers.add(provider);
      }
   }

   private void submitConfiguration(OTEConfigurationProvider provider) {
      exec.submit(new ProcessConfiguration(provider));
   }

   private synchronized void processConfiguration(OTEConfigurationProvider provider) {
      if (!isStarted) {
         return;
      }
      InputStream[] propertyFiles = provider.getPropertyFiles();
      if (propertyFiles != null) {
         for (InputStream propertyFile : propertyFiles) {
            try {
               Properties properties = new Properties();
               try {
                  properties.load(propertyFile);
                  String dsName = properties.getProperty(DS_COMPONENT_NAME_KEY);
                  if (dsName == null) {
                     OseeLog.log(getClass(), Level.SEVERE,
                                 "Unable to determin which component to configure due to missing: "
                                                           + DS_COMPONENT_NAME_KEY
                                                           + " in property file");
                  } else {
                     Configuration configuration = admin.getConfiguration(dsName, null);
                     Dictionary<String, Object> componentProperties = configuration.getProperties();
                     if (componentProperties == null) {
                        componentProperties = new Hashtable<String, Object>();
                     }

                     for (String key : properties.stringPropertyNames()) {
                        String value = System.getProperty(key);
                        if (value == null) {
                           value = properties.get(key).toString();
                        }
                        componentProperties.put(key, value);
                     }
                     configuration.update(componentProperties);
                  }
               } catch (IOException e) {
                  OseeLog.log(getClass(), Level.SEVERE,
                              "Failed to load Inputstream from " + provider.getClass().getName(),
                              e);
               }
            } catch (Exception e) {
               OseeLog.log(getClass(), Level.SEVERE,
                           "Failed to load properties file from " + provider.getClass().getName(),
                           e);
            }
            finally {
               try {
                  propertyFile.close();
               } catch (IOException e) {
                  OseeLog.log(getClass(), Level.SEVERE,
                              "Failed to close Inputstream from " + provider.getClass().getName(),
                              e);
               }
            }
         }
      }
   }

   private class ProcessConfiguration implements Runnable {
      private final OTEConfigurationProvider provider;

      public ProcessConfiguration(OTEConfigurationProvider provider) {
         this.provider = provider;
      }

      @Override
      public void run() {
         processConfiguration(provider);
      }
   }

}
