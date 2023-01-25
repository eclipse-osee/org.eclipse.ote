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
package org.eclipse.osee.ote.internal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;

import org.eclipse.osee.ote.OteServiceApi;
import org.eclipse.osee.ote.core.ConnectionRequestResult;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.endpoint.OteUdpEndpointSender;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.RequestRemoteTestEnvironment;
import org.eclipse.osee.ote.remote.messages.SerializedConnectionRequestResult;
import org.eclipse.osee.ote.remote.messages.SerializedRequestRemoteTestEnvironment;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class ConnectionListener implements EventHandler {

   private OteUdpEndpoint oteEndpoint;
   private OteServiceApi oteApi;
   
   public ConnectionListener(EventAdmin eventAdmin, OteUdpEndpoint oteEndpoint, OteServiceApi oteApi) {
      this.oteEndpoint = oteEndpoint;
      this.oteApi = oteApi;
   }


   @Override
   public void handleEvent(Event arg0) {
      SerializedRequestRemoteTestEnvironment serialized = new SerializedRequestRemoteTestEnvironment(OteEventMessageUtil.getBytes(arg0));
      RequestRemoteTestEnvironment request;
      try {
         request = serialized.getObject();
         IHostTestEnvironment hostTestEnvironment = oteApi.getIHostTestEnvironment();
         if(hostTestEnvironment != null){
            try {
               InetSocketAddress address = serialized.getHeader().getSourceInetSocketAddress();
               ConnectionRequestResult requestEnvironment = hostTestEnvironment.requestEnvironment(request.getSession(), request.getId(), request.getConfig());
               OteUdpEndpointSender oteEndpointSender = oteEndpoint.getOteEndpointSender(address);
               SerializedConnectionRequestResult serializedConnectionRequestResult = new SerializedConnectionRequestResult(requestEnvironment);
               serializedConnectionRequestResult.setResponse(serialized);
               oteEndpointSender.send(serializedConnectionRequestResult);
               oteEndpoint.addBroadcast(oteEndpointSender);
            } catch (RemoteException e) {
               e.printStackTrace();
            }
         }
      } catch (IOException e1) {
         e1.printStackTrace();
      } catch (ClassNotFoundException e1) {
         e1.printStackTrace();
      }
   }

}
