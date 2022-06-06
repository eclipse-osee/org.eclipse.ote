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
package org.eclipse.ote.simple.test.environment.rest;

import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.ote.rest.OteRestConfigurationProvider;
import org.eclipse.ote.simple.test.environment.SimpleOteApi;

/**
 * This API is an example of grouping multiple REST endpoint accessor methods together
 * 
 * @author Michael P. Masterson
 */
public class SimpleRestApiGroup {

   
   private final Simple1Endpoint endpoint1;

   /**
    * @param jaxRsApi
    * @param restConfig
    */
   public SimpleRestApiGroup(JaxRsApi jaxRsApi, OteRestConfigurationProvider restConfig) {
      this.endpoint1 = new Simple1Endpoint(jaxRsApi, restConfig);
   }

   public SimpleRestApiGroup(SimpleOteApi api) {
      this(api.zzz_getJaxRsApi(), api.zzz_getRestConfig());
   }

   /**
    * @return The specific Simple1Endpoint
    */
   public Simple1Endpoint endpoint1() {
      return this.endpoint1;
   }
   
}
