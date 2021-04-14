/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.ote.osgi.configuration;

import java.io.InputStream;
import org.eclipse.ote.osgi.configuration.internal.OTEPropertyConfiguration;

/**
 * This class will provide a properties file used to configure a DS service through the ConfigurationAdmin service.
 * There will be a InputStream per service to configure and you must define a property of type 'ds.component.name'. The
 * {@link OTEPropertyConfiguration} service will check system properties in case you want to overload defaults with
 * something else from the command line using -D<name>. In that case you must ensure that the option will be a unique
 * string. For services that depend on a configuration you will want to use: configuration-policy="require" in the ds
 * component. And you'll want to implement start and possibly update with the signature: public void
 * start(ComponentContext context, Map<String, Object> properties)
 * 
 * @author Michael P. Masterson
 * @author Andrew M. Finkbeiner
 */
public interface OTEConfigurationProvider {
   InputStream[] getPropertyFiles();
}
