package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerResult;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

public class RemoveServer implements Callable<OTEMasterServerResult> {

   private final JaxRsClient webClientProvider;
   private final OTEServer server;
   private final URI uri;

   public RemoveServer(JaxRsClient webClientProvider, URI uri, OTEServer server) {
      this.webClientProvider = webClientProvider;
      this.uri = uri;
      this.server = server;
   }

   @Override
   public OTEMasterServerResult call() throws Exception {
      OTEMasterServerResult result = new OTEMasterServerResult();
      try {
         URI mainTarget  =
               UriBuilder.fromUri(uri).path(OTEMasterServerImpl.CONTEXT_NAME).path(OTEMasterServerImpl.CONTEXT_SERVERS).build();
         URI targetUri  =
               UriBuilder.fromUri(uri).path(OTEMasterServerImpl.CONTEXT_NAME).path(OTEMasterServerImpl.CONTEXT_SERVERS).path(server.getUUID().toString()).build();
         if(HttpUtil.canConnect(mainTarget)){
            webClientProvider.target(targetUri).request().delete();
         } else {
            result.setSuccess(false);   
         }
      } catch (Throwable th) {
         result.setSuccess(false);
         result.setThrowable(th);
      }
      return result;
   }
}
