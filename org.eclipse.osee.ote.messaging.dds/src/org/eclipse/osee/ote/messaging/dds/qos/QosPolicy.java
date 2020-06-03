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

package org.eclipse.osee.ote.messaging.dds.qos;

import org.eclipse.osee.ote.messaging.dds.NotImplementedException;

/**
 * This class is here for future functionality that is described in the DDS specification but has not been implemented
 * or used.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class QosPolicy {
   public final static QosPolicy USERDATA_QOS_POLICY = new QosPolicy("UserData", 1);

   private final String policyName;
   private final long policyId;

   private QosPolicy(String policyName, long policyId) {
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) {
         throw new NotImplementedException();
      }
      this.policyName = policyName;
      this.policyId = policyId;
   }

   public String getPolicyName() {
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) {
         throw new NotImplementedException();
      }

      return policyName;
   }

   public long getPolicyId() {
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) {
         throw new NotImplementedException();
      }

      return policyId;
   }
}
