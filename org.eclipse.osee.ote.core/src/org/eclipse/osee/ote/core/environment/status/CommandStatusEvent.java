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

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class CommandStatusEvent implements Serializable {

   private static final long serialVersionUID = -567005567921815848L;
   private final CommandDescription description;

   /**
    * CommandStatusEvent Constructor.
    * 
    * @param description The command description.
    */
   public CommandStatusEvent(CommandDescription description) {
      super();
      this.description = description;
   }

   /**
    * @return Returns the description.
    */
   public CommandDescription getDescription() {
      return description;
   }
}