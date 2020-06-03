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

import org.eclipse.osee.ote.core.environment.command.CommandDescription;

public class SequentialCommandBegan implements IServiceStatusDataCommand {

   private static final long serialVersionUID = -3278399375292593249L;
   private CommandDescription description;

   public SequentialCommandBegan(CommandDescription description) {
      this.description = description;
   }

   public SequentialCommandBegan() {
   }

   public void set(CommandDescription description) {
      this.description = description;
   }

   @Override
   public CommandDescription getDescription() {
      return description;
   }

   @Override
   public void accept(IServiceStatusDataVisitor visitor) {
      if (visitor != null) {
         visitor.asSequentialCommandBegan(this);
      }
   }
}
