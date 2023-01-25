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
package org.eclipse.osee.ote.internal;

import java.util.concurrent.Callable;

import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.ConfigurationStatus;
import org.eclipse.osee.ote.OTEStatusCallback;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;

public class DownloadConfiguration implements Callable<ConfigurationStatus> {

   private final IRuntimeLibraryManager bundleLoader;
   private final Configuration configuration;
   private final OTEStatusCallback<ConfigurationStatus> callable;
   
   public DownloadConfiguration(IRuntimeLibraryManager bundleLoader2, Configuration configuration, OTEStatusCallback<ConfigurationStatus> callable) {
      this.bundleLoader = bundleLoader2;
      this.configuration = configuration;
      this.callable = callable;
   }

   @Override
   public ConfigurationStatus call() throws Exception {
      ConfigurationStatus status = new ConfigurationStatus(configuration, true, "");
      try{
         callable.setTotalUnitsOfWork(determineUnitsOfWork());
         if(!bundleLoader.acquireBundles(configuration, callable)){
            status.setFail("Failed to download all bundles.");
         }
      } finally {
         callable.complete(status);
      }
      return status;
   }

   private int determineUnitsOfWork() {
      return configuration.getItems().size();      
   }

}
