/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.ote.message.manager.internal;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.interfaces.IMessageScheduleChangeListener;
import org.eclipse.osee.ote.message.interfaces.IOSEEMessageReaderListener;
import org.eclipse.osee.ote.message.listener.MessageSystemListener;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.osee.ote.message.tool.SubscriptionKey;
import org.eclipse.osee.ote.remote.messages.MESSAGE_META_DATA_STAT;
import org.eclipse.ote.io.BasicDatagramChannelDataFactory;
import org.eclipse.ote.io.DatagramChannelData;

/**
 * Associates a {@link Message} with a {@link IOSEEMessageReaderListener} and handles transmitting the message data
 * upon a the method call
 * {@link org.eclipse.osee.ote.message.listener.IOSEEMessageListener#onDataAvailable(MessageData, DataType)}. When a
 * listener's {@link #onDataAvailable(MessageData, DataType)} method is invoked it will transmit the new data to all
 * registered clients
 * 
 * @author Ken J. Aguilar
 */
final class SubscriptionRecord implements IOSEEMessageReaderListener, IMessageScheduleChangeListener {

   private static int INT_PLUS_LONG_BYTE_SIZE = 12;

   private final MessageServiceImpl messageServiceImpl;
   private final Message msg;
   private final HashSet<ClientInfo> clients = new HashSet<ClientInfo>(10);
   private final List<SocketAddress> addresses = new ArrayList<SocketAddress>();
   private ByteBuffer buffer;
   private int msgStartPos;
   private ByteBuffer msgUpdatePart;
   private long updateCount = 0;
   private final SubscriptionKey key;
   private final TestEnvironmentInterface env;
   
   /**
    * Creates a new listener. A listener is a one to one mapping of a message to a list of client addresses
    */
   SubscriptionRecord(MessageServiceImpl messageServiceImpl, TestEnvironmentInterface env, final Message msg, final DataType type, final MessageMode mode, int id, final ClientInfo... clients) {
      this.messageServiceImpl = messageServiceImpl;
      this.msg = msg;
      this.env = env;
      this.key = new SubscriptionKey(id, type, mode, msg.getName());

      for (ClientInfo client : clients) {
         this.clients.add(client);
      }
      updateSocketAddresses();
      allocateBackingBuffer(msg.getMaxDataSize(type));

      MessageSystemListener systemListener = msg.getListener();
      if (!systemListener.containsListener(this)) {
         systemListener.addListener(this);
      }
      assert systemListener.containsListener(this);

      this.msg.addSchedulingChangeListener(this);
   }
   
   private void updateSocketAddresses(){
      addresses.clear();
      for (ClientInfo client : clients) {
         addresses.add(client.getIpAddress());this.clients.add(client);
      }
   }
   
   public SubscriptionKey getKey(){
      return key;
   }

   public Set<ClientInfo> getClients(){
      return clients;
   }
   
   public long getUpdateCount(){
      return updateCount;
   }
   
   public Message getMessage(){
      return msg;
   }
   
   private void allocateBackingBuffer(int maxDataSize) {
      final byte[] nameAsBytes = msg.getClass().getName().getBytes();
      buffer = ByteBuffer.allocateDirect(maxDataSize + nameAsBytes.length + 100);
      buffer.position(0);
      buffer.putInt(key.getId());
      msgUpdatePart = buffer.slice();
      msgStartPos = buffer.position();
   }

   @Override
   public synchronized String toString() {
      StringBuilder strBuilder = new StringBuilder(256);
      strBuilder.append(String.format("Message Watch Entry: mem type=%s, mode=%s, upd cnt=%d", key.getType(),
         key.getMode(), updateCount));

      strBuilder.append(" clients: ");
      for (ClientInfo addr : clients) {
         strBuilder.append(addr.getIpAddress().toString()).append(" ");
      }
      return strBuilder.toString();
   }


   /**
    * Adds a new client who will be notified when new updates occur
    * @param client 
    */
   public synchronized void addClient(final ClientInfo client) {
      clients.add(client);
      updateSocketAddresses();
   }

   public synchronized void removeClient(final ClientInfo client) {
      clients.remove(client);
      updateSocketAddresses();
   }

   public synchronized ClientInfo findClient(InetSocketAddress address) {
      for (ClientInfo clientInfo : clients) {
         if (clientInfo.getIpAddress().equals(address)) {
            return clientInfo;
         }
      }
      return null;
   }

   /**
    * removes this listener from the {@link Message} set of listeners. Thus no message updates will be sent to this
    * listener any longer
    */
   public synchronized void unregister() {
      msg.removeSchedulingChangeListener(this);
      msg.getListener().removeListener(this);
   }

   /**
    * checks to see if this listener is still registered for message updates
    * 
    * @return true if the listener is registered
    */
   public synchronized boolean isRegistered() {
      return msg.getListener().containsListener(this);
   }

   /**
    * This is a callback from the underlying messaging system that is invoked when data is received for the
    * particular message
    * 
    * @see IOSEEMessageReaderListener
    */
   @Override
   public synchronized void onDataAvailable(final MessageData data, final DataType type) {
      final byte[] msgData = data.toByteArray();
      int msgLength = data.getCurrentLength();
      // Truncating to the byte buffer size allocated minus the int and long put in the byte buffer below.
      if(msgLength > BasicDatagramChannelDataFactory.DatagramByteBufferSize - INT_PLUS_LONG_BYTE_SIZE){
         msgLength = BasicDatagramChannelDataFactory.DatagramByteBufferSize - INT_PLUS_LONG_BYTE_SIZE; 
      }
      /* do nothing if there is no clients registered for this type */
      try {
         if (key.getType() == type) {
            DatagramChannelData datagramChannelData = this.messageServiceImpl.getDatagramChannelData();
            datagramChannelData.setAddresses(addresses);
            datagramChannelData.getByteBuffer().clear();
            datagramChannelData.getByteBuffer().putInt(key.getId());
            datagramChannelData.getByteBuffer().putLong(env.getEnvTime());
            datagramChannelData.getByteBuffer().put(msgData, 0, msgLength);
            this.messageServiceImpl.submitSubscriptionData(datagramChannelData);
            updateCount++;
         }
      } catch (InterruptedException ex) {//ignore interrupted exception from data pool, but reset the flag
         Thread.currentThread().interrupt();
      } catch (Exception ex) {
         OseeLog.logf(
            MessageSystemTestEnvironment.class,
            Level.SEVERE,
            ex,
            "Exception during processing of update for %s: data length=%d, current length=%d, buf start=%d, buf cap=%d",
            msg.getMessageName(), msgData.length, data.getCurrentLength(), msgStartPos, msgUpdatePart.capacity());
      }
   }
   
   /**
    * do nothing stub required for interface implementation
    * 
    * @see IOSEEMessageReaderListener
    */
   @Override
   public void onInitListener() {
   }

   @SuppressWarnings("deprecation")
   @Override
   public void onRateChanged(Message message, double old, double rate) {
      if (clients.size() > 0) {
         MESSAGE_META_DATA_STAT messageDatastat = new MESSAGE_META_DATA_STAT();
         messageDatastat.IS_SCHEDULED.setValue(message.isScheduled());
         messageDatastat.RATE.setValue(rate);
         messageDatastat.MESSAGE.setValue(msg.getClass().getName());
         OteEventMessageUtil.postEvent(messageDatastat);
      }
      OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
         msg.getName() + " has a rate change to " + rate + " hz!!!!!");
   }

   @Override
   public void isScheduledChanged(boolean isScheduled) {
      if (clients.size() > 0) {
         MESSAGE_META_DATA_STAT messageDatastat = new MESSAGE_META_DATA_STAT();
         messageDatastat.IS_SCHEDULED.setValue(isScheduled);
         messageDatastat.RATE.setValue(msg.getRate());
         messageDatastat.MESSAGE.setValue(msg.getClass().getName());
         OteEventMessageUtil.postEvent(messageDatastat);
      }
      OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
         msg.getName() + " scheduling has changed to " + isScheduled);
   }

} /* end of MsgListener */