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
package org.eclipse.ote.simple.test.environment;

import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.ote.api.local.LocalProcessApi;
import org.eclipse.osee.ote.rest.OteRestApiBase;
import org.eclipse.osee.ote.rest.OteRestConfigurationProvider;
import org.eclipse.ote.simple.test.environment.rest.SimpleRestApiGroup;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * @author Nydia Delgado
 */
@Component(service = {SimpleOteApi.class}, immediate = true)
public class SimpleOteApi extends OteRestApiBase {

   private SimpleRestApiGroup endpoints;
   private OteRestConfigurationProvider restConfig;
   private LocalProcessApi localProcessApi;

   @Activate
   public void zzz_activate() {
      this.endpoints = new SimpleRestApiGroup(zzz_getJaxRsApi(), restConfig);
      this.localProcessApi = new LocalProcessApi();
   }

   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void zzz_bindTestEnv(SimpleTestEnvironment testEnv) {
      super.zzz_bindTestEnv(testEnv);
      testEnv.setOteApi(this);
   }

   @Override
   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void zzz_bindJaxRsApi(JaxRsApi jaxRsApi) {
      super.zzz_bindJaxRsApi(jaxRsApi);
   }

   @Override
   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void zzz_bindRestConfig(OteRestConfigurationProvider restConfig) {
      this.restConfig = restConfig;
   }

   public SimpleRestApiGroup rest() {
      return this.endpoints;
   }

   public LocalProcessApi localProcess() {
      return this.localProcessApi;
   }

   @Override
   public OteRestConfigurationProvider zzz_getRestConfig() {
      return this.restConfig;
   }
}
