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

package org.eclipse.osee.ote.service;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;

/**
 * This listener will be notified of test host availability events. Clients must register implementations of this
 * listener by calling {@link IOteClientService#addEnvironmentAvailibiltyListener(ITestEnvironmentAvailibilityListener)}
 * 
 * @author Ken J. Aguilar
 */
public interface ITestEnvironmentAvailibilityListener {

   /**
    * this method will be called when a {@link IHostTestEnvironment} becomes available for use.
    */
   void environmentAvailable(IServiceConnector connector, OteServiceProperties properties);

   /**
    * this method will be called whenever a {@link IHostTestEnvironment} becomes unavailable.
    */
   void environmentUnavailable(IServiceConnector connector, OteServiceProperties properties);
}
