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

package org.eclipse.ote.client.ui.core.widgets;

import java.io.Serializable;
import java.net.InetSocketAddress;

import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

/**
 * @author Andrew M. Finkbeiner
 */
public class EventMessageConnector extends RestLookupConnector {

   private final HostProxy host;
   
   public EventMessageConnector(OTEServer server, InetSocketAddress address) {
      super(server);
      host = new HostProxy(ServiceUtility.getService(OteUdpEndpoint.class), address, server);
      this.service = host;
   }
   
   @Override
   public Object getService() {
      return host;
   }

   @Override
   public boolean ping() {
      EnhancedProperties properties = getProperties();
      if(properties != null){
         return this.server.getUUID().equals(properties.getProperty("id", ""));
      }
      return false;
   }
   
   @Override
   public EnhancedProperties getProperties() {
         return host.getProperties();
   }
   
   @Override
   public Serializable getProperty(String property, Serializable defaultValue) {
      if("oteUdpEndpoint".equalsIgnoreCase(property)){
         return server.getOteRestServer();
      }
      return super.getProperty(property, defaultValue);
   }
   
   
}
