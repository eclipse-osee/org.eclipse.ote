package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.URI;
import java.util.concurrent.Callable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerAvailableNodes;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

public class GetAvailableServers implements Callable<OTEMasterServerAvailableNodes> {

   private final JaxRsApi webClientProvider;
   private final URI uri;

   public GetAvailableServers(JaxRsApi webClientProvider, URI uri) {
      this.webClientProvider = webClientProvider;
      this.uri = uri;
   }

   @Override
   public OTEMasterServerAvailableNodes call() throws Exception {
      URI targetUri = UriBuilder.fromUri(uri).path(OTEMasterServerImpl.CONTEXT_NAME).path(
         OTEMasterServerImpl.CONTEXT_SERVERS).build();

      OTEMasterServerAvailableNodes result = new OTEMasterServerAvailableNodes();
      try {
         OTEServer[] servers =
            webClientProvider.newTargetUrl(targetUri.toString()).request(MediaType.APPLICATION_XML).get(
               OTEServer[].class);
         result.setServers(servers);
         result.setSuccess(true);
      } catch (Throwable th) {
         result.setSuccess(false);
         result.setThrowable(th);
      }
      return result;
   }

}
