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
package org.eclipse.osee.ote.master.rest.client;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.ote.master.rest.model.OTEServer;

public class OTEMasterServerAvailableNodes extends OTEMasterServerResult {

   private List<OTEServer> oteServers;
   
   public OTEMasterServerAvailableNodes(){
      oteServers = new ArrayList<>();
   }
   
   public List<OTEServer> getServers(){
      return oteServers;
   }
   
   public void setServers(OTEServer[] servers) {
      oteServers.clear();
      for(OTEServer server:servers){
         oteServers.add(server);
      }
   }

}
