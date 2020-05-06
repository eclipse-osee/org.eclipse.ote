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

package org.eclipse.osee.ote.message.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.io.IOWriter;
import org.eclipse.osee.ote.message.io.IOWriterProvider;
import org.eclipse.osee.ote.messaging.dds.DataStoreItem;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;
import org.eclipse.osee.ote.messaging.dds.entity.DataReader;
import org.eclipse.osee.ote.messaging.dds.entity.DataWriter;
import org.eclipse.osee.ote.messaging.dds.entity.Subscriber;
import org.eclipse.osee.ote.messaging.dds.entity.Topic;
import org.eclipse.osee.ote.messaging.dds.listener.DomainParticipantListener;
import org.eclipse.osee.ote.messaging.dds.status.InconsistentTopicStatus;
import org.eclipse.osee.ote.messaging.dds.status.LivelinessChangedStatus;
import org.eclipse.osee.ote.messaging.dds.status.LivelinessLostStatus;
import org.eclipse.osee.ote.messaging.dds.status.OfferedDeadlineMissedStatus;
import org.eclipse.osee.ote.messaging.dds.status.OfferedIncompatibleQosStatus;
import org.eclipse.osee.ote.messaging.dds.status.PublicationMatchStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedDeadlineMissedStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedIncompatibleQosStatus;
import org.eclipse.osee.ote.messaging.dds.status.SampleLostStatus;
import org.eclipse.osee.ote.messaging.dds.status.SampleRejectedStatus;
import org.eclipse.osee.ote.messaging.dds.status.SubscriptionMatchStatus;

/**
 * Main entry point for sending messages to the UUT. When the DDS system notifies this listener of a
 * transmission request, the appropriate writer is called.
 * 
 * Most of the other api is intentionally left empty as this class only cares about sending the 
 * published data.
 * 
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public class DDSDomainParticipantListener implements DomainParticipantListener, IOWriterProvider {

   private final Map<String, List<IOWriter>> allwriters = new HashMap<String, List<IOWriter>>();

   public DDSDomainParticipantListener() {
      GCHelper.getGCHelper().addRefWatch(this);
   }

   @Override
   public void onInconsistentTopic(Topic theTopic, InconsistentTopicStatus status) {
      // do nothing
   }

   @Override
   public void onDataOnReaders(Subscriber theSubscriber) {
      // do nothing
   }

   @Override
   public void onDataAvailable(DataReader theReader) {
      // do nothing
   }

   @Override
   public void onSampleRejected(DataReader theReader, SampleRejectedStatus status) {
      // do nothing
   }

   @Override
   public void onLivelinessChanged(DataReader theReader, LivelinessChangedStatus status) {
      // do nothing
   }

   @Override
   public void onRequestedDeadlineMissed(DataReader theReader,
         RequestedDeadlineMissedStatus status) {
      // do nothing
   }

   @Override
   public void onRequestedIncompatibleQos(DataReader theReader,
         RequestedIncompatibleQosStatus status) {
      // do nothing
   }

   @Override
   public void onSubscriptionMatch(DataReader theReader, SubscriptionMatchStatus status) {
      // do nothing
   }

   @Override
   public void onSampleLost(DataReader theReader, SampleLostStatus status) {
      // do nothing
   }

   @Override
   public void onLivelinessLost(DataWriter theWriter, LivelinessLostStatus status) {
      // do nothing
   }

   @Override
   public void onOfferedDeadlineMissed(DataWriter theWriter, OfferedDeadlineMissedStatus status) {
      // do nothing
   }

   @Override
   public void onOfferedIncompatibleQos(DataWriter theWriter, OfferedIncompatibleQosStatus status) {
      // do nothing
   }

   @Override
   public void onPublicationMatch(DataWriter theWriter, PublicationMatchStatus status) {
      // do nothing
   }

   @Override
   public void onDataSentToMiddleware(DataWriter theWriter) {
      // do nothing
   }

   @Override
   public void onPublishNotifyMiddleware(IDestination destination, ISource source,
         DataStoreItem dataStoreItem) {
      List<IOWriter> writers = allwriters.get(dataStoreItem.getTheTopicDescription().getNamespace());
      if (writers != null) {
         for (IOWriter writer : writers) {
            if (writer.accept(dataStoreItem.getTheTopicDescription().getName())) {
               writer.write(destination, source, dataStoreItem);
            }
         }
      }
   }

   public List<IOWriter> getWriters(String namespace) {
      return allwriters.get(namespace);
   }

   public void registerWriter(IOWriter writer) {
      List<IOWriter> writers = allwriters.get(writer.getNamespace());
      if (writers == null) {
         writers = new ArrayList<IOWriter>();
         allwriters.put(writer.getNamespace(), writers);
      }
      if (writers.contains(writer)) {
         throw new MessageSystemException("A writer with the namespace [" + writer.getNamespace()
                                          + "] already exists.", Level.SEVERE);
      }
      else {
         writers.add(writer);
      }
   }

   public void unregisterWriter(IOWriter writer) {
      List<IOWriter> writers = allwriters.get(writer.getNamespace());
      if (writers != null) {
         if (!writers.remove(writer)) {
            OseeLog.log(DDSDomainParticipantListener.class, Level.WARNING,
                        String.format("Unable to remove %s, %s", writer.getNamespace(),
                                      writer.getClass().getName()));
         }
      }
   }

   public void dispose() {
      allwriters.clear();
   }
}
