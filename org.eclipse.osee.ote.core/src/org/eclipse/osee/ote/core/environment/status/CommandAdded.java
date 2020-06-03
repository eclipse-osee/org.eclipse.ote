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

package org.eclipse.osee.ote.core.environment.status;

import java.io.Serializable;
import org.eclipse.osee.ote.core.environment.command.CommandDescription;

public class CommandAdded implements IServiceStatusDataCommand, Serializable {

   private static final long serialVersionUID = -2555474494093618398L;

   private CommandDescription description;

   public CommandAdded(CommandDescription description) {
      this.description = description;
   }

   public CommandAdded() {
   }

   @Override
   public CommandDescription getDescription() {
      return description;
   }

   public void set(CommandDescription description) {
      this.description = description;
   }

   @Override
   public void accept(IServiceStatusDataVisitor visitor) {
      if (visitor != null) {
         visitor.asCommandAdded(this);
      }
   }
}
