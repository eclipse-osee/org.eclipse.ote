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

package org.eclipse.osee.connection.service;

import java.util.Collection;

/**
 * @author Ken J. Aguilar
 */
public interface IConnectionService {

   /**
    * finds all currently existing connectors
    */
   Collection<IServiceConnector> getAllConnectors();

   /**
    * finds all available connectors that are accepted by all filters in the filter chain
    */
   Collection<IServiceConnector> findConnectors(IConnectorFilter[] filterChain);

   /**
    * registers an listener object for notification of connector events. Note that the listener object's
    * {@link IConnectorListener#onConnectorsAdded(Collection)} will be called immediately for each active connector.
    */
   void addListener(IConnectorListener listener);

   void removeListener(IConnectorListener listener);

   void addConnector(IServiceConnector connector);

   void addConnectors(Collection<IServiceConnector> connectors);

   void removeConnector(IServiceConnector connector) throws Exception;

   boolean isStopped();
}
