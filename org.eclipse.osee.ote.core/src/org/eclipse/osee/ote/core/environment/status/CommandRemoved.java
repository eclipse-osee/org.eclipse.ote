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

public class CommandRemoved implements Serializable, IServiceStatusDataCommand {

   private static final long serialVersionUID = -177791874608013281L;
   private CommandDescription description;
   private CommandEndedStatusEnum reason;

   public CommandRemoved(CommandDescription description, CommandEndedStatusEnum reason) {
      this.description = description;
      this.reason = reason;
   }

   public CommandRemoved() {
   }

   @Override
   public CommandDescription getDescription() {
      return description;
   }

   public void setDescription(CommandDescription description) {
      this.description = description;
   }

   public void setReason(CommandEndedStatusEnum reason) {
      this.reason = reason;
   }

   public CommandEndedStatusEnum getReason() {
      return reason;
   }

   @Override
   public void accept(IServiceStatusDataVisitor visitor) {
      if (visitor != null) {
         visitor.asCommandRemoved(this);
      }
   }
}
