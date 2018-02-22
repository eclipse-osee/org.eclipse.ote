/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.messaging.dds.status;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ote.messaging.dds.entity.DataReader;
import org.eclipse.osee.ote.messaging.dds.entity.DataWriter;
import org.eclipse.osee.ote.messaging.dds.qos.QosPolicy;

/**
 * Maintains counts of the number of {@link DataReader}'s that the {@link DataWriter} <code>Topic</code> matched but has
 * an incompatible Qos Policy.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class OfferedIncompatibleQosStatus extends CountedStatus {

   private final long lastPolicyId;
   private final Collection<QosPolicy> policies;

   /**
    * @param totalCount The cumulative count of <code>DataReader</code>'s whose <code>Topic</code>'s match but have an
    * incompatible Qos Policy.
    * @param totalCountChange The change in count since the last time the listener was called or the status was read.
    * @param lastPolicyId The ID of one of the incompatible policies found from the last detected incompatibility.
    * @param policies The total counts of incompatibilities for each policy which has been found to be incompatible at
    * some point.
    */
   public OfferedIncompatibleQosStatus(long totalCount, long totalCountChange, long lastPolicyId, Collection<QosPolicy> policies) {
      super(totalCount, totalCountChange);
      this.lastPolicyId = lastPolicyId;
      this.policies = new ArrayList<>(policies);
   }

   /**
    * Gets the ID of one of the incompatible policies found from the last detected incompatibility.
    * 
    * @return Returns the lastPolicyId.
    */
   public long getLastPolicyId() {
      return lastPolicyId;
   }

   /**
    * Gets the total counts of incompatibilities for each policy which has been found to be incompatible at some point.
    * 
    * @return Returns the counts by individual policy.
    */
   public Collection<QosPolicy> getPolicies() {
      return policies;
   }
}
