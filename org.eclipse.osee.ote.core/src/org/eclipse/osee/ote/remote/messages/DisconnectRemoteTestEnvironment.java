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
package org.eclipse.osee.ote.remote.messages;

import java.io.Serializable;
import java.util.UUID;

public class DisconnectRemoteTestEnvironment implements Serializable {

   private static final long serialVersionUID = 1100894850334052780L;
  
   private UUID id;
   
   public DisconnectRemoteTestEnvironment(UUID id){
      this.id = id;
   }

   public UUID getId() {
      return id;
   }
}
