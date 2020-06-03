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

import org.eclipse.osee.ote.messaging.dds.NotImplementedException;

/**
 * This class is here for future functionality that is described in the DDS specification but has not been implemented
 * or used.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class Condition {
   protected boolean trigger;

   public Condition() {
      this.trigger = false;

      // This class, and the use of it has not been implemented
      throw new NotImplementedException();
   }

   public boolean getTrigger() {
      return trigger;
   }
}
