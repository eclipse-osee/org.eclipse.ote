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

package org.eclipse.osee.ote.connection.jini;

import java.io.IOException;
import java.net.MalformedURLException;
import net.jini.core.discovery.LookupLocator;

/**
 * @author Ken J. Aguilar
 */
public interface IJiniConnectorRegistrar {
   void addLocators(String... hosts) throws MalformedURLException, ClassNotFoundException, IOException;

   LookupLocator[] getLocators();

   void addGroup(String... groups) throws IOException;

   String[] getGroups();
}
