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
package org.eclipse.osee.ote.core;

import java.util.Set;
import java.util.UUID;

public interface OTESessionManager {
   void add(UUID sessionId, IUserSession session);
   void remove(UUID sessionId);
   IUserSession get(UUID sessionId);
   Set<UUID> get();
   IUserSession getActiveUser();//??
   void setActiveUser(UUID sessionId);//??
}
