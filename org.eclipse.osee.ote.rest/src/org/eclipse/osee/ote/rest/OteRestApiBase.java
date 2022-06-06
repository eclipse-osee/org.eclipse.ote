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

import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.ote.api.OteApiBase;

/**
 * @author Michael P. Masterson
 */
public class OteRestApiBase extends OteApiBase {

   private JaxRsApi jaxRsApi;
   private OteRestConfigurationProvider restConfig;

   /**
    * NOT INTENDED FOR TEST SCRIPT USE.<br>
    * 
    * @param jaxRsApi
    */
   public void zzz_bindJaxRsApi(JaxRsApi jaxRsApi) {
      this.jaxRsApi = jaxRsApi;
   }

   /**
    * NOT INTENDED FOR TEST SCRIPT USE.<br>
    * 
    * @param restConfig
    */
   public void zzz_bindRestConfig(OteRestConfigurationProvider restConfig) {
      this.restConfig = restConfig;
   }

   /**
    * NOT INTENDED FOR TEST SCRIPT USE.<br>
    * 
    * @return JaxRsApi service instance
    */
   public JaxRsApi zzz_getJaxRsApi() {
      return this.jaxRsApi;
   }

   /**
    * NOT INTENDED FOR TEST SCRIPT USE.<br>
    * 
    * @return OteRestConfigurationProvider service instance
    */
   public OteRestConfigurationProvider zzz_getRestConfig() {
      return restConfig;
   }
}
