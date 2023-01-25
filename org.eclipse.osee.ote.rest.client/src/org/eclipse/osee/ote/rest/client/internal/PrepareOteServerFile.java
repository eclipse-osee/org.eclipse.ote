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
package org.eclipse.osee.ote.rest.client.internal;

import java.net.URI;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.ote.rest.client.OTECacheItem;
import org.eclipse.osee.ote.rest.client.Progress;
import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTEConfigurationIdentity;
import org.eclipse.osee.ote.rest.model.OTEConfigurationItem;
import org.eclipse.osee.ote.rest.model.OTEJobStatus;

public class PrepareOteServerFile extends BaseClientCallable<Progress> {

   private static final long POLLING_RATE = 1000;
   private final URI uri;
   private final List<OTECacheItem> jars;
   private final Progress progress;
   private final JaxRsApi factory;
   private OTEJobStatus status;
   private final String baseJarURL;

   public PrepareOteServerFile(URI uri, String baseJarURL, List<OTECacheItem> jars, Progress progress, JaxRsApi factory) {
      super(progress);
      this.uri = uri;
      this.jars = jars;
      this.progress = progress;
      this.factory = factory;
      this.baseJarURL = baseJarURL;
   }

   @Override
   public void doWork() throws Exception {
      status = sendBundleConfiguration();
      if (!status.isJobComplete()) {
         waitForJobComplete();
      }

      if (!status.isSuccess()) {
         throw new Exception("Failed to update the environment cache: " + status.getErrorLog());
      }
   }

   private void waitForJobComplete() throws Exception {

      URI jobUri = status.getUpdatedJobStatus().toURI();
      WebTarget service = factory.newTargetUrl(jobUri.toString());

      while (!status.isJobComplete()) {
         Thread.sleep(POLLING_RATE);
         status = service.request(MediaType.APPLICATION_JSON).get(OTEJobStatus.class);
         progress.setUnitsOfWork(status.getTotalUnitsOfWork());
         progress.setUnitsWorked(status.getUnitsWorked());
      }
   }

   private OTEJobStatus sendBundleConfiguration() throws Exception {
      OTEConfiguration configuration = new OTEConfiguration();
      OTEConfigurationIdentity identity = new OTEConfigurationIdentity();
      identity.setName("test");
      configuration.setIdentity(identity);
      for (OTECacheItem bundleInfo : jars) {
         OTEConfigurationItem item = new OTEConfigurationItem();
         item.setBundleName(bundleInfo.getFile().getName());
         item.setBundleVersion("N/A");
         item.setLocationUrl(baseJarURL + bundleInfo.getMd5());
         item.setMd5Digest(bundleInfo.getMd5());
         configuration.addItem(item);
      }
      URI targetUri = UriBuilder.fromUri(uri).path("ote").path("cache").build();
      return factory.newTargetUrl(targetUri.toString()).request(MediaType.APPLICATION_JSON).post(
         Entity.json(configuration), OTEJobStatus.class);
   }

}
