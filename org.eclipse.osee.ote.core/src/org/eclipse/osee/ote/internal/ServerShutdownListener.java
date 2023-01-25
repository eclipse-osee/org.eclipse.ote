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

import java.rmi.RemoteException;

import org.eclipse.osee.ote.OteServiceApi;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.remote.messages.TestEnvironmentServerShutdown;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class ServerShutdownListener implements EventHandler {

   private OteServiceApi oteApi;

   public ServerShutdownListener(EventAdmin eventAdmin, OteUdpEndpoint oteEndpoint, OteServiceApi oteApi) {
      this.oteApi = oteApi;
   }

   @Override
   public void handleEvent(Event arg0) {
      if(oteApi.getIHostTestEnvironment() == null){
         return;
      }
      TestEnvironmentServerShutdown serverShutdown = new TestEnvironmentServerShutdown(OteEventMessageUtil.getBytes(arg0));
      String id;
      try {
         id = (String)oteApi.getIHostTestEnvironment().getProperties().getProperty("id", "dontknow");
         if(serverShutdown.SERVER_ID.getValue().equals(id)){
            shutdown();
         }
      } catch (RemoteException e) {
         e.printStackTrace();
      }
   }
   
   private void shutdown(){
      BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
      Bundle systemBundle = context.getBundle(0);
      try {
         systemBundle.stop();
         boolean canExit = false;
         while(!canExit){
            try{
               Thread.sleep(20);
            } catch (Throwable th){
            }
            canExit = true;
            try{
               for(Bundle b:context.getBundles()){
                  if(b.getState() != Bundle.ACTIVE){
                     canExit = false;
                  }
               }
            } catch (Throwable th){
               canExit = true;
            }
         }
      } catch (BundleException e) {
      }
      System.exit(0);
   }

}
