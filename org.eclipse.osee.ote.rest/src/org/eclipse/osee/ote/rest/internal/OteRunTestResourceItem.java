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

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.framework.command.RunTests;
import org.eclipse.osee.ote.rest.model.OTEJobStatus;

public class OteRunTestResourceItem {

   @SuppressWarnings("unused")
   private UriInfo uriInfo;
   @SuppressWarnings("unused")
   private Request request;
   private String id;
   
   public OteRunTestResourceItem(UriInfo uriInfo, Request request, String id) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.id = id;
   }
   
   @DELETE
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public OTEJobStatus abortAll() throws IOException, InterruptedException, ExecutionException {
      OteRunTestCommands commands = ServiceUtility.getService(OteRunTestCommands.class);
      OTEJobStatus status = new OTEJobStatus();
      status.setSuccess(false);
      if(commands != null){
         RunTests cmd = commands.getCommand(id);
         if(cmd != null){
            cmd.cancel();
            status.setSuccess(true);
         }
      }
      return status;
   }
   
   @PUT
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public OTEJobStatus abortSingle() {
      OteRunTestCommands commands = ServiceUtility.getService(OteRunTestCommands.class);
      OTEJobStatus status = new OTEJobStatus();
      status.setSuccess(false);
      if(commands != null){
         RunTests cmd = commands.getCommand(id);
         if(cmd != null){
            cmd.cancelSingle();
            status.setSuccess(true);
         }
      }
      return status;
   }
   
}
