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

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;

/**
 * This service provides the means to find all available test servers and connect to them.
 * 
 * @author Ken J. Aguilar
 */
public interface IOteClientService {
   /**
    * registers a {@link ITestEnvironmentAvailibilityListener} that will be notified whenever a test host changes its
    * availability status. <BR>
    * <B>NOTE: </B>The newly registered listener's
    * {@link ITestEnvironmentAvailibilityListener#environmentAvailable(IHostTestEnvironment, org.eclipse.osee.connection.service.IServiceConnector, ServiceProperty)}
    * method will be called immediately for each test host currently available
    */
   void addEnvironmentAvailibiltyListener(ITestEnvironmentAvailibilityListener listener);

   void removeEnvironmentAvailibiltyListener(ITestEnvironmentAvailibilityListener listener);

   /**
    * adds the {@link ITestConnectionListener} that will be notified of connection events. <B>NOTE:</B> that if a
    * connection has already been made prior to calling this method then the listener's
    * {@link ITestConnectionListener#onPostConnect(ITestEnvironment)} method will be immediately called.
    */
   void addConnectionListener(ITestConnectionListener listener);

   void removeConnectionListener(ITestConnectionListener listener);

   /**
    * sets the user that will logged into the OTE client service. A user must be set prior to connecting to an OTE test
    * environment. If a connection is already established it will be broken prior to setting the new user.
    */
   void setUser(OSEEPerson1_4 user, InetAddress address) throws TestSessionException;

   /**
    * gets the current user as set by the {@link #setUser(OSEEPerson1_4, InetAddress)} method
    * 
    * @return the current user or null if no user has been set
    */
   OSEEPerson1_4 getUser();

   /**
    * creates a connection to a test server. <B>NOTE: </B><I>A user must be logged in prior to calling this method.>/I>
    * @param monitor 
    * 
    * @see #setUser(OSEEPerson1_4, InetAddress)
    */
   ConnectionEvent connect(IHostTestEnvironment env, IEnvironmentConfigurer configurer, TestEnvironmentConfig config, IProgressMonitor monitor) throws TestSessionException;

   /**
    * breaks the current connection to a test server. This will call the
    * {@link ITestConnectionListener#onPreDisconnect(ConnectionEvent)} method for each registered
    * {@link ITestConnectionListener} before the connection is actually broken.
    */
   void disconnect() throws TestSessionException;

   /**
    * gets the currently connected test environment
    * 
    * @return returns the connected {@link ITestEnvironment} or null if no connection exists
    */
   ITestEnvironment getConnectedEnvironment();

   /**
    * returns the connector for the currently connected test environment
    */
   IServiceConnector getConnector();

   IHostTestEnvironment getConnectedHost();

   OteServiceProperties getProperties(IHostTestEnvironment testHost);

   boolean isConnected();

   /**
    * sets a {@link SessionDelegate} who will handle certain aspects of the client session A successful call to
    * {@link #setUser(OSEEPerson1_4, InetAddress)} must have occurred prior to calling this method.
    */
   void setSessionDelegate(SessionDelegate sessionDelegate);

   /**
    * returns a collection of all the {@link IHostTestEnvironment} that are currently available.
    */
   List<IServiceConnector> getAvailableTestHosts();

   IServiceConnector getConnector(IHostTestEnvironment host);

   UUID getSessionKey();
}
