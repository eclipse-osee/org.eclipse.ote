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
 * Maintains counts of the number of {@link org.eclipse.osee.ote.messaging.dds.entity.DataWriter}'s that the
 * {@link org.eclipse.osee.ote.messaging.dds.entity.DataReader} matched based upon <code>Topic</code> & Qos Policies.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class SubscriptionMatchStatus extends CountedStatus {
   private final InstanceHandle lastPublicationHandle;

   /**
    * @param totalCount The cumulative count of <code>DataWriter</code>'s whose Qos Policies match.
    * @param totalCountChange The change in count since the last time the listener was called or the status was read.
    * @param lastPublicationHandle The last <code>DataWriter</code> matching the <code>DataReader</code> causing the
    * status to change.
    */
   public SubscriptionMatchStatus(long totalCount, long totalCountChange, InstanceHandle lastPublicationHandle) {
      super(totalCount, totalCountChange);
      this.lastPublicationHandle = lastPublicationHandle;
   }

   /**
    * Gets a handle to the last <code>DataWriter</code> matching the DataWriter causing the status to change.
    * 
    * @return Returns the lastInstanceHandle.
    */
   public InstanceHandle getLastPublicationHandle() {
      return lastPublicationHandle;
   }
}
