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

package org.eclipse.ote.message.manager.internal;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.OTEException;
import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.OTESessionManager;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.commands.RecordCommand;
import org.eclipse.osee.ote.message.commands.RecordCommand.MessageRecordDetails;
import org.eclipse.osee.ote.message.commands.SetElementValue;
import org.eclipse.osee.ote.message.commands.SubscribeToMessage;
import org.eclipse.osee.ote.message.commands.UnSubscribeToMessage;
import org.eclipse.osee.ote.message.commands.ZeroizeElement;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.osee.ote.message.tool.SubscriptionDetails;
import org.eclipse.osee.ote.message.tool.rec.IMessageEntryFactory;
import org.eclipse.osee.ote.message.tool.rec.MessageRecordConfig;
import org.eclipse.osee.ote.message.tool.rec.MessageRecorder;
import org.eclipse.osee.ote.remote.messages.RECORDING_COMPLETE;
import org.eclipse.ote.io.BasicDatagramChannelDataFactory;
import org.eclipse.ote.io.BasicDatagramChannelRunnable;
import org.eclipse.ote.io.DatagramChannelData;
import org.eclipse.ote.io.DatagramChannelDataPool;
import org.eclipse.ote.io.DatagramChannelWorker;

/**
 * Service that dispatches message updates to registered clients
 * 
 * @author Michael P. Masterson
 * @author Andrew M. Finkbeiner
 * @author Ken J. Aguilar
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MessageServiceImpl implements IRemoteMessageService {

   private IMessageManager messageManager;
   private final HashMap<String, Throwable> cancelledSubscriptions = new HashMap<String, Throwable>(40);
   private final HashMap<String, Map<DataType, EnumMap<MessageMode, SubscriptionRecord>>> messageMap = new HashMap<String, Map<DataType, EnumMap<MessageMode, SubscriptionRecord>>>(100);
   private final RECORDING_COMPLETE recordingCompleteMsg = new RECORDING_COMPLETE();

   private IMessageRequestor messageRequestor;
   private MessageRecorder recorder;
   private DatagramChannel recorderOutputChannel;
   private volatile boolean terminated = false;
   private final AtomicInteger idCounter = new AtomicInteger(0x0DEF0000);

   private InetSocketAddress xmitAddress;

   private OTESessionManager sessionManager;

   private TestEnvironmentInterface env;

   private DatagramChannelDataPool datagramChannelDataPool;

   private DatagramChannelWorker datagramChannelWorker;

   public void bindTestEnvironment(TestEnvironmentInterface env) {
      this.env = env;
   }

   /**
    * @param env Not used
    */
   public void unbindTestEnvironment(TestEnvironmentInterface env) {
      this.env = null;
   }

   /**
    * Constructs a new message manager service
    */
   public MessageServiceImpl() {

   }

   public void start() {
      try {
         datagramChannelDataPool = new DatagramChannelDataPool(new BasicDatagramChannelDataFactory(),
                                                               64);
         xmitAddress = new InetSocketAddress(InetAddress.getLocalHost(),
                                             PortUtil.getInstance().getValidPort());
         datagramChannelWorker = new DatagramChannelWorker("OTE Message Service Worker",
                                                           new BasicDatagramChannelRunnable(xmitAddress));
         datagramChannelWorker.start();
         messageRequestor = messageManager.createMessageRequestor(getClass().getName());
      } catch (IOException e) {
         OseeLog.log(getClass(), Level.SEVERE, e);
      }

   }

   public void stop() {
      terminateService();
   }

   public void bindMessageManager(IMessageManager messageManager) {
      this.messageManager = messageManager;
   }

   /**
    * @param messageManager Not used
    */
   public void unbindMessageManager(IMessageManager messageManager) {
      this.messageManager = null;
   }

   public void bindOTESessionManager(OTESessionManager sessionManager) {
      this.sessionManager = sessionManager;
   }

   /**
    * @param sessionManager Not used
    */
   public void unbindOTESessionManager(OTESessionManager sessionManager) {
      this.sessionManager = null;
   }
   
   
   @Override
   public synchronized void setupRecorder(IMessageEntryFactory factory) {
      if (recorder != null && recorder.isRecording()) {
         throw new IllegalStateException("a record session is currently running");
      }
      recorder = new MessageRecorder(factory);
   }

   /**
    * Attempts to set message data.
    * 
    * @see IRemoteMessageService#setElementValue(SetElementValue)
    */
   @Override
   public synchronized void setElementValue(SetElementValue cmd) throws OTEException {
      final String msgName = cmd.getMessage();
      try {
         final Class<?> msgWriterClass = env.getRuntimeManager().loadFromRuntimeLibraryLoader(msgName);

         /* check to see if an instance of a writer for the specified message exists */
         Message writer = messageRequestor.getMessageWriter(msgWriterClass);
         if (writer == null) {
            OseeLog.logf(MessageSystemTestEnvironment.class, Level.SEVERE,
                         "Attempting to set message data for %s even though no previous writer exists",
                         msgName);
            throw new OTEException("Could not find the class definition for " + msgName
                                   + " message writer");
         }

         List<Object> elementPath = cmd.getElement();
         if (elementPath != null) {
            final Element element = writer.getElement(elementPath, cmd.getMemType());
            OseeLog.log(MessageSystemTestEnvironment.class,
                        Level.INFO,
                        "Updating message data for element " + element.getElementName()
                                    + " on message " + writer.getName() + "(mem type = "
                                    + writer.getMemType() + ") to " + cmd.getValue());
            if (element instanceof DiscreteElement<?>) {
               ((DiscreteElement<?>) element).parseAndSet((ITestEnvironmentAccessor) env,
                                                          cmd.getValue());
            } else {
               OseeLog.log(MessageSystemTestEnvironment.class, Level.WARNING,
                           "not a DiscreteElement: " + element.getName());
            }
         }
         if (cmd.shouldSend()) {
            writer.send(cmd.getMemType());
         }
      } catch (Throwable t) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.WARNING,
                     "Exception occurred when attempting to set element value for message "
                                                                        + cmd.getMessage(),
                     t);
         throw new OTEException(String.format("failed to set %s of %s to %s", cmd.getElement(),
                                              cmd.getMessage(), cmd.getValue()),
                                t);
      }
   }

   @Override
   public synchronized void zeroizeElement(ZeroizeElement cmd) throws OTEException {
      final String msgName = cmd.getMessage();
      try {
         final Class<?> msgWriterClass = env.getRuntimeManager().loadFromRuntimeLibraryLoader(msgName);
         /* check to see if an instance of a writer for the specified message exists */
         Message writer = messageRequestor.getMessageWriter(msgWriterClass);
         if (writer == null) {
            OseeLog.logf(MessageSystemTestEnvironment.class, Level.SEVERE,
                         "Attempting to zeroize data for %s even though no previous writer exists",
                         msgName);
            throw new Exception("Could not find the class definition for " + msgName
                                + " message writer");
         }
         List<Object> elementPath = cmd.getElement();
         if (elementPath != null) {
            final Element element = writer.getElement(elementPath, cmd.getMemType());
            OseeLog.log(MessageSystemTestEnvironment.class,
                        Level.INFO,
                        "Zeroizing message data for element " + element.getElementName()
                                    + " on message " + writer.getName() + "(mem type = "
                                    + writer.getMemType());
            element.zeroize();
         } else {
            writer.zeroize();
         }
         writer.send(cmd.getMemType());

      } catch (Throwable t) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.WARNING,
                     "Exception occurred when attempting to set element value for message "
                                                                        + cmd.getMessage(),
                     t);
         throw new OTEException(String.format("failed to zeroize element %s on %s",
                                              cmd.getElement(), cmd.getMessage()),
                                t);
      }
   }

   /**
    * Handles subscription request from remote clients.
    * 
    * @return a {@link org.eclipse.osee.ote.message.MessageState} object detailing the current
    *         message state as it exists in the environment
    * @see IRemoteMessageService
    */
   @Override
   public synchronized SubscriptionDetails subscribeToMessage(
         final SubscribeToMessage cmd) throws OTEException {
      if (terminated) {
         throw new IllegalStateException("tool service has been terminated");
      }
      String userName = "N/A";
      final String name = cmd.getMessage();
      UUID key = null;
      Class<?> msgClass;
      try {

         msgClass = env.getRuntimeManager().loadFromRuntimeLibraryLoader(name);
      } catch (ClassNotFoundException e) {
         throw new OTEException(String.format("could find %s", name), e);
      }
      try {

         /* check to see if an instance of a writer for the specified message exists */
         Message msgInstance = cmd.getMode() == MessageMode.READER ? messageRequestor.getMessageReader(msgClass)
                                                                   : messageRequestor.getMessageWriter(msgClass);
         if (msgInstance == null) {
            throw new Exception("Could not instantiate reader for " + name);
         }
         final DataType type = cmd.getType();
         if (!((MessageSystemTestEnvironment) env).isPhysicalTypeAvailable(type)) {
            // the message can't exist in this environment return null;
            return null;
         }

         /* ask the client for an address when given the message name and mem type */
         key = cmd.getKey();
         final InetSocketAddress address = cmd.getAddress();
         IUserSession user = sessionManager.get(key);
         if (user != null) {
            userName = user.getUser().getName();
         }
         if (address == null) {
            throw new Exception("client callback for user " + userName
                                + " returned a null address when subscribing to " + name);
         }
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.INFO,
                      "Client %s at %s is subscribing to message %s: current mem=%s", userName,
                      address.toString(), name, type);

         Map<DataType, EnumMap<MessageMode, SubscriptionRecord>> memToModeMap = messageMap.get(name);
         if (memToModeMap == null) {
            memToModeMap = new HashMap<DataType, EnumMap<MessageMode, SubscriptionRecord>>();
            messageMap.put(name, memToModeMap);
         }
         EnumMap<MessageMode, SubscriptionRecord> modeMap = memToModeMap.get(type);
         if (modeMap == null) {
            modeMap = new EnumMap<MessageMode, SubscriptionRecord>(MessageMode.class);
            memToModeMap.put(type, modeMap);
         }
         SubscriptionRecord record = modeMap.get(cmd.getMode());
         ClientInfo client = new ClientInfo(address);
         /* see if we have a listener already created for the specified message */
         if (record != null) {
            /*
             * make sure the listener is still registered for message update. This should always be
             * the case
             */
            if (!record.isRegistered()) {
               OseeLog.logf(MessageSystemTestEnvironment.class, Level.SEVERE,
                            "Existing listener for %s (mem = %s) is not registered for updates",
                            name, type);
            }
            /* there is atleast one client already registered, add this one as well */
            record.addClient(client);
         } else {
            /* this is the first subscription request for this message */
            assert name.equals(msgInstance.getClass().getName());
            msgInstance.setMemTypeActive(type);
            record = new SubscriptionRecord(this, env, msgInstance, type, cmd.getMode(),
                                            idCounter.incrementAndGet(), client);
            modeMap.put(cmd.getMode(), record);
         }

         /*
          * return the message state back to the client. if both a reader and a writer exist then
          * always favor the writer
          */
         return new SubscriptionDetails(record.getKey(),
                                        msgInstance.getActiveDataSource(type).toByteArray(),
                                        msgInstance.getAvailableMemTypes());

      } catch (Throwable ex) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.WARNING,
                     "Exception occurred when subscribing to " + name, ex);
         if (key != null) {
            throw new OTEException("User " + userName + "Could not subscribe to message " + name,
                                   ex);
         } else {
            throw new OTEException("Could not subscribe to message " + name, ex);
         }
      }
   }

   @Override
   public synchronized void unsubscribeToMessage(
         final UnSubscribeToMessage cmd) throws OTEException {
      final String name = cmd.getMessage();
      final DataType type = cmd.getMemTypeOrdinal();

      final Map<DataType, EnumMap<MessageMode, SubscriptionRecord>> memToModeMap = messageMap.get(name);
      if (memToModeMap == null) {
         /* no listeners for this message so return */
         return;
      }
      final EnumMap<MessageMode, SubscriptionRecord> modeMap = memToModeMap.get(type);
      if (modeMap == null) {
         throw new OTEException(String.format("no subscription appears to exist for %s in %s mode",
                                              name, type.name()));
      }
      final SubscriptionRecord record = modeMap.get(cmd.getMode());

      if (record != null) {
         ClientInfo client = record.findClient(cmd.getAddress());
         /* remove the client address from the listener's client list */

         record.removeClient(client);

         OseeLog.logf(MessageSystemTestEnvironment.class, Level.INFO,
                      "client at %s is unsubscribing to the %s for %s(%s)",
                      client.getIpAddress().toString(), cmd.getMode(), name, type);
         /*
          * if the listener has no more clients then remove the listener and unregister the listener
          * for message updates.
          */
         if (record.getClients().isEmpty()) {
            OseeLog.logf(MessageSystemTestEnvironment.class, Level.INFO,
                         "No longer listening for updates for message %s. Final update count=%d",
                         name, record.getUpdateCount());
            record.unregister();
            record.getMessage().setMemTypeInactive(type);
            messageRequestor.remove(record.getMessage());
            modeMap.remove(cmd.getMode());
            memToModeMap.remove(type);
         }
      }
   }

   @Override
   public synchronized boolean startRecording(RecordCommand cmd) throws OTEException {
      if (terminated) {
         throw new IllegalStateException("tool service has been terminated");
      }
      String user;
      try {
         UUID key = cmd.getKey();
         IUserSession userSession = sessionManager.get(key);
         user = userSession.getUser().getName();
      } catch (Exception ex) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.WARNING,
                     "Problems retrieving the active user", ex);
         user = "N.A.";
      }
      try {
         LinkedList<MessageRecordConfig> msgsToRecord = new LinkedList<MessageRecordConfig>();
         for (MessageRecordDetails details : cmd.getMsgsToRecord()) {
            String name = details.getName();
            final Class<?> msgClass = env.getRuntimeManager().loadFromRuntimeLibraryLoader(name);
            /* check to see if an instance of a writer for the specified message exists */
            Message reader = messageRequestor.getMessageReader(msgClass);
            if (reader == null) {
               throw new RemoteException("Could not instantiate reader for " + name);
            }
            DataType type = details.getType();
            List<List<Object>> elementNames = details.getBodyElementNames();
            ArrayList<Element> elementsToRecord = new ArrayList<Element>(elementNames.size());
            for (List<Object> elementName : elementNames) {
               final Element element = reader.getElement(elementName, type);
               if (element == null) {

               } else {
                  if (!element.isNonMappingElement()) {
                     elementsToRecord.add(element);
                  }
               }
            }

            List<List<Object>> headerElementNames = details.getHeaderElementNames();
            ArrayList<Element> headerElementsToRecord = new ArrayList<Element>(headerElementNames.size());
            Element[] headerElements = reader.getActiveDataSource(type).getMsgHeader().getElements();
            if (headerElements != null) {
               for (List<Object> elementName : headerElementNames) {
                  Element element = reader.getElement(elementName);
                  if (element != null) {
                     headerElementsToRecord.add(element);
                  }
               }
            }
            MessageRecordConfig config = new MessageRecordConfig(reader, type,
                                                                 details.getHeaderDump(),
                                                                 headerElementsToRecord.toArray(new Element[headerElementsToRecord.size()]),
                                                                 details.getBodyDump(),
                                                                 elementsToRecord.toArray(new Element[elementsToRecord.size()]));
            msgsToRecord.add(config);
         }
         setupRecorderOutputChannel();
         recorderOutputChannel.connect(cmd.getDestAddress());
         recorder.startRecording(msgsToRecord, recorderOutputChannel);
         OseeLog.log(MessageSystemTestEnvironment.class,
                     Level.INFO, "Recording start by user " + user + ", sending recorder output to "
                                 + cmd.getDestAddress().toString());
         return true;
      } catch (Throwable ex) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
                     "Exception while starting message recording for user " + user, ex);
         throw new OTEException("failed to start recording", ex);
      }

   }

   /**
    * @throws IOException
    * @throws UnknownHostException
    * @throws SocketException
    */
   private void setupRecorderOutputChannel() throws IOException, UnknownHostException, SocketException {
      if (recorderOutputChannel != null)
         return;

      recorderOutputChannel = DatagramChannel.open();
      InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 0);
      try {
         recorderOutputChannel.socket().bind(address);
      } catch (BindException e) {
         throw new IOException("could not bind to address " + address.toString());
      }
   }

   @Override
   public synchronized InetSocketAddress getRecorderSocketAddress() throws OTEException {
      if (terminated) {
         throw new IllegalStateException("tool service has been terminated");
      }

      if (recorderOutputChannel == null) {
         try {
            setupRecorderOutputChannel();
         } catch (Exception ex) {
            throw new OTEException("Exception initializing recorder channel");
         }
      }

      if (!recorderOutputChannel.isOpen()) {
         throw new OTEException("Recorder output channel is closed");
      }
      final DatagramSocket socket = recorderOutputChannel.socket();

      return new InetSocketAddress(socket.getLocalAddress(), socket.getLocalPort());
   }

   @Override
   public synchronized InetSocketAddress getMsgUpdateSocketAddress() throws OTEException {
      if (terminated) {
         throw new IllegalStateException("tool service has been terminated");
      }
      return xmitAddress;
   }

   @Override
   public void stopRecording() throws OTEException {
      if (terminated) {
         throw new IllegalStateException("tool service has been terminated");
      }
      if (recorder.isRecording()) {
         try {
            recorder.stopRecording(false);
         } catch (IOException e) {
            OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
                        "Exception while stopping message recording", e);
            throw new OTEException("could not stop recorder", e);
         }
         finally {
            OteEventMessageUtil.postEvent(recordingCompleteMsg);
            ;
         }
         try {
            recorderOutputChannel.disconnect();
            recorderOutputChannel.close();
            recorderOutputChannel = null;
         } catch (IOException e) {
            throw new OTEException("could not disconnect recorder output channel", e);
         }
      }

   }

   /**
    * terminates the message tool service
    */
   @Override
   public void terminateService() {
      if (terminated) {
         return;
      }
      OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO, "terminate message tool service");
      try {
         for (Map<DataType, EnumMap<MessageMode, SubscriptionRecord>> memToModeMap : messageMap.values()) {
            for (EnumMap<MessageMode, SubscriptionRecord> modeMap : memToModeMap.values()) {
               for (SubscriptionRecord listener : modeMap.values()) {
                  /* unregister the listenr for message updates */
                  listener.unregister();
                  if (!listener.getClients().isEmpty()) {
                     OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
                                 "Message Watch clients still exist while terminating message watch service");
                  }
               }
               modeMap.clear();
            }
            memToModeMap.clear();
         }
         messageMap.clear();
         cancelledSubscriptions.clear();
         if (recorder != null && recorder.isRecording()) {
            try {
               recorder.stopRecording(false);
            } catch (IOException e) {
               OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE,
                           "failed to stop recording", e);
            }
         }

         try {
            if (recorderOutputChannel != null) {
               recorderOutputChannel.close();
            }
         } catch (IOException ex) {
            OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, ex.getMessage(), ex);
         }
      }
      finally {
         terminated = true;
      }
      OseeLog.log(MessageSystemTestEnvironment.class, Level.INFO,
                  "terminated message tool service");
   }

   @Override
   public Set<DataType> getAvailablePhysicalTypes() {
      final Set<DataType> available = new HashSet<DataType>(((MessageSystemTestEnvironment) env).getDataTypes());
      return available;
   }

   @Override
   public Map<String, Throwable> getCancelledSubscriptions() {
      return cancelledSubscriptions;
   }

   @Override
   public void reset() {
   }

   /**
    * Fetches a datagram channel data object out of the pool or creates a new one
    * @return
    * @throws InterruptedException
    */
   public DatagramChannelData getDatagramChannelData() throws InterruptedException {
      return datagramChannelDataPool.get();
   }

   /**
    * Submits the datagram data to the worker thread for sending
    * @param datagramChannelData 
    * @throws InterruptedException 
    */
   public void submitSubscriptionData(DatagramChannelData datagramChannelData) throws InterruptedException {
      this.datagramChannelWorker.submit(datagramChannelData);
   }
}
