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

/**
 * Maintains counts of the number of times the {@link org.eclipse.osee.ote.messaging.dds.entity.DataWriter} failed to
 * signal its liveliness within the liveliness period.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class LivelinessLostStatus extends CountedStatus {

   /**
    * @param totalCount The cumulative count of liveliness lost.
    * @param totalCountChange The change in count since the last time the listener was called or the status was read.
    */
   public LivelinessLostStatus(long totalCount, long totalCountChange) {
      super(totalCount, totalCountChange);
   }

}
