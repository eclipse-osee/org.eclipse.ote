/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ote.server;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentServiceConfig;

public interface OteServiceStarter {

   public IHostTestEnvironment start(IServiceConnector serviceSideConnector, ITestEnvironmentServiceConfig config, PropertyParamter propertyParameter, String environmentFactoryClass) throws Exception;

   public IHostTestEnvironment start(IServiceConnector serviceSideConnector, ITestEnvironmentServiceConfig config, PropertyParamter propertyParameter, TestEnvironmentFactory factory) throws Exception;

   public void stop() throws Exception;

}
