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

package org.eclipse.osee.ote.messaging.dds.listener;

import org.eclipse.osee.ote.messaging.dds.DataStoreItem;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;

/**
 * The interface for receiving events from the <code>DomainParticipant</code>. This class also extends the
 * <code>TopicListener</code>, <code>SubscriberListener</code>, and <code>PublisherListener</code>.
 * 
 * @see org.eclipse.osee.ote.messaging.dds.entity.DomainParticipant
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public interface DomainParticipantListener extends TopicListener, SubscriberListener, PublisherListener {

   /**
    * The method called whenever data is published within the DDS system. This can be used by the listening object to
    * propogate information out to local entities running DDS.
    * 
    * @param destination TODO
    * @param source TODO
    * @param dataStoreItem - The published information.
    */
   public void onPublishNotifyMiddleware(IDestination destination, ISource source, DataStoreItem dataStoreItem);

}
