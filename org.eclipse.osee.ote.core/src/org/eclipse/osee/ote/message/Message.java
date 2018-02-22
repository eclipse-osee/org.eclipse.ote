/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.persistence.XmlizableStream;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.message.condition.ICondition;
import org.eclipse.osee.ote.message.condition.TransmissionCountCondition;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.MsgWaitResult;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.IMessageScheduleChangeListener;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;
import org.eclipse.osee.ote.message.listener.MessageSystemListener;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.w3c.dom.Document;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class Message<S extends ITestEnvironmentMessageSystemAccessor, T extends MessageData, U extends Message<S, T, U>> implements Xmlizable, XmlizableStream {
   private static volatile AtomicLong constructed = new AtomicLong(0);
   private static volatile AtomicLong finalized = new AtomicLong(0);
   private final LinkedHashMap<String, Element> elementMap;
   @JsonProperty
   private final String name;
   private final MessageSystemListener listenerHandler;
   protected final ArrayList<IMessageScheduleChangeListener> schedulingChangeListeners =
      new ArrayList<IMessageScheduleChangeListener>(10);
   private boolean destroyed = false;

   private DataType currentMemType;
   private final Map<DataType, ArrayList<U>> memTypeToMessageMap = new HashMap<>();
   private final Map<DataType, ArrayList<T>> memToDataMap = new HashMap<>();
   private final int phase;
   protected double rate;
   protected final double defaultRate;
   private final boolean isScheduledFromStart;
   private boolean regularUnscheduleCalled = false;
   private boolean isTurnedOff = false;

   @SuppressWarnings("rawtypes")
   private IMessageRequestor messageRequestor = null;
   private static final double doubleTolerance = 0.000001;
   private final Set<DataType> memTypeActive = new HashSet<>();

   private T defaultMessageData;

   private final List<IMemSourceChangeListener> preMemSourceChangeListeners = new CopyOnWriteArrayList<>();
   private final List<IMemSourceChangeListener> postMemSourceChangeListeners =
      new CopyOnWriteArrayList<IMemSourceChangeListener>();
   private final List<IMessageDisposeListener> preMessageDisposeListeners = new CopyOnWriteArrayList<>();
   private final List<IMessageDisposeListener> postMessageDisposeListeners = new CopyOnWriteArrayList<>();

   private final int defaultByteSize;
   private final int defaultOffset;

   protected final MessageSystemListener removableListenerHandler;

   public Message(String name, int defaultByteSize, int defaultOffset, boolean isScheduled, int phase, double rate) {
      constructed.incrementAndGet();
      listenerHandler = new MessageSystemListener(this);
      this.name = name;
      this.defaultByteSize = defaultByteSize;
      this.defaultOffset = defaultOffset;
      elementMap = new LinkedHashMap<>(20);
      this.phase = phase;
      this.rate = rate;
      this.defaultRate = rate;
      this.isScheduledFromStart = isScheduled;
      GCHelper.getGCHelper().addRefWatch(this);
      this.removableListenerHandler = new MessageSystemListener(this);
   }

   /**
    * Attemps to remove the specified listener from the list of REMOVABLE listeners. This will NOT remove any listener
    * added using the addListener() call, only those added using the addRemovableListener() call will be removed.
    * 
    * @param listener The removable listener to remove
    */
   public void removeRemovableListener(IOSEEMessageListener listener) {
      removableListenerHandler.removeListener(listener);
   }

   /**
    * Adds listener to the list of listeners removed at the end of every script.
    * 
    * @param listener the removable listern to add.
    */
   public void addRemovableListener(IOSEEMessageListener listener) {
      removableListenerHandler.addListener(listener);
   }

   /**
    * Removes all the listeners from the RemovableListenerHandler. This method is meant to be called upon script
    * completion but can be used by anyone. Other listeners can be removed using the traditional removeListener call.
    */
   public void clearRemovableListeners() {
      this.removableListenerHandler.clearListeners();

   }

   public void destroy() {

      notifyPreDestroyListeners();
      destroyed = true;
      defaultMessageData.dispose();

      memToDataMap.clear();
      memTypeToMessageMap.clear();
      listenerHandler.dispose();

      notifyPostDestroyListeners();
      schedulingChangeListeners.clear();
      postMessageDisposeListeners.clear();
      preMessageDisposeListeners.clear();
      postMemSourceChangeListeners.clear();
      preMemSourceChangeListeners.clear();
      elementMap.clear();

      if (messageRequestor != null) {
         messageRequestor.dispose();
      }
      removableListenerHandler.dispose();
   }

   private void notifyPostDestroyListeners() {
      for (IMessageDisposeListener listener : postMessageDisposeListeners) {
         listener.onPostDispose(this);
      }
   }

   private void notifyPreDestroyListeners() {
      for (IMessageDisposeListener listener : preMessageDisposeListeners) {
         try {
            listener.onPreDispose(this);
         } catch (Exception e) {
            OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, "exception during listener notification", e);
         }
      }
   }

   public void setData(byte[] data) {
      checkState();
      if (data == null) {
         throw new IllegalArgumentException("data array cannot be null");
      }
      for (MessageData msgdata : memToDataMap.get(currentMemType)) {
         msgdata.setFromByteArray(data);
      }
   }

   public void setData(ByteBuffer data, int length) {
      checkState();
      if (data == null) {
         throw new IllegalArgumentException("data array cannot be null");
      }
      for (MessageData msgdata : memToDataMap.get(currentMemType)) {
         msgdata.setFromByteArray(data, length);
      }
   }

   public void setData(byte[] data, int length) {
      checkState();
      if (data == null) {
         throw new IllegalArgumentException("data array cannot be null");
      }
      for (MessageData msgdata : memToDataMap.get(currentMemType)) {
         msgdata.setFromByteArray(data, length);
      }
   }

   public void setBackingBuffer(byte[] data) {
      checkState();
      if (data == null) {
         throw new IllegalArgumentException("data array cannot be null");
      }
      for (MessageData msgdata : memToDataMap.get(currentMemType)) {
         msgdata.setNewBackingBuffer(data);
      }
   }

   public byte[] getData() {
      checkState();
      return getActiveDataSource().toByteArray();
   }

   public T getMemoryResource() {
      checkState();
      return memToDataMap.get(currentMemType).get(0);
   }

   /**
    * Returns the number of byte words in the payload of this message.
    * 
    * @return number of bytes in the message payload
    */
   public int getPayloadSize() {
      checkState();
      return memToDataMap.get(currentMemType).get(0).getPayloadSize();
   }

   public int getPayloadSize(DataType type) {
      checkState();
      return memToDataMap.get(type).get(0).getPayloadSize();
   }

   /**
    * Returns the number of byte words in the header of this message.
    * 
    * @return the number of bytes in the header
    */
   public int getHeaderSize() {
      checkState();
      final IMessageHeader hdr = memToDataMap.get(currentMemType).get(0).getMsgHeader();
      if (hdr != null) {
         return hdr.getHeaderSize();
      }
      return 0;
   }

   public int getHeaderSize(DataType type) {
      checkState();
      return memToDataMap.get(type).get(0).getMsgHeader().getHeaderSize();
   }

   /*
    * protected static final ThreadLocal current = new ThreadLocal() { protected Object initialValue() { return new
    * MemMessageHolder(); } };
    */
   public void send() throws MessageSystemException {
      checkState();
      if (!isTurnedOff) {
         ArrayList<T> dataList = memToDataMap.get(currentMemType);
         if (dataList != null) {
            int listSize = dataList.size();
            for (int i = 0; i < listSize; i++) {
               dataList.get(i).send();
            }
         } else {
            throw new MessageSystemException(
               "Message: " + name + " does not have the  physical type " + currentMemType + " available for this environment!!!!!",
               Level.SEVERE);
         }
      }
   }

   public void addSendListener(IMessageSendListener listener) {
      getActiveDataSource().addSendListener(listener);
   }

   public void removeSendListener(IMessageSendListener listener) {
      getActiveDataSource().removeSendListener(listener);
   }

   public boolean containsSendListener(IMessageSendListener listener) {
      return getActiveDataSource().containsSendListener(listener);
   }

   public void send(DataType type) throws MessageSystemException {
      checkState();
      if (!isTurnedOff) {
         Collection<T> dataList = memToDataMap.get(type);
         if (dataList != null) {

            for (T data : dataList) {
               data.send();
            }

         } else {
            throw new MessageSystemException(
               "Message: " + name + " does not have a physical type available for this environment!!!!!", Level.SEVERE);
         }
      } else {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.WARNING,
            this.getMessageName() + " has attempted a send(), but is currently turned off.");
      }
   }

   //	we may not really need this guy, in fact I think we don't
   //	protected void takeNextSample() {
   //	for (T item : memToDataMap.get(currentMemType)) {
   //	item.takeNextSample();
   //	}
   //	}

   public boolean setMemSource(S accessor, DataType type) {
      return setMemSource(type);
   }

   /**
    * Associates Messages to MemTypes based on the memType's physical availability
    */
   //	public abstract void associateMessages(S accessor);
   /**
    * Changes the element references for this message to a corresponding message with the given MemType. The messages
    * defined for this memType must have been provided by the associateMessages function to be seen.
    * 
    * @param memType the possibly new physical mem type.
    */
   public void switchElementAssociation(DataType memType) {
      checkState();
      switchElementAssociation(getMessageTypeAssociation(memType));
   }

   //   public abstract void switchElementAssociation(Collection<U> messages);

   public void addMessageTypeAssociation(DataType memType, U messageToBeAdded) {
      checkState();
      ArrayList<U> list;
      if (!memTypeToMessageMap.containsKey(memType)) {
         list = new ArrayList<>(4);
         memTypeToMessageMap.put(memType, list);
      } else {
         list = memTypeToMessageMap.get(memType);
      }
      list.add(messageToBeAdded);

      //		addMessageDataSource(messageToBeAdded.defaultMessageData);
   }

   protected Collection<U> getMessageTypeAssociation(DataType type) {
      final ArrayList<U> list = memTypeToMessageMap.get(type);
      if (list != null) {
         return Collections.unmodifiableCollection(list);
      } else {
         return new ArrayList<U>();
      }
   }

   public void addMessageDataSource(T... dataList) {
      checkState();
      for (T data : dataList) {
         addMessageDataSource(data);
      }
   }

   public void addMessageDataSource(Collection<T> dataList) {
      for (T data : dataList) {
         addMessageDataSource(data);
      }
   }

   protected void addMessageDataSource(T data) {
      final DataType type = data.getType();
      final ArrayList<T> list;
      if (!memToDataMap.containsKey(type)) {
         list = new ArrayList<>();
         memToDataMap.put(type, list);
      } else {
         list = memToDataMap.get(type);
      }
      list.add(data);
      data.addMessage(this);
   }

   public Collection<T> getMemSource(DataType type) {
      checkState();
      final ArrayList<T> list = memToDataMap.get(type);
      if (list != null) {
         return Collections.unmodifiableCollection(list);
      } else {
         return new ArrayList<T>();
      }
   }

   public boolean getMemSource(DataType type, Collection<T> listToAddto) {
      checkState();
      final ArrayList<T> list = memToDataMap.get(type);
      if (list != null) {
         return listToAddto.addAll(list);
      }
      return false;
   }

   public DataType getMemType() {
      return currentMemType;
   }

   public void addElement(Element element) {
      checkState();
      elementMap.put(element.getName(), element);
   }

   /**
    * Gets a list of all the message's data elements.
    * <br>
    * This returns ALL the elements, which may not be mapped to the
    * active data type and/or may be non-mapping elements.
    * 
    * Use {@link #getElements(DataType)} to get mapped elements
    * 
    * @return a collection of {@link Element}s
    */
   public Collection<Element> getElements() {
      checkState();
      return elementMap.values();
   }

   public void getAllElements(Collection<Element> elements) {
      checkState();
      IMessageHeader header = getActiveDataSource().getMsgHeader();
      if (header != null) {
         Collections.addAll(elements, header.getElements());
      }
      elements.addAll(elementMap.values());

   }

   /**
    * @return a collection of mapped {@link Element}s for the specified DataType
    */
   public Collection<Element> getElements(DataType type) {
      checkState();
      LinkedList<Element> list = new LinkedList<>();
      for (Element element : elementMap.values()) {
         Element e = element.switchMessages(getMessageTypeAssociation(type));
         if (!e.isNonMappingElement()) {
            list.add(e);
         }
      }
      return list;
   }

   /**
    * @return true if the Message contains an element with the given name, false otherwise
    */
   public boolean hasElement(String elementName) {
      checkState();
      return elementMap.containsKey(elementName);
   }

   /**
    * @return HashMap<String, Element>
    */
   public HashMap<String, Element> getElementMap() {
      checkState();
      return elementMap;
   }

   /**
    * @param elementName the name of the element as defined in the message ( All caps ).
    * @return the element associated with the given name
    * @throws IllegalArgumentException if an element doesn't exist with given name. Use {@link #hasElement(String)} with
    * any use of this function.
    */
   public Element getElement(String elementName) {
      return getElement(elementName, currentMemType);
   }

   public <E extends Element> E getElement(String elementName, Class<E> clazz) {
      checkState();
      return clazz.cast(getElement(elementName, currentMemType));
   }

   public boolean hasElement(List<Object> elementPath) {
      return getElement(elementPath) != null;
   }

   public Element getElement(List<Object> elementPath) {
      checkState();
      Element el = null;
      RecordElement rel = null;
      if (elementPath.size() == 1) {
         el = elementMap.get(elementPath.get(0));
      } else {
         String string = (String) elementPath.get(1);
         if (string.startsWith("HEADER(")) {
            Element[] elements = getActiveDataSource(currentMemType).getMsgHeader().getElements();
            for (Element element : elements) {
               if (element.getName().equals(elementPath.get(2))) {
                  return element;
               }
            }
            return null;
         } else {
            el = this.elementMap.get(string);
            if (el instanceof RecordElement) {
               rel = (RecordElement) el;
            }
         }
         for (int i = 2; i < elementPath.size(); i++) {
            if (elementPath.get(i) instanceof String) {
               String name = (String) elementPath.get(i);
               el = rel.getElementMap().get(name);
               if (el instanceof RecordElement) {
                  rel = (RecordElement) el;
               }
            } else if (elementPath.get(i) instanceof Integer) {
               Integer index = (Integer) elementPath.get(i);
               rel = rel.get(index);
               el = rel;
            }
         }
      }
      return el;
   }

   public Element getElement(List<Object> elementPath, DataType type) {
      Element element = getElement(elementPath);
      if (element == null) {
         return null;
      }
      return element.switchMessages(this.getMessageTypeAssociation(type));
   }

   /**
    * @return the element associated with the given name
    * @throws IllegalArgumentException if an element doesn't exist with given name. Use {@link #hasElement(String)} with
    * any use of this function.
    */
   public Element getElement(String elementName, DataType type) {
      checkState();
      Element retVal = elementMap.get(elementName);
      if (retVal != null) {
         return retVal.switchMessages(this.getMessageTypeAssociation(type));
      }
      return null;

   }

   public Element getBodyOrHeaderElement(String elementName) {
      return getBodyOrHeaderElement(elementName, currentMemType);
   }

   public Element getBodyOrHeaderElement(String elementName, DataType type) {
      checkState();
      Element e = elementMap.get(elementName);
      if (e == null) {
         Element[] elements = getActiveDataSource(type).getMsgHeader().getElements();
         for (Element element : elements) {
            if (element.getName().equals(elementName)) {
               return element;
            }
         }
      } else {
         e = e.switchMessages(this.getMessageTypeAssociation(type));
      }
      return e;
   }

   /**
    * Turning off a message causes sends to be short-circuited and the message to be unscheduled.
    */
   public void turnOff() {
      checkState();
      isTurnedOff = true;
      unschedule();
   }

   /**
    * Turning on message allows sends to work again & reschedules message if that is the default state defined by the
    * message constructor call.
    */
   public void turnOn() {
      checkState();
      isTurnedOff = false;
      if (isScheduledFromStart()) {
         schedule();
      }
   }

   /**
    * This is the turnOn being called from the method register in MessageCollection. Messages shouldn't be scheduled at
    * this point b/c the control message hasn't gone out yet. Messages can't go out until the control message goes out
    * the first time so that collisions in the box are avoided.
    */
   public void whenBeingRegisteredTurnOn() {
      isTurnedOff = false;
   }

   /**
    * Returns if the message is turned off.
    */
   public boolean isTurnedOff() {
      return isTurnedOff;
   }

   private void setSchedule(boolean newValue) {
      ArrayList<T> dataList = memToDataMap.get(currentMemType);
      for (T d : dataList) {
         d.setScheduled(newValue);
      }
   }

   /**
    * This method schedules the message. There is also some code that allows the scheduled state to be updated in
    * Message Watch.
    */
   public void schedule() {
      checkState();
      if (!isTurnedOff) {
         setSchedule(true);
         regularUnscheduleCalled = false;
         for (IMessageScheduleChangeListener listener : schedulingChangeListeners) {
            listener.isScheduledChanged(true);
         }
      }
   }

   /**
    * This method unschedules the message. The variable regularUnscheduledCalled is used to preserve unschedules that
    * are called in constructors, which is before the control message goes out for the first time.
    */
   public void unschedule() {
      checkState();
      setSchedule(false);
      regularUnscheduleCalled = true;
      for (IMessageScheduleChangeListener listener : schedulingChangeListeners) {
         listener.isScheduledChanged(false);
      }
   }

   /**
    * Returns if the message is scheduled or not.
    */
   @Deprecated
   public boolean isScheduled() {
      ArrayList<T> dataList = memToDataMap.get(currentMemType);
      for (T d : dataList) {
         if (!d.isScheduled()) {
            return false;
         }
      }
      return true;
   }

   /**
    * This is called at the end of a script run to reset the "hard" unschedule variable that is used to preserve
    * unschedules called in constructors.
    */
   public void resetScheduling() {
      regularUnscheduleCalled = false;

   }

   /**
    * @return - double - rate of message
    */
   public double getRate() {
      return rate;
   }

   /**
    * @return - int - phase of message
    */
   public int getPhase() {
      return phase;
   }

   public MessageSystemListener getListener() {
      return listenerHandler;
   }

   public MessageSystemListener getRemoveableListener() {
      return removableListenerHandler;
   }
   
   public void setListenerTrace(MessageListenerTrace listener) {
      if(listenerHandler != null){
         listenerHandler.setMessageListenerTrace(this, listener);
      }
   }
   
   public MessageListenerTrace clearListenerTrace() {
      if(listenerHandler != null){
         return listenerHandler.clearListenerTrace(this);
      }
      return null;
   }

   public void addListener(IOSEEMessageListener listener) {
      listenerHandler.addListener(listener);
   }

   public boolean removeListener(IOSEEMessageListener listener) {
      return listenerHandler.removeListener(listener);
   }

   /**
    * Notifies all registered listeners of an update.
    * <P>
    * <B>NOTE: </B>Should only be called from sub classes of {@link MessageData}
    * 
    * @param data the Message Data object that has been updated
    * @param type the memtype of the message data object
    */
   public void notifyListeners(final MessageData data, final DataType type) {
      checkState();
      this.listenerHandler.onDataAvailable(data, type);
      this.removableListenerHandler.onDataAvailable(data, type);
   }

   /*
    * public HashMap getTypeToMessageData(){ return typeToMessageData; }
    */
   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   private static final int TransmissionTimeoutDefault = 15000;

   @Override
   public String toString() {
      return name;
   }

   /**
    * @return Returns the messageName.
    */
   public String getMessageName() {
      return name;
   }

   @Override
   public org.w3c.dom.Element toXml(Document doc) {
      org.w3c.dom.Element rootElement = doc.createElement("Message");
      rootElement.appendChild(Jaxp.createElement(doc, "Name", name));
      rootElement.appendChild(Jaxp.createElement(doc, "Type", getMemType().name()));
      return rootElement;
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      writer.writeStartElement("Message");
      XMLStreamWriterUtil.writeElement(writer, "Name", name);
      XMLStreamWriterUtil.writeElement(writer, "Type", getMemType().name());
      writer.writeEndElement();
   }

   @JsonProperty
   public String getType() {
       return getMemType().name();
   }
   
   public void zeroize() {
      checkState();
      for (DataType memType : memToDataMap.keySet()) {
         for (Element el : getElements(memType)) {
            el.zeroize();
         }
      }
   }

   /**
    * Verifies that the message is sent at least once using the default message timeout. DO NOT override this method in
    * production code.
    * 
    * @return if the check passed
    */
   public boolean checkForTransmission(ITestAccessor accessor) throws InterruptedException {
      return checkForTransmission(accessor, TransmissionTimeoutDefault);
   }

   /**
    * Verifies that the message is sent at least once within the time specified. DO NOT override this method in
    * production code.
    * 
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    */
   public boolean checkForTransmission(ITestAccessor accessor, int milliseconds) throws InterruptedException {
      return checkForTransmissions(accessor, 1, milliseconds);
   }

   /**
    * Verifies that the message is sent at least "numTransmission" times within the default message timeout. DO NOT
    * override this method in production code.
    * 
    * @param numTransmissions the number of transmissions to look for
    * @return if the check passed
    */
   public boolean checkForTransmissions(ITestAccessor accessor, int numTransmissions) throws InterruptedException {
      return checkForTransmissions(accessor, numTransmissions, TransmissionTimeoutDefault);
   }

   /**
    * Verifies that the message is sent at least "numTransmission" times within the time specified.
    * 
    * @param numTransmissions the number of transmission to look for
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    */
   public boolean checkForTransmissions(ITestAccessor accessor, int numTransmissions, int milliseconds) throws InterruptedException {
      checkState();
      accessor.getLogger().methodCalledOnObject(accessor, getMessageName(),
         new MethodFormatter().add(numTransmissions).add(milliseconds));
      TransmissionCountCondition c = new TransmissionCountCondition(numTransmissions);
      MsgWaitResult result = waitForCondition(accessor, c, false, milliseconds);
      CheckPoint passFail =
         new CheckPoint(this.name, Integer.toString(numTransmissions), Integer.toString(result.getXmitCount()),
            result.isPassed(), result.getXmitCount(), result.getElapsedTime());
      accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);
      accessor.getLogger().methodEnded(accessor);
      return passFail.isPass();
   }

   /**
    * Verifies that the message is not sent within the time specified.
    * 
    * @param milliseconds the amount to time (in milliseconds) to check
    * @return if the check passed
    */
   public boolean checkForNoTransmissions(ITestEnvironmentMessageSystemAccessor accessor, int milliseconds) throws InterruptedException {
      checkState();
      if (accessor == null) {
         throw new IllegalArgumentException("accessor cannot be null");
      }
      accessor.getLogger().methodCalledOnObject(accessor, getMessageName(), new MethodFormatter().add(milliseconds),
         this);
      long time = accessor.getEnvTime();
      org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer cancelTimer =
         accessor.setTimerFor(listenerHandler, milliseconds);

      boolean result;
      listenerHandler.waitForData(); // will also return if the timer (set above)
      // expires

      result = listenerHandler.isTimedOut();

      cancelTimer.cancelTimer();
      time = accessor.getEnvTime() - time;

      accessor.getLogger().testpoint(
         accessor,
         accessor.getTestScript(),
         accessor.getTestScript().getTestCase(),
         new CheckPoint(this.getMessageName(), "No Transmissions",
            result ? "No Transmissions" : "Transmissions Occurred", result, time));
      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
      return result;
   }

   /**
    * Waits until message is sent at least once within the default message timeout.
    * 
    * @return if the check passed
    */
   public boolean waitForTransmission(ITestEnvironmentMessageSystemAccessor accessor) throws InterruptedException {
      return waitForTransmission(accessor, TransmissionTimeoutDefault);
   }

   /**
    * Waits until message is sent at least once within the time specified.
    * 
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    */
   public boolean waitForTransmission(ITestEnvironmentMessageSystemAccessor accessor, int milliseconds) throws InterruptedException {
      return waitForTransmissions(accessor, 1, milliseconds);
   }

   /**
    * Waits until message is sent at least "numTransmission" times within the default message timeout.
    * 
    * @param numTransmissions the number of transmissions to look for
    * @return if the check passed
    */
   public boolean waitForTransmissions(ITestEnvironmentMessageSystemAccessor accessor, int numTransmissions) throws InterruptedException {
      return waitForTransmissions(accessor, numTransmissions, TransmissionTimeoutDefault);
   }

   /**
    * Waits until message is sent at least "numTransmission" times within the time specified.
    * 
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    */
   public boolean waitForTransmissions(ITestEnvironmentMessageSystemAccessor accessor, int numTransmissions, int milliseconds) throws InterruptedException {
      checkState();
      accessor.getLogger().methodCalledOnObject(accessor, getMessageName(),
         new MethodFormatter().add(numTransmissions).add(milliseconds), this);
      boolean pass = waitForTransmissionsNoLog(accessor, numTransmissions, milliseconds);
      accessor.getLogger().methodEnded(accessor);
      return pass;
   }

   public boolean waitForTransmissionsNoLog(ITestEnvironmentMessageSystemAccessor accessor, int numTransmissions, int milliseconds) throws InterruptedException {
      checkState();
      if (accessor == null) {
         throw new IllegalArgumentException("environment accessor parameter cannot be null");
      }
      TransmissionCountCondition c = new TransmissionCountCondition(numTransmissions);
      MsgWaitResult result = waitForCondition(accessor, c, false, milliseconds);
      return result.isPassed();
   }

   public MsgWaitResult waitForCondition(ITestEnvironmentAccessor accessor, ICondition condition, boolean maintain, int milliseconds) throws InterruptedException {
      checkState();
      return listenerHandler.waitForCondition(accessor, condition, maintain, milliseconds);
   }

   /**
    * @return Returns size value.
    */
   public int getMaxDataSize() {
      checkState();
      return getMaxDataSize(currentMemType);
   }

   public int getMaxDataSize(DataType type) {
      checkState();
      int size = 0;
      for (MessageData msgData : memToDataMap.get(type)) {
         if (msgData != null && msgData.getPayloadSize() > size) {
            size = msgData.getPayloadSize();
         }
      }
      return size;
   }

   /*
    * @Override public boolean equals(Object obj) { return this.getClass().equals(obj.getClass()); }
    */

   /*
    * @Override public int hashCode() { return getClass().hashCode(); }
    */

   /**
    * returns a {@link MessageState} object that represents this message's state. The state is intended to be used in
    * synchronizing a remote instance of this message
    * 
    * @return Returns MessageState object reference.
    */
   public MessageState getMessageState() {
      checkState();
      MessageMode mode = isWriter() ? MessageMode.WRITER : MessageMode.READER;
      return new MessageState(currentMemType, getData(), memToDataMap.keySet(), mode);
   }

   /**
    * restores the state of this message. The state is intended to come from a remote instance of this message.
    */
   public void setMessageState(final MessageState state) {
      checkState();
      setMemSource(state.getCurrentMemType());
      setData(state.getData());
   }

   public void addSchedulingChangeListener(IMessageScheduleChangeListener listener) {
      checkState();
      schedulingChangeListeners.add(listener);
   }

   public void removeSchedulingChangeListener(IMessageScheduleChangeListener listener) {
      checkState();
      schedulingChangeListeners.remove(listener);
   }

   public T getActiveDataSource() {
      checkState();
      ArrayList<T> dataList = memToDataMap.get(currentMemType);
      if (dataList == null) {
         throw new IllegalStateException("no datas for " + currentMemType);
      }
      return dataList.get(0);
   }

   public T getActiveDataSource(DataType type) {
      checkState();
      ArrayList<T> dataList = memToDataMap.get(type);
      return dataList != null ? dataList.get(0) : null;
   }

   public void addElements(Element... elements) {
      checkState();
      for (Element element : elements) {
         elementMap.put(element.getElementName(), element);
         element.addPath(this.getClass().getName());
      }
   }

   public int getBitOffset() {
      return 0;
   }

   /**
    * @param currentMemType the currentMemType to set
    */
   protected void setCurrentMemType(DataType currentMemType) {
      checkState();
      this.currentMemType = currentMemType;
   }

   public boolean setMemSource(DataType type) {
      checkState();
      DataType oldMemType = getMemType();
      notifyPreMemSourceChangeListeners(oldMemType, type, this);
      this.switchElementAssociation(type);
      setCurrentMemType(type);
      notifyPostMemSourceChangeListeners(oldMemType, type, this);
      return true;
   }

   public boolean activateMemSource(DataType type) {
      checkState();
      DataType oldMemType = getMemType();
      notifyPreMemSourceChangeListeners(oldMemType, type, this);
      //		this.switchElementAssociation(type);
      //		setCurrentMemType(type);
      notifyPostMemSourceChangeListeners(oldMemType, type, this);
      return true;
   }

   private void notifyPostMemSourceChangeListeners(DataType old, DataType newtype, Message message) {
      checkState();
      for (IMemSourceChangeListener listener : postMemSourceChangeListeners) {
         try {
            listener.onChange(old, newtype, message);
         } catch (Exception e) {
            OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, e);
         }
      }
   }

   private void notifyPreMemSourceChangeListeners(DataType old, DataType newtype, Message message) {
      checkState();
      for (IMemSourceChangeListener listener : preMemSourceChangeListeners) {
         listener.onChange(old, newtype, message);
      }
   }

   public void addPreMemSourceChangeListener(IMemSourceChangeListener listener) {
      checkState();
      preMemSourceChangeListeners.add(listener);
   }

   public void addPostMemSourceChangeListener(IMemSourceChangeListener listener) {
      checkState();
      postMemSourceChangeListeners.add(listener);
   }
   
   public void removePreMemSourceChangeListener(IMemSourceChangeListener listener) {
      checkState();
      preMemSourceChangeListeners.remove(listener);
   }

   public void removePostMemSourceChangeListener(IMemSourceChangeListener listener) {
      checkState();
      postMemSourceChangeListeners.remove(listener);
   }

   public void addPreMessageDisposeListener(IMessageDisposeListener listener) {
      checkState();
      preMessageDisposeListeners.add(listener);
   }

   public void removePreMessageDisposeListener(IMessageDisposeListener listener) {
      checkState();
      preMessageDisposeListeners.remove(listener);
   }

   public void addPostMessageDisposeListener(IMessageDisposeListener listener) {
      checkState();
      postMessageDisposeListeners.add(listener);
   }

   /**
    * @return the memToDataMap
    */
   public Collection<ArrayList<T>> getAllData() {
      checkState();
      return memToDataMap.values();
   }

   public Set<DataType> getAvailableMemTypes() {
      checkState();
      return memToDataMap.keySet();
   }

   public Collection<T> getMessageData(DataType type) {
      checkState();
      return memToDataMap.get(type);
   }

   public String getTypeName() {
      return getName();
   }

   /**
    * This variable reflects whether a message is defined to start out being scheduled.
    * 
    * @return Returns the isScheduledFromStart.
    */
   public boolean isScheduledFromStart() {
      return isScheduledFromStart;
   }

   /**
    * This variable reflects whether unsubscribe has been called on the message. The main purpose of this is to preserve
    * if an unschedule is called on a message from a constructor.
    * 
    * @return Returns the regularUnscheduleCalled.
    */
   public boolean isRegularUnscheduleCalled() {
      return regularUnscheduleCalled;
   }

   /**
    * @return the defaultMessageData
    */
   public T getDefaultMessageData() {
      checkState();
      return defaultMessageData;
   }

   /**
    * @param defaultMessageData the defaultMessageData to set
    */
   @SuppressWarnings("unchecked")
   protected void setDefaultMessageData(T defaultMessageData) {
      checkState();
      this.defaultMessageData = defaultMessageData;
      addMessageDataSource(defaultMessageData);
      addMessageTypeAssociation(defaultMessageData.getType(), (U) this);
   }

   public boolean isWriter() {
      checkState();
      return defaultMessageData.isWriter();
   }

   public void setMemTypeActive(DataType type) {
      checkState();
      memTypeActive.add(type);
      notifyPostMemSourceChangeListeners(currentMemType, currentMemType, this);
   }

   public void setMemTypeInactive(DataType type) {
      checkState();
      memTypeActive.add(type);
      notifyPostMemSourceChangeListeners(currentMemType, currentMemType, this);
   }

   public boolean isMemTypeActive(DataType type) {
      checkState();
      return memTypeActive.contains(type);
   }

   protected void checkState() throws IllegalStateException {
      if (isDestroyed()) {
         throw new IllegalStateException(getName() + " is destroyed");
      }
   }

   public boolean isDestroyed() {
      return destroyed;
   }

   @Override
   protected void finalize() throws Throwable {
      finalized.incrementAndGet();
      super.finalize();
   }

   public static long getConstructed() {
      return constructed.get();
   }

   public static long getFinalized() {
      return finalized.get();
   }

   public boolean isValidElement(Element currentElement, Element proposedElement) {
      return true;
   }

   public IMessageHeader[] getHeaders() {
      final Collection<T> dataSources = getMemSource(getMemType());
      if (dataSources.size() > 0) {
         final IMessageHeader[] headers = new IMessageHeader[dataSources.size()];
         int i = 0;
         for (T dataSrc : dataSources) {
            headers[i] = dataSrc.getMsgHeader();
            i++;
         }
         return headers;
      } else {
         return new IMessageHeader[0];
      }
   }

   public long getActivityCount() {
      return getActiveDataSource().getActivityCount();
   }

   public long getSentCount() {
      return getActiveDataSource().getSentCount();
   }

   public void setActivityCount(long activityCount) {
      getActiveDataSource().setActivityCount(activityCount);
   }

   public void switchElementAssociation(Collection<U> messages) {
   }

   @SuppressWarnings("rawtypes")
   public Map<? extends DataType, Class<? extends Message>[]> getAssociatedMessages() {
      return new HashMap<DataType, Class<? extends Message>[]>();
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   public void postCreateMessageSetup(IMessageManager messageManager, MessageData data) throws Exception {
      Map<? extends DataType, Class<? extends Message>[]> o = getAssociatedMessages();
      messageRequestor = messageManager.createMessageRequestor(getName());
      for (Entry<? extends DataType, Class<? extends Message>[]> entry : o.entrySet()) {
         if (messageManager.isPhysicalTypeAvailable(entry.getKey())) {
            for (Class<? extends Message> clazz : entry.getValue()) {
               final Message message;
               if (data.isWriter()) {
                  message = messageRequestor.getMessageWriter(clazz);
               } else {
                  message = messageRequestor.getMessageReader(clazz);
               }
               this.addMessageDataSource((T) message.getDefaultMessageData());
               this.addMessageTypeAssociation(entry.getKey(), (U) message);
               setMemSource(entry.getKey());
            }
         }
      }
   }

   /**
    * Changes the rate a message is being published at. NOTE: This is only going to be allowed to be used on periodic
    * message & users are not allowed to set rate to zero.
    * 
    * @param newRate - hz
    */
   public void changeRate(double newRate) {
      if (Math.abs(newRate - 0.0) < doubleTolerance) { //newRate == 0.0
         throw new IllegalArgumentException(
            "Cannot change message rate to zero (" + getName() + ")!\n\tUse unschedule() to do that!");
      }
      if (Math.abs(newRate - rate) > doubleTolerance) { //newRate != rate
         //         accessor.getMsgManager().changeMessageRate(this, newRate, rate);
         double oldRate = rate;
         rate = newRate;
         for (IMessageScheduleChangeListener listener : schedulingChangeListeners) {
            listener.onRateChanged(this, oldRate, newRate);
         }
      }
   }

   /**
    * Changes the rate back to the default rate.
    */
   public void changeRateToDefault(ITestEnvironmentMessageSystemAccessor accessor) {
      //      accessor.getMsgManager().changeMessageRate(this, defaultRate, rate);
      double oldRate = getRate();
      rate = defaultRate;
      for (IMessageScheduleChangeListener listener : schedulingChangeListeners) {
         listener.onRateChanged(this, oldRate, defaultRate);
      }
   }

   public void sendWithLog(ITestAccessor accessor) {
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, getMessageName(), new MethodFormatter(), this);
      }
      send();
      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
   }

   public int getDefaultByteSize() {
      return defaultByteSize;
   }

   public int getDefaultOffset() {
      return defaultOffset;
   }

   public Element getElementByPath(ElementPath path) {
      return getElementByPath(path, currentMemType);
   }

   public Element getElementByPath(ElementPath path, DataType type) {
      if (path.isHeaderElement()) {
         Element[] elements = getActiveDataSource(type).getMsgHeader().getElements();
         for (Element element : elements) {
            if (element.getName().equals(path.getElementName())) {
               return element;
            }
         }
         return null;
      }
      return getElement(path.getList(), type);
   }
   
   public ListIterator<Element> getElementIterator() {
	   ArrayList<Element> list = new ArrayList<>(elementMap.values());
	   return list.listIterator();
   }
   
   public ListIterator<Element> getElementIterator(Element elemnt) {
	   ArrayList<Element> list = new ArrayList<>(elementMap.values());
	   int index = list.indexOf(elemnt);
	   if (index >= 0) {
		   return list.listIterator(index);		   
	   }
	   return null;
   }

   
}