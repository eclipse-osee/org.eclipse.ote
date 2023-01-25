/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.rest.internal;

import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.ConfigurationItem;
import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTEConfigurationItem;

public class TranslateUtil {

   public static Configuration translateToOtherConfig(OTEConfiguration restConfig) {
      if(restConfig == null){
         return null;
      }
      Configuration config = new Configuration();
      for(OTEConfigurationItem item:restConfig.getItems()){
         config.addItem(TranslateUtil.translateToOtherConfig(item));
      }
      return config;
   }
   
   public static ConfigurationItem translateToOtherConfig(OTEConfigurationItem restConfigItem) {
      return new ConfigurationItem(restConfigItem.getLocationUrl(), restConfigItem.getBundleVersion(), restConfigItem.getBundleName(), restConfigItem.getMd5Digest(), restConfigItem.isOsgiBundle());
   }
   
   public static OTEConfiguration translateConfig(Configuration config){
      if(config == null){
         return null;
      }
      OTEConfiguration restConfig = new OTEConfiguration();
      for(ConfigurationItem item:config.getItems()){
         OTEConfigurationItem newitem = new OTEConfigurationItem();
         newitem.setBundleName(item.getSymbolicName());
         newitem.setBundleVersion(item.getVersion());
         newitem.setLocationUrl(item.getLocationUrl());
         newitem.setMd5Digest(item.getMd5Digest());
         restConfig.addItem(newitem);
      }
      return restConfig;
   }
   
}
