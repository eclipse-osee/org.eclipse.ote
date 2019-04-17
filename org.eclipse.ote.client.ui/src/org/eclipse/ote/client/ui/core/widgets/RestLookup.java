/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.client.ui.core.widgets;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.endpoint.OteEndpointUtil;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServer;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerAvailableNodes;
import org.eclipse.osee.ote.master.rest.model.OTEServer;
import org.eclipse.osee.ote.properties.OtePropertiesCore;
import org.eclipse.ote.client.ui.internal.ServiceUtil;

/**
 * @author Andrew M. Finkbeiner
 */
public class RestLookup {
   
   private static final Set<String> alreadyReported = new HashSet<>();

   private static final String OTE_MASTER_URI = System.getProperty("ote.master.uri", "http://localhost:8008/");

   private static final long CHECK_GONE_TIME_ELAPSED = 1000 * 60 * 5;
   
   private Map<String, Pair<Boolean, RestLookupConnector>> connectors;
   private Map<String, Long> lastCheckedRemoval;
   
   public RestLookup() {
      connectors = new HashMap<>();
      lastCheckedRemoval = new HashMap<>();
   }
   
   synchronized void removeConnector(String id){
      IConnectionService connection = ServiceUtil.getService(IConnectionService.class);
      Pair<Boolean, RestLookupConnector> pair = connectors.get(id);
      if(pair != null){
         try {
            connection.removeConnector(pair.getSecond());
            connectors.remove(id);
         } catch (Exception e) {
            OseeLog.log(getClass(), Level.SEVERE, e);
         }
      } 
   }
   
   /**
    * This should only be used for direct uri connection
    * 
    * @param uri
    * @return A connector if a connection is available
    */
   public synchronized RestLookupConnector add(String uri){
      try{
         OTEServer server = new OTEServer();
         InetSocketAddress address = OteEndpointUtil.getAddress(uri);
         server.setOteRestServer(uri);
         HostProxy hostProxy = new HostProxy(ServiceUtility.getService(OteUdpEndpoint.class), address, server);
         EnhancedProperties properties = hostProxy.getProperties();
         if(properties != null){
            server.setUUID((String)properties.getProperty("id", "unknown"));
            server.setOteActivemqServer((String)properties.getProperty("activeMq", "tcp://localhost:61611"));

            IConnectionService connection = ServiceUtil.getService(IConnectionService.class);
            if(connection != null){
               EventMessageConnector connector = new EventMessageConnector(server, address);
               IHostTestEnvironment host = (IHostTestEnvironment)connector.getService();
               connector.setFields(host.getProperties());
               connectors.put(server.getUUID(), new Pair<Boolean, RestLookupConnector>(true, connector));
               connection.addConnector(connector);
               return connector;
            }
         }
      } catch (Throwable th){
         OseeLog.log(getClass(), Level.SEVERE, th);
      }
      return null;
   }

   public synchronized boolean getLatest() {
      try {
         IConnectionService connection = ServiceUtil.getService(IConnectionService.class);
         OTEMasterServer master = ServiceUtil.getService(OTEMasterServer.class);
         if(connection != null && master != null){
            Future<OTEMasterServerAvailableNodes> availableServers = master.getAvailableServers(new URI(OTE_MASTER_URI));
            OTEMasterServerAvailableNodes oteMasterServerAvailableNodes = availableServers.get(20, TimeUnit.SECONDS);
            List<OTEServer> servers = oteMasterServerAvailableNodes.getServers();

            initializeConnectionsStore();

            for(OTEServer server:servers){
               try{
                  Pair<Boolean, RestLookupConnector> pair = connectors.get(server.getUUID());
                  if(pair == null){
                     RestLookupConnector connector = getRestConnector(server);
                     connectors.put(server.getUUID(), new Pair<Boolean, RestLookupConnector>(true, connector));
                     connection.addConnector(connector);
                  } else {//update changed fields
                     pair.setFirst(true);
                     pair.getSecond().setUserList(server.getConnectedUsers());
                  }
               } catch (Throwable th){
                  
                  if(!alreadyReported.contains(th.getMessage())){
                     th.printStackTrace();
                     alreadyReported.add(th.getMessage());
                  }
               }
            }

            removeMissingConnectors(connection);
            
            if(servers.size() == 0){
               return false;
            }
         } else {
            return false;
         }
      } catch (URISyntaxException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
         return false;
      } catch (InterruptedException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
         return false;
      } catch (ExecutionException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
         return false;
      } catch (Throwable e){
         OseeLog.log(getClass(), Level.SEVERE, e);
         return false;
      }
      return true;
   }

   private RestLookupConnector getRestConnector(OTEServer server) {
      InetSocketAddress address = OteEndpointUtil.getAddress(server.getOteRestServer());
      return new EventMessageConnector(server, address);
   }

   private void removeMissingConnectors(IConnectionService connection) {
      List<String> toRemove = new ArrayList<>();
      long currentTime = System.currentTimeMillis();
      for(String id:connectors.keySet()){
         if(!connectors.get(id).getFirst()){
            Long lastCheck = lastCheckedRemoval.get(id);
            if(lastCheck == null || currentTime - lastCheck.longValue() > CHECK_GONE_TIME_ELAPSED ) {
               lastCheckedRemoval.put(id, currentTime);
               toRemove.add(id);
            }
         }
      }
      for(String id: toRemove){
         Pair<Boolean, RestLookupConnector> remove = connectors.get(id);
         boolean failedToPing = true;
         if(remove.getSecond().isConnected()){ //if we think we're connected try extra hard
            long timeoutValue = OtePropertiesCore.pingTimeout.getLongValue(5000);
            failedToPing = !remove.getSecond().ping(timeoutValue);
         } 
         
         if(failedToPing){//guard against it just being the lookupserver gone
            try {
               connectors.remove(id);
               connection.removeConnector(remove.getSecond());
               lastCheckedRemoval.remove(id);
            } catch (Exception e) {
               OseeLog.log(getClass(), Level.SEVERE, e);
            }
         }
         
      }
   }

   private void initializeConnectionsStore() {
      for(Pair<Boolean, RestLookupConnector> pair:connectors.values()){
         pair.setFirst(false);                     
      }
   }

}
