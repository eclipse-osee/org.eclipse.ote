/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ote.message.io;

import java.util.Collection;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.messaging.dds.DataStoreItem;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;

/**
 * Interface for sending data from a source to a destination.  Generally there is one
 * IOWriter for each DDS namespace.
 * 
 * @author Michael P. Masterson
 */
public interface IOWriter {
   /**
    * This method should be implemented as a synchronous one-shot write
    */
   void write(IDestination destination, ISource source, DataStoreItem data);

   /**
    * This method can be synchronous or asynchronous, there is no guarantee that when it
    * returns the message data has been sent.  Of course it should be sent very shortly after
    */
   void write(IDestination destination, ISource source, MessageData data);

   /**
    * This method can be synchronous or asynchronous, there is no guarantee that when it
    * returns the message data's have been sent.  It is expected that the sender will aggregate the
    * list of messages if possible.
    */
   void write(IDestination destination, ISource source, Collection<MessageData> data);

   /**
    * @param topic Topic string that may or may not match the namespace
    * @return true iff this writer is applicable to the topic
    */
   boolean accept(String topic);
   
   /**
    * @return The namespace associated with this writer
    */
   String getNamespace();
}
