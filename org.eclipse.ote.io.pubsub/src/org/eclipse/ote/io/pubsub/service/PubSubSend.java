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
package org.eclipse.ote.io.pubsub.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.osee.ote.message.io.IOWriter;
import org.eclipse.osee.ote.messaging.dds.DataStoreItem;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;
import org.eclipse.ote.io.BasicDatagramChannelDataFactory;
import org.eclipse.ote.io.BasicDatagramChannelRunnable;
import org.eclipse.ote.io.DatagramChannelData;
import org.eclipse.ote.io.DatagramChannelDataPool;
import org.eclipse.ote.io.DatagramChannelWorker;
import org.eclipse.ote.io.pubsub.BasicLogicalParticipant;
import org.eclipse.ote.io.pubsub.PubSubData;
import org.eclipse.ote.io.pubsub.PubSubHeader;
import org.eclipse.ote.io.pubsub.PubSubSubscriber;
import org.eclipse.ote.io.pubsub.config.PubSubEthernetConfigurationProvider;

/**
 * @author Michael P. Masterson
 */
public class PubSubSend implements IOWriter {

   private static boolean sendWithLimit = true;

   static {
      String value = System.getProperty("ote.pubsub.send.limit");
      if (value != null) {
         sendWithLimit = Boolean.parseBoolean(value);
      }
   }

   private final Namespace namespace;
   private final DatagramChannelDataPool datagramChannelDataPool;
   private final InetSocketAddress xmitAddress;
   private final DatagramChannelWorker datagramChannelWorker;
   private final ConcurrentHashMap<SocketAddress, List<SocketAddress>> subscribers;
   private final PubSubEthernetConfigurationProvider config;

   public PubSubSend(Namespace namespace, PubSubEthernetConfigurationProvider config) throws IOException {
      GCHelper.getGCHelper().addRefWatch(this);
      subscribers = new ConcurrentHashMap<SocketAddress, List<SocketAddress>>();
      datagramChannelDataPool = new DatagramChannelDataPool(new BasicDatagramChannelDataFactory(), 32);
      xmitAddress = getValidAddress();
      datagramChannelWorker =
         new DatagramChannelWorker("OTE PubSub Send Worker", new BasicDatagramChannelRunnable(xmitAddress));
      datagramChannelWorker.start();
      this.namespace = namespace;
      this.config = config;
   }

   private InetSocketAddress getValidAddress() throws IOException {
      return new InetSocketAddress(InetAddress.getLocalHost(), PortUtil.getInstance().getValidPort());
   }

   private void write(PubSubData data, BasicLogicalParticipant subscriberDef) {//v1
      try {
         if (subscriberDef.isEnabled()) {
            DatagramChannelData datagramChannelData = datagramChannelDataPool.get();
            datagramChannelData.getByteBuffer().clear();
            if (sendWithLimit) {
               byte[] srcBytes = data.get();
               int msgBodyDataSize = data.getMsgHeader().MSG_DATA_SIZE.getValue();
               int length = msgBodyDataSize + PubSubHeader.HEADER_BYTE_SIZE;
               if (length > srcBytes.length) {
                  length = srcBytes.length;
               }
               if (length > BasicDatagramChannelDataFactory.DatagramByteBufferSize) {
                  OseeLog.log(getClass(), Level.WARNING, String.format(
                     "For %s the byte length calculated from the header [%d] is larger than the byte buffer size [%d]." +
                        "\nThe message length (including header) is being truncated to [%d]." +
                        "\nThis applies to what will be displayed for this message in any of the Message Tools (ex. Message Watch) as well.",
                        data.getMsgHeader().MSG_NAME.toString(), length,
                        BasicDatagramChannelDataFactory.DatagramByteBufferSize,
                        BasicDatagramChannelDataFactory.DatagramByteBufferSize));
                  length = BasicDatagramChannelDataFactory.DatagramByteBufferSize;
                  data.getMsgHeader().MSG_DATA_SIZE.setNoLog(length - PubSubHeader.HEADER_BYTE_SIZE);
               }
               datagramChannelData.getByteBuffer().put(srcBytes, 0, length);
            } else {
               datagramChannelData.getByteBuffer().put(data.get());
            }
            SocketAddress currentAddress = config.getAddress(subscriberDef.getName());
            List<SocketAddress> addresses = subscribers.get(currentAddress);
            if (addresses == null) {
               addresses = new ArrayList<SocketAddress>();
               addresses.add(currentAddress);
               subscribers.put(currentAddress, addresses);
            }
            datagramChannelData.setAddresses(addresses);
            datagramChannelWorker.send(datagramChannelData);
         }
      } catch (InterruptedException ex2) {
         Thread.currentThread().interrupt();
      }
   }

   public void dispose() {
      try {
         datagramChannelWorker.stop();
      } catch (Throwable th) {
         OseeLog.log(getClass(), Level.SEVERE, th);
      }
   }

   public void destroy() {
      dispose();
   }

   @Override
   public String getNamespace() {
      return namespace.toString();
   }

   @Override
   public void write(IDestination destination, ISource source, final DataStoreItem sample) {
      if (destination != null) {
         write((PubSubData) sample.getTheDataSample().getData(), ((PubSubSubscriber) destination).getParticipant());
      } else {
         try {
            throw new Exception(
               ((PubSubData) sample.getTheDataSample().getData()).getName() + " is sending the wrong way.");
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }

   @Override
   public void write(IDestination destination, ISource source, MessageData data) {
      if (destination != null && source != null) {
         try {
            throw new Exception(data.getName() + " is sending the wrong way.");
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      } else if (destination != null) {
         write((PubSubData) data, ((PubSubSubscriber) destination).getParticipant());
      } else {
         try {
            throw new Exception(data.getName() + " is sending the wrong way.");
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }

   public void write(BasicLogicalParticipant participant, MessageData data) {
      write((PubSubData) data, participant);
   }

   @Override
   public void write(IDestination destination, ISource source, Collection<MessageData> data) {
      for (MessageData msgData : data) {
         write(destination, source, msgData);
      }
   }

   @Override
   public boolean accept(String topic) {
      return true;
   }

}
