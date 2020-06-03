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

package org.eclipse.osee.ote.messaging.dds.status;

import org.eclipse.osee.ote.messaging.dds.InstanceHandle;

/**
 * Maintains counts of the number of times the {@link org.eclipse.osee.ote.messaging.dds.entity.DataWriter} failed to
 * write within the offered deadline.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class OfferedDeadlineMissedStatus extends CountedStatus {
   private final InstanceHandle lastInstanceHandle;

   /**
    * @param totalCount The cumulative count of offered deadlines missed.
    * @param totalCountChange The change in count since the last time the listener was called or the status was read.
    * @param lastInstanceHandle The last instance in the <code>DataWriter</code> for which the deadline was missed.
    */
   public OfferedDeadlineMissedStatus(long totalCount, long totalCountChange, InstanceHandle lastInstanceHandle) {
      super(totalCount, totalCountChange);
      this.lastInstanceHandle = lastInstanceHandle;
   }

   /**
    * Gets a handle to the instance in the <code>DataWriter</code> for which the last offered deadline that was missed.
    * 
    * @return Returns the lastInstanceHandle.
    */
   public InstanceHandle getLastInstanceHandle() {
      return lastInstanceHandle;
   }
}
