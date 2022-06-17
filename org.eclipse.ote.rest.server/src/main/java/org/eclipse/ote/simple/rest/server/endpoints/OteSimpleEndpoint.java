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
package org.eclipse.ote.simple.rest.server.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.ote.simple.rest.server.resources.OteSimpleJsonPojo;

/**
 * @author Nydia Delgado
 */
@Path("/ote")
public class OteSimpleEndpoint {

   @Path("/{typename}/{value}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public OteSimpleJsonPojo getData(@PathParam("typename") String name, @PathParam("value") String value) {

      OteSimpleJsonPojo obj = new OteSimpleJsonPojo();
      obj.setTypeName(name);
      obj.setValue(value);

      return obj;
   }

}
