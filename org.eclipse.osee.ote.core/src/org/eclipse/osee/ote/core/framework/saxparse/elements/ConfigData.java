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
package org.eclipse.osee.ote.core.framework.saxparse.elements;

/**
 * @author Andrew M. Finkbeiner
 */
public class ConfigData {
   private final String machineName;
   private final String UutConfiguration;

   /**
    * @return the machineName
    */
   public String getMachineName() {

      return machineName;
   }

   public String getUutConfiguration() {

      return UutConfiguration;
   }

   public ConfigData(String machineName, String UutConfiguration) {
      this.machineName = machineName;
      this.UutConfiguration = UutConfiguration;
   }
}
