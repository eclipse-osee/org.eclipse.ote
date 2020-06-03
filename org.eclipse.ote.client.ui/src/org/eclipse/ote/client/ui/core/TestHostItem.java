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

package org.eclipse.ote.client.ui.core;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.service.OteServiceProperties;

/**
 * @author Ken J. Aguilar
 */
public class TestHostItem {
   private final OteServiceProperties properties;
   private final IServiceConnector connector;
   private ITestEnvironment connectedEnvirnonment;

   public TestHostItem(IServiceConnector connector) {
      this.connector = connector;
      this.properties = new OteServiceProperties(connector);
   }

   public IHostTestEnvironment getTestHost() {
	  return (IHostTestEnvironment)connector.getService();
   }

   public OteServiceProperties getProperties() {
      return properties;
   }

   @Override
   public boolean equals(Object arg0) {
      if (arg0 instanceof TestHostItem) {
         return ((TestHostItem) arg0).properties.getProperty("id").equals(properties.getProperty("id"));
      }
      return false;
   }

   @Override
   public int hashCode() {
	   return properties.getProperty("id").hashCode();
   }

   public boolean isConnected() {
      return connectedEnvirnonment != null;
   }

   public void setConnectedEnvironment(ITestEnvironment environment) {
      this.connectedEnvirnonment = environment;
   }

   public ITestEnvironment getConnectedEnvironment() {
      return connectedEnvirnonment;
   }

   public IServiceConnector getConnector() {
      return connector;
   }
}
