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
package org.eclipse.osee.ote.remote.messages;

import java.io.Serializable;

import org.eclipse.osee.ote.Configuration;

public class ConfigurationAndResponse implements Serializable {

   private static final long serialVersionUID = 8496858852630392143L;

   private final String address;
   private final int port;
   private final boolean install;
   private final Configuration configuration;
   private final String id;
   
   public ConfigurationAndResponse(String address, int port, Configuration configuration, boolean install, String id){
      this.address = address;
      this.port = port;
      this.configuration = configuration;
      this.install = install;
      this.id = id;
   }
   
   public String getAddress(){
      return address;
   }
   
   public int getPort(){
      return port;
   }
   
   public Configuration getConfiguration(){
      return configuration;
   }
   
   public boolean install(){
      return install;
   }
   
   public String getId(){
      return id;
   }
   
}
