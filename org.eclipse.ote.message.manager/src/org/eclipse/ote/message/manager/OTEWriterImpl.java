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

package org.eclipse.ote.message.manager;

import java.util.List;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.io.IOWriter;
import org.eclipse.osee.ote.message.io.IOWriterProvider;
import org.eclipse.osee.ote.messaging.dds.Data;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;
import org.eclipse.osee.ote.messaging.dds.InstanceHandle;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.entity.DataWriter;
import org.eclipse.osee.ote.messaging.dds.entity.EntityFactory;
import org.eclipse.osee.ote.messaging.dds.entity.Publisher;
import org.eclipse.osee.ote.messaging.dds.entity.Topic;
import org.eclipse.osee.ote.messaging.dds.listener.DataWriterListener;

/**
 * Contains the list of actual writers ensuring that each writer that applies is called when the DDS
 * system issues a write() call.
 * 
 * @author Michael P. Masterson
 */
public class OTEWriterImpl extends DataWriter {

   private TopicDescriptionImpl topic;
   private IOWriterProvider physicalWriter;
   private OTETopicLookup topicLookup;
   private MessageData messageData;

   public OTEWriterImpl(Topic topic, Publisher publisher, Boolean enabled,
         DataWriterListener listener, EntityFactory parentFactory, IOWriterProvider physicalWriter,
         OTETopicLookup topicLookup, MessageData messageData, String namespace) {
      super(topic, publisher, enabled, listener, parentFactory);
      this.topic = new TopicDescriptionImpl(messageData.getTopicName(), namespace);
      this.physicalWriter = physicalWriter;
      this.topicLookup = topicLookup;
      this.messageData = messageData;
   }

   @Override
   public ReturnCode write(IDestination destination, ISource source, Data data,
         InstanceHandle handle) {
      List<IOWriter> writers = physicalWriter.getWriters(topic.getNamespace());
      if (writers != null) {
         int size = writers.size();
         for (int i = 0; i < size; i++) {
            IOWriter writer = writers.get(i);
            if (writer.accept(topic.getName())) {
               writer.write(destination, source, messageData);
            }
         }
      }
      MessageData readerData = topicLookup.getReader(topic);
      if (readerData != null) {
         readerData.copyData(messageData.getMem().getBuffer());
         readerData.incrementActivityCount();
         readerData.notifyListeners();
      }
      messageData.notifyListeners();
      return ReturnCode.OK;
   }

}
