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
package org.eclipse.osee.ote.master;

import java.util.List;
import java.util.UUID;

public interface OTELookup {
   List<OTELookupServerEntry> getAvailableServers();

   void addServer(OTELookupServerEntry server);

   void removeServer(OTELookupServerEntry server);

   void removeServer(UUID fromString);
}
