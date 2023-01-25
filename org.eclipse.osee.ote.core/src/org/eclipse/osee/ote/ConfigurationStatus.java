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
package org.eclipse.osee.ote;

public class ConfigurationStatus {

   private Configuration configuration;
   private boolean success;
   private String message;

   public ConfigurationStatus(Configuration configuration, boolean success, String message) {
      this.configuration = configuration;
      this.success = success;
      this.message = message;
   }

   public void setFail(String message) {
      success = false;
      this.message = message;
   }
   
   public boolean isSuccess(){
      return success;
   }
   
   public String getMessage(){
      return message;
   }
   
   public Configuration getConfiguration(){
      return configuration;
   }

}
