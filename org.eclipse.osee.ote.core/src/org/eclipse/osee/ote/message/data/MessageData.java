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

package org.eclipse.osee.ote.message.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.CopyOnWriteNoIteratorList;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.log.Env;
import org.eclipse.osee.ote.message.IMessageDisposeListener;
import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.IMessageSendListener;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.osee.ote.messaging.dds.Data;
import org.eclipse.osee.ote.messaging.dds.DataSample;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.entity.DataReader;
import org.eclipse.osee.ote.messaging.dds.entity.DataWriter;
import org.eclipse.osee.ote.messaging.dds.listener.DataReaderListener;
import org.eclipse.osee.ote.messaging.dds.listener.DataWriterListener;
import org.eclipse.osee.ote.messaging.dds.service.Key;
import org.eclipse.osee.ote.messaging.dds.service.TypeSupport;
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
import org.eclipse.osee.ote.properties.OtePropertiesCore;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class MessageData implements DataReaderListener, DataWriterListener, Data, Key {

   private static long debugTimeout = OtePropertiesCore.timeDebugTimeout.getLongValue();
   private static boolean debugTime = OtePropertiesCore.timeDebug.getBooleanValue();

   protected DataWriter writer;
   protected DataReader reader;
   private final DataSample myDataSample;

   private final MemoryResource mem;
   private final String typeName;
   private final String name;
   private final CopyOnWriteNoIteratorList<Message> messages = new CopyOnWriteNoIteratorList<>(Message.class);
   private final CopyOnWriteNoIteratorList<IMessageSendListener> messageSendListeners = new CopyOnWriteNoIteratorList<>(IMessageSendListener.class);
   private final int defaultDataByteSize;
   private final DataType physicalIoType;
   private final DataType logicalIoType;

   private final boolean isEnabled = true;
   private long activityCount = 0;
   private long sentCount;
   private int currentLength;
   private boolean isScheduled = false;
   private long time = -1;
   private final Map<Class<?>, Pair<Message, MemoryResource>> overrideMessages = new HashMap<Class<?>, Pair<Message, MemoryResource>>();

   public MessageData(String typeName, String name, int dataByteSize, int offset,
         DataType physicalIoType) {
      this(typeName, name, dataByteSize, offset, physicalIoType, physicalIoType);
   }

   public MessageData(String typeName, String name, int dataByteSize, int offset,
         DataType physicalIoType, DataType logicalIoType) {
      this(typeName, name,
           new MemoryResource(new byte[dataByteSize], offset, dataByteSize - offset),
           physicalIoType, logicalIoType);
   }

   public MessageData(String typeName, String name, MemoryResource mem, DataType physicalIoType,
         DataType logicalIoType) {
      this.mem = mem;
      myDataSample = new DataSample(this);
      this.typeName = typeName;
      this.name = name;
      this.defaultDataByteSize = mem.getLength() + mem.getOffset();
      this.currentLength = defaultDataByteSize;
      this.physicalIoType = physicalIoType;
      this.logicalIoType = logicalIoType;
      GCHelper.getGCHelper().addRefWatch(this);
   }

   public MessageData(String name, int dataByteSize, int offset, DataType physicalIoType) {
      this(name, name, dataByteSize, offset, physicalIoType);
   }

   public abstract IMessageHeader getMsgHeader();

   /**
    * Represents the physical medium this data goes out on. Multiple Logical IO Types may flow into
    * a single Physical IO Type. For instance you may have two ethernet boxes and have their own
    * unique logical protocols but they still go out over physical ethernet.
    * 
    * @return The physical IO type for this message data
    */
   public DataType getPhysicalIoType() {
      return physicalIoType;
   }

   /**
    * Logical IO type represents the lowest level protocol going out over a physical medium.
    * Multiple Logical IO Types may flow into a single Physical IO Type. For instance you may have
    * two ethernet boxes and have their own unique logical protocols but they still go out over
    * physical ethernet.
    * 
    * @return The Logical IO Type for this message data
    */
   public DataType getLogicalIoType() {
      return logicalIoType;
   }

   /**
    * Returns the number of byte words in the payload of this message.
    * 
    * @return the number of bytes in the message payload
    */
   public int getPayloadSize() {
      return currentLength;
   }

   public String getName() {
      return name;
   }

   /**
    * adds a {@link Message} who are mapped to this data object
    * @param message Message to add
    */
   public void addMessage(Message message) {
      if (!messages.contains(message)) {
         messages.add(message);
         message.addPreMessageDisposeListener(disposeListener);
      }
   }

   /**
    * returns a list of the message that this data is a source for. <BR>
    * 
    * @return a collection of messages
    */
   public Collection<Message> getMessages() {
      return messages.fillCollection(new ArrayList<Message>());
      //      return new ArrayList<Message>(messages);
   }

   /**
    * @return Returns the activityCount.
    */
   public long getActivityCount() {
      return activityCount;
   }

   /**
    * @param activityCount The activityCount to set.
    */
   public void setActivityCount(long activityCount) {
      this.activityCount = activityCount;
   }

   public void incrementActivityCount() {
      activityCount++;
   }

   public void incrementSentCount() {
      sentCount++;
   }

   public long getSentCount() {
      return sentCount;
   }

   public boolean isEnabled() {
      return isEnabled;
   }

   public abstract void visit(IMessageDataVisitor visitor);

   public void dispose() {

      try{
         Message[] msgs = messages.get();
         for (int i = 0; i < msgs.length; i++){
            Message local = msgs[i];
            if(local != null){
               local.removePreMessageDisposeListener(disposeListener);
            }
         }
      } catch (Throwable th){
         OseeLog.log(getClass(), Level.SEVERE, "failed to remove message dispose listener.", th);
      }
      messages.clear();
      if (writer != null) {
         writer.getPublisher().deleteDataWriter(writer);
         writer.dispose(this, null);
         writer = null;
      } else if (reader != null && reader.getSubscriber() != null) {
         reader.getSubscriber().deleteDataReader(reader);
         reader.dispose();
         reader = null;
      }
      disposeListener = null;
   }

   public void copyData(int destOffset, byte[] data, int srcOffset, int length) {
      setCurrentLength(length + destOffset);
      mem.copyData(destOffset, data, srcOffset, length);
   }

   public void copyData(int destOffset, ByteBuffer data, int length) throws MessageSystemException {
      try {
         setCurrentLength(destOffset + length);
         mem.copyData(destOffset, data, length);
      } catch (MessageSystemException ex) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.INFO, ex,
               "increasing backing store for %s to %d. prev length: %d, recv cnt: %d", getName(), destOffset + length,
               mem.getData().length, this.activityCount);
         setNewBackingBuffer(data, destOffset, length);
      }
   }

   public void copyData(ByteBuffer data) {
      copyData(0, data, data.remaining());
   }

   /**
    * Notifies all {@link Message}s that have this registered as a data source of the update
    * @throws MessageSystemException 
    */
   public void notifyListeners() throws MessageSystemException {
      final DataType physicalIoType = getPhysicalIoType();
      Message[] ref = messages.get();
      for (int i = 0; i < ref.length; i++) {
         Message message = ref[i];
         try {
            if (!message.isDestroyed()) {
               message.notifyListeners(this, physicalIoType);
            }
         } catch (Throwable t) {
            final String msg =
                  String.format("Problem during listener notification for message %s. Data=%s, physicalIoType=%s",
                        message.getName(), this.getName(), this.getPhysicalIoType());
            OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, msg, t);
         }
      }
   }

   /**
    * @return the currentLength
    */
   public int getCurrentLength() {
      return currentLength;
   }

   /**
    * @param currentLength the currentLength to set
    */
   public void setCurrentLength(int currentLength) {
      this.currentLength = currentLength;
   }

   /**
    * Override this method if you need to set some default data in the backing buffer.
    * @param data 
    */
   public void setNewBackingBuffer(byte[] data) {
      setCurrentLength(data.length);
      this.mem.setData(data);
      if (this.getMsgHeader() != null) {
         initializeDefaultHeaderValues();
      } else {
         // System.out.println("what??-- bad HeaderData");
      }

   }

   public void setNewBackingBuffer(ByteBuffer buffer) {
      byte[] data = new byte[buffer.remaining()];
      buffer.get(data);
      this.mem.setData(data);
      setCurrentLength(data.length);
      if (this.getMsgHeader() != null) {
         initializeDefaultHeaderValues();
      } else {
         // System.out.println("what??-- bad HeaderData");
      }
   }

   public void setNewBackingBuffer(ByteBuffer buffer, int offset, int length) {
      byte[] data = new byte[offset + length];
      buffer.get(data, offset, length);
      this.mem.setData(data);
      setCurrentLength(data.length);
      if (this.getMsgHeader() != null) {
         initializeDefaultHeaderValues();
      } else {
         // System.out.println("what??-- bad HeaderData");
      }

   }

   public abstract void initializeDefaultHeaderValues();

   /**
    * @return the mem
    */
   public MemoryResource getMem() {
      return mem;
   }

   public int getDefaultDataByteSize() {
      return defaultDataByteSize;
   }

   @Override
   public synchronized void onDataAvailable(DataReader theReader) {
      // System.out.println(String.format("data available %s %s", this.getName(),
      // this.getNamespace()));
      if (isEnabled()) {
         ReturnCode val = theReader.takeNextSample(myDataSample);
         if (val == ReturnCode.OK) {
            incrementActivityCount();
            notifyListeners();
         } else {
            Env.getInstance().severe(val.getDescription());
         }
      }
   }

   @Override
   public void onLivelinessChanged(DataReader theReader, LivelinessChangedStatus status) {
   }

   @Override
   public void onRequestedDeadlineMissed(DataReader theReader, RequestedDeadlineMissedStatus status) {
   }

   @Override
   public void onRequestedIncompatibleQos(DataReader theReader, RequestedIncompatibleQosStatus status) {
   }

   @Override
   public void onSampleLost(DataReader theReader, SampleLostStatus status) {
   }

   @Override
   public void onSampleRejected(DataReader theReader, SampleRejectedStatus status) {
   }

   @Override
   public void onSubscriptionMatch(DataReader theReader, SubscriptionMatchStatus status) {
   }

   @Override
   public synchronized void onDataSentToMiddleware(DataWriter theWriter) {
      // header.setSequenceNumber(header.getSequenceNumber() + 1);
      notifyListeners();
   }

   @Override
   public void onLivelinessLost(DataWriter theWriter, LivelinessLostStatus status) {
   }

   @Override
   public void onOfferedDeadlineMissed(DataWriter theWriter, OfferedDeadlineMissedStatus status) {
   }

   @Override
   public void onOfferedIncompatibleQos(DataWriter theWriter, OfferedIncompatibleQosStatus status) {
   }

   @Override
   public void onPublicationMatch(DataWriter theWriter, PublicationMatchStatus status) {
   }

   @Override
   public Object getKeyValue() {
      return null;
   }

   @Override
   public void setFromByteArray(byte[] input) {
      try {
         copyData(0, input, 0, input.length);
      } catch (MessageSystemException ex) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.WARNING,

               "Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(),
               this.mem.getData().length, input.length);
         setNewBackingBuffer(input);
      }
   }

   @Override
   public void setFromByteBuffer(ByteBuffer buffer) {
      try {
         copyData(buffer);
      } catch (Exception e) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.SEVERE,
               "Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(),
               this.mem.getData().length, buffer.limit());
         setNewBackingBuffer(buffer);
      }
   }

   @Override
   public ByteBuffer toByteBuffer() {
      return mem.getAsBuffer();
   }

   public void setFromByteArray(byte[] input, int length) {
      try {
         copyData(0, input, 0, length);
      } catch (MessageSystemException ex) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.SEVERE,
               "Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(),
               this.mem.getData().length, length);
         setNewBackingBuffer(input);
      }
   }

   public void setFromByteArray(int destOffset, byte[] input, int srcOffset, int length) {
      try {
         copyData(destOffset, input, srcOffset, length);
      } catch (MessageSystemException ex) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.SEVERE,
               "Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(),
               this.mem.getData().length, length);
         setNewBackingBuffer(input);
      }
   }

   public void setFromByteArray(ByteBuffer input, int length) {
      try {
         copyData(0, input, length);
      } catch (MessageSystemException ex) {
         OseeLog.logf(MessageSystemTestEnvironment.class, Level.SEVERE,
               "Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(),
               this.mem.getData().length, length);
         setNewBackingBuffer(input);
      }
   }

   @Override
   public byte[] toByteArray() {
      return mem.getData();
   }

   public void setReader(DataReader reader) {
      this.reader = reader;
   }

   public void setWriter(DataWriter writer) {
      this.writer = writer;
   }

   public void send() throws MessageSystemException {
      performOverride();
      if (writer == null) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, getName() + " - the writer is null");
      } else if (shouldSendData()) {
         try {
            notifyPreSendListeners();
            long start = 0, elapsed;
            if(debugTime){
               start = System.nanoTime();
            }
            getMem().setDataHasChanged(false);
            writer.write(null, null, this, null);
            incrementSentCount();
            if(debugTime){
               elapsed = System.nanoTime() - start;
               if(elapsed > debugTimeout){
                  Locale.setDefault(Locale.US);
                  System.out.printf("%s SLOW IOSEND %,d\n", getName(), elapsed);
               }
            }
            notifyPostSendListeners();
         } catch (Throwable ex) {
            throw new MessageSystemException("Could not send message data " + getName(), Level.SEVERE, ex);
         }
      }
   }

   protected void sendTo(IDestination destination, ISource source) throws MessageSystemException {
      performOverride();

      if (writer == null) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.WARNING, getName() + " - the writer is null");
      } else if (shouldSendData()) {
         try {
            notifyPreSendListeners();
            // this.initializeDefaultHeaderValues();
            getMem().setDataHasChanged(false);
            writer.write(destination, source, this, null);
            incrementSentCount();
            notifyPostSendListeners();
         } catch (Throwable ex) {
            throw new MessageSystemException("Could not send message data " + getName(), Level.SEVERE, ex);
         }
      }
   }

   /**
    * Override this method if you want to specialize the send criteria in a data source. For example, if you only want
    * to send data if the data has changed.
    */
   protected boolean shouldSendData() {
      return true;
   }

   public TypeSupport getTypeSupport() {
      return new DDSTypeSupport(this, getName(), getName(), getPayloadSize());
   }

   public String getTopicName() {
      return getName();
   }

   public String getTypeName() {
      return typeName;
   }

   @Override
   public boolean isSameInstance(byte[] data1, byte[] data2) {
      return true;
   }

   public Namespace getNamespace() {
      if (isWriter()) {
         return new Namespace(writer.getTopic().getNamespace());
      } else {
         return new Namespace(reader.getTopicDescription().getNamespace());
      }
   }

   /*
    * each type that extends DDSData needs to have it's own namespace.... we need to go through each DDSData child and
    * determine all of it's possible namespaces
    */
   public boolean isWriter() {
      if (writer != null && reader == null) {
         return true;
      } else if (writer == null && reader != null) {
         return false;
      } else {
         throw new MessageSystemException(
               "This is an illegal message it has neither a reader or a writer [" + this.getName() + "].", Level.SEVERE);
      }
   }

   private IMessageDisposeListener disposeListener = new IMessageDisposeListener() {

      @Override
      public void onPreDispose(Message message) {
         messages.remove(message);
      }

      @Override
      public void onPostDispose(Message message) {
      }

   };

   @Override
   public void copyFrom(Data data) {
      ByteBuffer buffer = data.toByteBuffer();
      copyData(data.getOffset(), buffer, buffer.remaining());
   }

   @Override
   public String toString() {
      return getClass().getName() + ": name=" + getName();
   }

   @Override
   public int getOffset() {
      return 0;
   }

   /**
    * @return the isScheduled
    */
   public boolean isScheduled() {
      return isScheduled;
   }

   /**
    * @param isScheduled the isScheduled to set
    */
   public void setScheduled(boolean isScheduled) {
      this.isScheduled = isScheduled;
   }

   private void notifyPostSendListeners() {
      try {
         long start = 0, elapsed;
         IMessageSendListener[] listeners = messageSendListeners.get();
         for (int i = 0; i < listeners.length; i++) {
            IMessageSendListener listener = listeners[i];
            if(debugTime){
               start = System.nanoTime();
            }
            listener.onPostSend(this);
            if(debugTime){
               elapsed = System.nanoTime() - start;
               if(elapsed > debugTimeout){
                  Locale.setDefault(Locale.US);
                  System.out.printf("%s %s SLOW POST SEND %,d\n", getName(), listener.getClass().getName(), elapsed);
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Message.class, Level.SEVERE, ex);
      }
   }

   private void notifyPreSendListeners() {
      try {
         long start = 0, elapsed;
         IMessageSendListener[] listeners = messageSendListeners.get();
         for (int i = 0; i < listeners.length; i++) {
            IMessageSendListener listener = listeners[i];
            if(debugTime){
               start = System.nanoTime();
            }
            listener.onPreSend(this);
            if(debugTime){
               elapsed = System.nanoTime() - start;
               if(elapsed > debugTimeout){
                  Locale.setDefault(Locale.US);
                  System.out.printf("%s %s SLOW PRE SEND %,d\n", getName(), listener.getClass().getName(), elapsed);
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Message.class, Level.SEVERE, ex);
      }
   }

   public void addSendListener(IMessageSendListener listener) {
      messageSendListeners.add(listener);
   }

   public void removeSendListener(IMessageSendListener listener) {
      messageSendListeners.remove(listener);
   }

   public boolean containsSendListener(IMessageSendListener listener) {
      return messageSendListeners.contains(listener);
   }

   public boolean isMessageCollectionNotEmpty() {
      return messages.get().length > 0;
   }

   public void zeroize() {
      final byte[] data = toByteArray();
      Arrays.fill(data, getMsgHeader().getHeaderSize(), data.length, (byte) 0);
   }

   /**
    * A time value associated with this message.
    * The time value will have different meanings or may not be used depending on the context and usage.
    * @return The time in milliseconds
    */
   public long getTime() {
      return time;
   }

   public void setTime(long time) {
      this.time = time;
   }
   
   protected void performOverride() {
      if (getMem().isDataChanged()) {
         synchronized(overrideMessages) {
            for (Pair<Message, MemoryResource> override : overrideMessages.values()) {
               byte[] overrideMsgData = override.getFirst().getData();
               byte[] overrideMask = override.getSecond().getData();
               if (null != overrideMsgData && null != overrideMask) {
                  byte[] targetMsgData = getMem().getData();
                  int targetMsgHeaderSize = getMem().getOffset();
                  int minLength = Math.min(targetMsgData.length - targetMsgHeaderSize, overrideMsgData.length - targetMsgHeaderSize);
                  minLength = Math.min(minLength, overrideMask.length);
                  int targetIndex;
                  int overrideIndex;
                  int overrideMsgHeaderSize = override.getFirst().getHeaderSize();
                  for (int byteIndex=0; byteIndex < minLength; byteIndex++) {
                     if (overrideMask[byteIndex] != 0x0) {
                        targetIndex = byteIndex+targetMsgHeaderSize;
                        overrideIndex = byteIndex + overrideMsgHeaderSize;
                        overrideMsgData[overrideIndex] &= overrideMask[byteIndex]; // zeroize non override regions
                        targetMsgData[targetIndex] &= ~overrideMask[byteIndex]; // zeroize regions to override
                        targetMsgData[targetIndex] = (byte) (targetMsgData[targetIndex] | overrideMsgData[overrideIndex]);
                     }
                  }
               }
            }
         }
      }
   }

  public MemoryResource getOverrideResource(Class<? extends Message> clazz) {
     MemoryResource memResource = null;
     Pair<Message, MemoryResource> override = getOverride(clazz);
     if (override != null) {
        memResource = override.getSecond();
     }
     return memResource;
  }

  public Message getOverrideMessage(Class<? extends Message> clazz) {
     Message message = null;
     Pair<Message, MemoryResource> override = getOverride(clazz);
     if (override != null) {
        message = override.getFirst();
     }
     return message;

  }

  private Pair<Message, MemoryResource> getOverride(Class<? extends Message> clazz) {
     Pair<Message, MemoryResource> override = overrideMessages.get(clazz);
     if (override == null) {
        synchronized(overrideMessages) {
           try {
              Message msg = clazz.newInstance();
              byte[] mask = new byte[msg.getMaxDataSize()];
              MemoryResource memoryResource = new MemoryResource(mask, 0, mask.length);
              override = new Pair<Message, MemoryResource>(msg, memoryResource);
              overrideMessages.put(clazz, override);
           } catch (Throwable th) {
              th.printStackTrace();
           }
        }
     }
     return override;
  }
  

  /**
   * Remove any override messages for which there are no more overridden elements 
   */
  public void cleanupOverrides() {
     synchronized(overrideMessages) {
        Set<Class<?>> keySet = overrideMessages.keySet();
        for (Class<?> clazz : keySet) {
           byte[] overrideMask = overrideMessages.get(clazz).getSecond().getData();
           boolean overrides = false;
           for (byte checkByte : overrideMask) {
              if (checkByte != (byte) 0x0) {
                 overrides = true;
                 break;
              }
           }
           if (!overrides) {
              overrideMessages.remove(clazz);
           }
        }
     }
  }
  
  /**
   * Remove all overrides
   */
  public void clearOverrides() {
     synchronized(overrideMessages) {
        overrideMessages.clear();
     }
  }

}
