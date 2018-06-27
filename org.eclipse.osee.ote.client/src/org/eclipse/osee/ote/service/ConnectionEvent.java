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
package org.eclipse.osee.ote.service;

import java.util.UUID;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;

/**
 * This event contains information regarding a change in connectivity between a client and a test host
 * 
 * @author Ken J. Aguilar
 */
public final class ConnectionEvent {

   private final ITestEnvironment environment;
   private final IServiceConnector connector;
   private final UUID sessionKey;
   private final OteServiceProperties props;
   private final IHostTestEnvironment hostTestEnvironment;

   public ConnectionEvent(IHostTestEnvironment hostTestEnvironment, IServiceConnector connector, ITestEnvironment environment, UUID sessionKey) {
      if (connector == null) {
         throw new NullPointerException("connector cannot be null");
      }
      this.environment = environment;
      this.connector = connector;
      this.sessionKey = sessionKey;
      this.hostTestEnvironment = hostTestEnvironment;
      props = new OteServiceProperties(connector);
   }

   /**
    * returns the test environment in which the connection event applies to
    * 
    * @return the environment
    */
   public ITestEnvironment getEnvironment() {
      return environment;
   }

   /**
    * returns the {@link IServiceConnector} that provides the communication interface to the remote service
    * 
    * @return the connector
    */
   public IServiceConnector getConnector() {
      return connector;
   }

   /**
    * gets the test environment session key
    * 
    * @return the sessionKey
    */
   public UUID getSessionKey() {
      return sessionKey;
   }

   public OteServiceProperties getProperties() {
      return props;
   }

   public IHostTestEnvironment getHostEnvironment() {
      return hostTestEnvironment;
   }

}
