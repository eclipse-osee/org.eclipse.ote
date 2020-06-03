/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.connection.jini;

import java.io.File;
import java.net.URI;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import net.jini.core.lookup.ServiceItem;

/**
 * @author Ken J. Aguilar
 */
public class JiniClientSideConnector extends JiniConnector {
   public static final String TYPE = "jini.client-end";
   private final ServiceItem serviceItem;
   private final IJiniConnectorLink link;

   JiniClientSideConnector(ServiceItem serviceItem) {
      super();
      this.serviceItem = serviceItem;
      link = (IJiniConnectorLink) getProperties().getProperty(LINK_PROPERTY);
   }

   @Override
   public Object getService() {
      return serviceItem.service;
   }

   @Override
   public String getConnectorType() {
      return TYPE;
   }

   @Override
   public URI upload(File file) throws Exception {
      return null;
   }

   @Override
   public boolean ping() {
      try {
         return link.ping();
      } catch (RemoteException e) {
         return false;
      }
   }

   @Override
   public void init(Object service) throws UnknownHostException, ExportException {

   }

   @Override
   public String getUniqueServerId() {
      return serviceItem.serviceID.toString();
   }

}
