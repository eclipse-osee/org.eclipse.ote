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

package org.eclipse.osee.ote.messaging.dds.condition;

import java.util.Collection;
import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.entity.Entity;

/**
 * This class is here for future functionality that is described in the DDS specification but has not been implemented
 * or used.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class StatusCondition extends Condition {

   private final Collection<?> enabledStatuses;
   private final Entity parentEntity;

   public StatusCondition(Entity parentEntity) {

      this.parentEntity = parentEntity;
      enabledStatuses = null; // UNSURE find out if this should be something else?

      // This class, and the use of it has not been implemented
      throw new NotImplementedException();
   }

   public ReturnCode setEnabledStatuses(Collection<?> mask) {
      return ReturnCode.ERROR;
   }

   public Collection<?> getEnabledStatuses() {
      return enabledStatuses;
   }

   /**
    * @return Returns the entity.
    */
   public Entity getParentEntity() {
      return parentEntity;
   }
}
