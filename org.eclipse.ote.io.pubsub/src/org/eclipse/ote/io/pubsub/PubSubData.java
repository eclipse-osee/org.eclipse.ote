/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.io.pubsub;

import java.nio.BufferUnderflowException;
import java.nio.InvalidMarkException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.data.IMessageDataVisitor;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;
import org.eclipse.ote.io.GenericOteIoType;

/**
 * @author Michael P. Masterson
 */
public class PubSubData extends MessageData {

   private final PubSubHeader header;
   protected int id;

   private final List<BasicLogicalParticipant> publishers;
   private final List<BasicLogicalParticipant> subscribers;
   private final PubSubSubscriber pubSubscriberForSends = new PubSubSubscriber();

   public PubSubData(Message msg, String typeName, String name, int dataByteSize, int offset, int id) {
      super(typeName, name, dataByteSize + PubSubHeader.HEADER_BYTE_SIZE, PubSubHeader.HEADER_BYTE_SIZE + offset,
         GenericOteIoType.PUB_SUB);
      publishers = new ArrayList<>();
      subscribers = new ArrayList<>();
      header = new PubSubHeader(msg, name, this.getMem().slice(0, PubSubHeader.HEADER_BYTE_SIZE));
      this.id = id;
      setupHeaderInfo();
   }

   public void setupHeaderInfo() {
      header.MSG_DATA_SIZE.setValue(this.get().length - PubSubHeader.HEADER_BYTE_SIZE);
      header.HEADER_SIZE.setValue(PubSubHeader.HEADER_BYTE_SIZE);
      header.MSG_NAME.setValue(getName());
      header.MSG_ID.setValue(id);
   }

   public int getId() {
      return id;
   }

   @Override
   public int getPayloadSize() {
      return getHeader().MSG_DATA_SIZE.getNoLog().intValue();
   }

   public int getHeaderSize() {
      return PubSubHeader.HEADER_BYTE_SIZE;
   }

   @Override
   public String getTopicName() {
      return getName();
   }

   public PubSubHeader getHeader() {
      return header;
   }

   public byte[] get() {
      return getMem().getData();
   }

   @Override
   public PubSubHeader getMsgHeader() {
      return header;
   }

   @Override
   public void initializeDefaultHeaderValues() {
      setupHeaderInfo();
   }

   public boolean addSubscriber(BasicLogicalParticipant subscriber) {
      return subscribers.add(subscriber);
   }

   public boolean removeSubscriber(BasicLogicalParticipant subscriber) {
      return subscribers.remove(subscriber);
   }

   public boolean addAllSubscribers(List<BasicLogicalParticipant> subscribers) {
      return this.subscribers.addAll(subscribers);
   }

   public void clearSubscribers() {
      subscribers.clear();
   }

   public boolean addPublisher(BasicLogicalParticipant publisher) {
      return publishers.add(publisher);
   }

   public boolean removePublishers(BasicLogicalParticipant publisher) {
      return publishers.remove(publisher);
   }

   public boolean addAllPublishers(List<BasicLogicalParticipant> publishers) {
      return this.publishers.addAll(publishers);
   }

   public void clearPublishers() {
      publishers.clear();
   }

   public List<BasicLogicalParticipant> getPublishers() {
      return publishers;
   }

   public List<BasicLogicalParticipant> getSubScribers() {
      return subscribers;
   }

   @Override
   public void send() throws MessageSystemException {
      if (writer == null) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, getName() + " - the writer is null");
      } else if (shouldSendData()) {
         try {
            commonWrite(publishers, subscribers);
         } catch (BufferUnderflowException ex) { // deal with concurrency issues in MemoryResource.copyData
            Exception exception = new Exception("Concurrent execution issue likely due to calling send on a scheduled message.", ex);
            OseeLog.log(getClass(), Level.WARNING, exception);
         } catch (InvalidMarkException ex) { // deal with concurrency issues in MemoryResource.copyData
            Exception exception = new Exception("Concurrent execution issue likely due to calling send on a scheduled message.", ex);
            OseeLog.log(getClass(), Level.WARNING, exception);
         } catch (Throwable ex) {
            ex.printStackTrace();
            throw new MessageSystemException("Could not send message data " + getName(), Level.SEVERE, ex);
         }
      }
   }

   @Override
   protected void sendTo(IDestination destination, ISource source) throws MessageSystemException {
      if (writer == null) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, getName() + " - the writer is null");
      } else if (shouldSendData()) {
         try {
            BasicLogicalParticipant[] subscriber = null;
            if (destination instanceof PubSubSubscriber) {
               subscriber = new BasicLogicalParticipant[] {((PubSubSubscriber) destination).getParticipant()};
            }

            BasicLogicalParticipant publisher = null;
            if (source instanceof PubSubPublisher) {
               publisher = ((PubSubPublisher) source).getParticipant();
            }
            commonWrite(Collections.asList(publisher), Collections.asList(subscriber));
         } catch (Throwable ex) {
            throw new MessageSystemException("Could not send message data " + getName(), Level.SEVERE, ex);
         }
      }

   }

   private void commonWrite(List<BasicLogicalParticipant> publishersToWrite, List<BasicLogicalParticipant> subscribersToWrite
      ) {
      performOverride();
      boolean incremented = false;
      header.SEQUENCE_NUM.setValue(header.SEQUENCE_NUM.getValue() + 1);
      for (int i = 0; i < publishersToWrite.size() && publishersToWrite.get(i) != null; i++) {
         BasicLogicalParticipant logicalPublisher = publishersToWrite.get(i);
         header.SOURCE_ID.setValue(logicalPublisher.getId());
         for (int j = 0; j < subscribersToWrite.size() && subscribersToWrite.get(j) != null; j++) {
            if (logicalPublisher.isEnabled()) {
               BasicLogicalParticipant logicalSubscriber = subscribersToWrite.get(j);
               header.DEST_ID.setValue(logicalSubscriber.getId());
               getMem().setDataHasChanged(false);
               pubSubscriberForSends.set(logicalSubscriber);
               writer.write(pubSubscriberForSends, null, this, null);
               if (!incremented) {
                  incrementSentCount();
                  incremented = true;
               }
            }
         }
      }
   }

   @Override
   public void visit(IMessageDataVisitor visitor) {
      // do nothing
   }

}
