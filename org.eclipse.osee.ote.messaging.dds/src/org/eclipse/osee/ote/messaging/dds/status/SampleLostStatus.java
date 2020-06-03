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
 * Maintains counts of the samples lost across all instances of data published for the
 * {@link org.eclipse.osee.ote.messaging.dds.entity.Topic}.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class SampleLostStatus extends CountedStatus {

   /**
    * @param totalCount The cumulative count of all samples lost for the <code>Topic</code>.
    * @param totalCountChange The change in count since the last time the listener was called or the status was read.
    */
   public SampleLostStatus(long totalCount, long totalCountChange) {
      super(totalCount, totalCountChange);
   }
}
