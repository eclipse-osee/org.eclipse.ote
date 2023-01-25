/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.master.rest.client;

import java.net.URI;
import java.util.concurrent.Future;

import org.eclipse.osee.ote.master.rest.model.OTEServer;

public interface OTEMasterServer {
   Future<OTEMasterServerAvailableNodes> getAvailableServers(URI uri);
   Future<OTEMasterServerResult> addServer(URI uri, OTEServer server);
   Future<OTEMasterServerResult> removeServer(URI uri, OTEServer server);
}
