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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.ote.rest.client.Progress;

public class GetOteServerFile extends BaseClientCallable<Progress> {

   private final URI uri;
   private final String filePath;
   @SuppressWarnings("unused")
   private final Progress progress;
   private final JaxRsApi factory;
   private final File destination;

   public GetOteServerFile(URI uri, File destination, String filePath, Progress progress, JaxRsApi factory) {
      super(progress);
      this.uri = uri;
      this.filePath = filePath;
      this.progress = progress;
      this.factory = factory;
      this.destination = destination;
   }

   @Override
   public void doWork() throws Exception {
      URI targetUri = UriBuilder.fromUri(uri).path("ote").path("file").queryParam("path", filePath).build();

      Response response = factory.newTargetUrl(targetUri.toString()).request(MediaType.APPLICATION_XML).get();
      if (response.getStatus() == Status.OK.getStatusCode()) {
         InputStream is = (InputStream) response.getEntity();
         FileOutputStream fos = new FileOutputStream(destination);
         try {
            Lib.inputStreamToOutputStream(is, fos);
         } finally {
            Lib.close(fos);
         }
      } else {
         throw new Exception(response.toString());
      }
   }

}
