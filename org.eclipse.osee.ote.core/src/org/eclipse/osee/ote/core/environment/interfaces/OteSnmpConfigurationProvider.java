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
package org.eclipse.osee.ote.core.environment.interfaces;

/**
 * Service interface for providing SNMP connection/configuration information
 * 
 * @author Michael P. Masterson
 */
public interface OteSnmpConfigurationProvider {

   /**
    * @param id The snmp server/agent identifier
    * @return the ip address as a String
    */
   public String getIpString(String id);

}
