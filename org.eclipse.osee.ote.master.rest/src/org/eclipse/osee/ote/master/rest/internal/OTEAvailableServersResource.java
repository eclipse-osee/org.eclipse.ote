package org.eclipse.osee.ote.master.rest.internal;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

import org.eclipse.osee.ote.master.OTELookup;
import org.eclipse.osee.ote.master.OTELookupServerEntry;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

@Path("servers")
public class OTEAvailableServersResource {

   // Allows to insert contextual objects into the class, 
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public List<OTEServer> getOTEServers() throws MalformedURLException, IllegalArgumentException, UriBuilderException, InterruptedException, ExecutionException {
      OTELookup oteLookup = OTERestApplication.getOTELookup();
      List<OTELookupServerEntry> availableServers = oteLookup.getAvailableServers();
      List<OTEServer> servers = new ArrayList<>();
      for (OTELookupServerEntry entry : availableServers) {
         servers.add(Util.convert(entry));
      }
      return servers;
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public String getAsHtml() {
      OTELookup oteLookup = OTERestApplication.getOTELookup();
      List<OTELookupServerEntry> availableServers = oteLookup.getAvailableServers();

      StringBuilder builder = new StringBuilder();
      builder.append("<html><body>");
      builder.append("<table border=\"1\">");

      builder.append("<tr>\n");
      builder.append("<th>");
      builder.append("Station");
      builder.append("</th>");
      builder.append("<th>");
      builder.append("Name");
      builder.append("</th>");
      builder.append("<th>");
      builder.append("ConnectedUsers");
      builder.append("</th>");
      builder.append("<th>");
      builder.append("StartTime");
      builder.append("</th>");
      builder.append("</tr>\n");

      for (OTELookupServerEntry entry : availableServers) {
         builder.append("<tr>\n");
         builder.append("<td>");
         builder.append(entry.getStation());
         builder.append("</td>");
         builder.append("<td>");
         builder.append(entry.getName());
         builder.append("</td>");
         builder.append("<td>");
         builder.append(entry.getConnectedUsers());
         builder.append("</td>");
         builder.append("<td>");
         builder.append(entry.getStartTime());
         builder.append("</td>");
         builder.append("</tr>\n");
      }
      builder.append("</table>");
      builder.append("<br/>");
      builder.append("</body></html>");
      return builder.toString();
   }

   @POST
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public void updateServer(OTEServer server) throws Exception {
      OTELookup oteLookup = OTERestApplication.getOTELookup();
      oteLookup.addServer(Util.convert(server));
   }

   @DELETE
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public void removeServer(OTEServer server) throws Exception {
      OTELookup oteLookup = OTERestApplication.getOTELookup();
      oteLookup.removeServer(Util.convert(server));
   }
   
   @DELETE
   @Path("{uuid}")
   public void removeServer(@PathParam("uuid") String uuid) throws Exception {
      OTELookup oteLookup = OTERestApplication.getOTELookup();
      oteLookup.removeServer(UUID.fromString(uuid));
   }

}
