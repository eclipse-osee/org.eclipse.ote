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
package org.eclipse.osee.ote.message;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.FixedPointElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.listener.OteRecorderListener;

/**
 * @author Shandeep Singh
 */
public class OteRecorder {
   private Set<Message> oteMessages;
   private List<Message> messageRecordings;
   private Map<Message, OteRecorderListener> messageListenerMap;
   private boolean isRecordingStarted;
   private List<Message> chainedFilters;
   private OteRecorderChainedFilter oteRecorderChainedFilter;
   private int maxMessageRecordings = Integer.MAX_VALUE;

   public OteRecorder(List<Message> oteMessages) {
      this.oteMessages = new HashSet<>();
      this.oteMessages.addAll(oteMessages);
      this.messageRecordings = new ArrayList<>();
      this.messageListenerMap = new HashMap<>();
      this.oteRecorderChainedFilter = new OteRecorderChainedFilter();
   }

   public OteRecorder(int maxMessageRecordings, List<Message> oteMessages) {
      this(oteMessages);
      this.maxMessageRecordings = maxMessageRecordings;
   }

   /**
    * If recorder is not started, start recording for all messages that are added to this recorder
    * 
    * @return boolean - true if the recording successfully started, false if it did not start or already is started
    */
   public boolean start() {
      if (!isRecordingStarted) {
         for (Message oteMessage : oteMessages) {
            addListenerToMessage(oteMessage);
         }
         isRecordingStarted = true;
         return true;
      } else {
         OseeLog.log(OteRecorder.class, Level.INFO, "Recording already started");
         return false;
      }
   }

   /**
    * If recorder is started, pause recording for all messages that currently have a listener added to them
    * 
    * @return boolean - true if the recording successfully paused, false if it did not pause or already is paused
    */
   public boolean pause() {
      if (isRecordingStarted) {
         for (Map.Entry<Message, OteRecorderListener> entrySet : messageListenerMap.entrySet()) {
            Message message = entrySet.getKey();
            OteRecorderListener oteRecorderListener = entrySet.getValue();
            message.removeListener(oteRecorderListener);
         }
         messageListenerMap.clear();
         isRecordingStarted = false;
         return true;
      } else {
         OseeLog.log(OteRecorder.class, Level.INFO, "Recording already paused");
         return false;
      }
   }

   /**
    * Adds a single message to the current recorder object. Next time the recorder is started, the listener will start
    * listening for the passed in message. If the recorder is currently started, the listener will also be added to the
    * passed in message and start listening to it immediately
    * 
    * @param oteMessage - A Message type
    */
   public void addMessageToRecorder(Message oteMessage) {
      oteMessages.add(oteMessage);
      if (isRecordingStarted) {
         addListenerToMessage(oteMessage);
      }
   }

   /**
    * Removes a single message from the current recorder object. Next time the recorder is started, the listener will
    * stop listening for the passed in message. If the recorder is currently started, the listener will also be removed
    * from the passed in message and stop listening to it immediately
    * 
    * @param oteMessage - A Message type
    */
   public void removeMessageFromRecorder(Message oteMessage) {
      oteMessages.remove(oteMessage);
      if (isRecordingStarted) {
         removeListenerFromMessage(oteMessage);
      }
   }

   private void addListenerToMessage(Message oteMessage) {
      if (!messageListenerMap.containsKey(oteMessage)) {
         OteRecorderListener oteRecorderListener =
            new OteRecorderListener(oteMessage, messageRecordings, maxMessageRecordings);
         oteMessage.addListener(oteRecorderListener);
         messageListenerMap.put(oteMessage, oteRecorderListener);
      } else {
         OseeLog.log(OteRecorder.class, Level.INFO, oteMessage.getMessageName() + " is already added to the recorder");
      }
   }

   private void removeListenerFromMessage(Message oteMessage) {
      if (messageListenerMap.containsKey(oteMessage)) {
         OteRecorderListener oteRecorderListener = messageListenerMap.get(oteMessage);
         oteMessage.removeListener(oteRecorderListener);
         messageListenerMap.remove(oteMessage);
      } else {
         OseeLog.log(OteRecorder.class, Level.INFO, oteMessage.getMessageName() + " was not found in the recorder");
      }
   }

   /**
    * Returns an object which will contain all recorded messages
    * 
    * @return OteRecorderChainedFilter
    */
   public OteRecorderChainedFilter getAllRecordedMessages() {
      oteRecorderChainedFilter = setChainedFilters(messageRecordings);

      return oteRecorderChainedFilter;
   }

   /**
    * Returns an object which will contain of all recorded messages of a specific type of message
    * 
    * @param oteMessage - A Message type
    * @return OteRecorderChainedFilter
    */
   public <T extends Message> OteRecorderChainedFilter getAllMessagesOfType(T oteMessage) {
      chainedFilters = messageRecordings.stream().filter(
         message -> message.getMessageName().equals(oteMessage.getMessageName())).collect(Collectors.toList());

      oteRecorderChainedFilter = setChainedFilters(chainedFilters);
      return oteRecorderChainedFilter;
   }

   private OteRecorderChainedFilter setChainedFilters(List<Message> chainedFilters) {
      oteRecorderChainedFilter = new OteRecorderChainedFilter();
      oteRecorderChainedFilter.setChainedFilters(chainedFilters);

      return oteRecorderChainedFilter;
   }

   /**
    * Returns the index of the first instance of a specific type of message
    * 
    * @param oteMessage - A Message type
    * @return int - index of message
    */
   public int getFirstIndexOf(Message oteMessage) {
      int index = -1;

      for (int i = 0; i < messageRecordings.size(); i++) {
         if (messageRecordings.get(i).getMessageName() == oteMessage.getMessageName()) {
            index = i;
            break;
         }
      }

      return index;
   }

   /**
    * Returns the first instance of a specific type of message
    * 
    * @param oteMessage - A generic Message type
    * @return T - first instance of type oteMessage
    */
   @SuppressWarnings("unchecked")
   public <T extends Message> T getFirstMessageOfType(T oteMessage) {
      T firstMessageOfWantedType = null;

      for (Message message : messageRecordings) {
         if (message.getMessageName() == ((Message) oteMessage).getMessageName()) {
            firstMessageOfWantedType = (T) message;
            break;
         }
      }

      return firstMessageOfWantedType;
   }

   /**
    * Returns the Message at the specified index
    * 
    * @param index
    * @return Message
    */
   @SuppressWarnings("unchecked")
   public <T extends Message> T getMessageAtIndex(int index) {
      if (index >= messageRecordings.size()) {
         return null;
      } else {
         return (T) messageRecordings.get(index);
      }
   }

   /**
    * Returns the first instance of the specified message to find after a specific message
    * 
    * @param messageToFind - A generic Message type to find
    * @param messageToFindAfter - The message to start at
    * @return T - first instance of the messageToFind
    */
   @SuppressWarnings("unchecked")
   public <T extends Message> T getFirstMessageAfterOfType(T messageToFind, Message messageToFindAfter) {
      T firstMessagesAfter = null;
      int indexOfStartingMessage = messageRecordings.indexOf(messageToFindAfter) + 1;

      for (int i = indexOfStartingMessage; i < messageRecordings.size(); i++) {
         if (messageRecordings.get(i).getMessageName() == ((Message) messageToFind).getMessageName()) {
            firstMessagesAfter = (T) messageRecordings.get(i);
            break;
         }
      }

      return firstMessagesAfter;
   }

   /**
    * Returns an object which will contain all the specified messages to find after a specific message
    * 
    * @param messageToFind - A generic Message type to find
    * @param messageToFindAfter - The message to start at
    * @return OteRecorderChainedFilter
    */
   public <T extends Message> OteRecorderChainedFilter getAllMessagesAfterOfType(T messageToFind, Message messageToFindAfter) {
      List<Message> messagesAfterList = new ArrayList<>();
      int indexOfStartingMessage = messageRecordings.indexOf(messageToFindAfter) + 1;

      if (indexOfStartingMessage != -1) {
         for (int i = indexOfStartingMessage; i < messageRecordings.size(); i++) {
            if (messageRecordings.get(i).getMessageName() == ((Message) messageToFind).getMessageName()) {
               messagesAfterList.add(messageRecordings.get(i));
            }
         }
      }

      oteRecorderChainedFilter = setChainedFilters(messagesAfterList);
      return oteRecorderChainedFilter;
   }

   /**
    * Returns a List of all the message types to record currently added to this recorder object
    * 
    * @return List - A List of all the messages types to record
    */
   public List<Message> getCurrentOteMessagesToRecord() {
      return oteMessages.stream().collect(Collectors.toList());
   }

   /**
    * Clears the current list of all message recordings
    */
   public void clearAllMessageRecordings() {
      messageRecordings = new ArrayList<>();
      for (Map.Entry<Message, OteRecorderListener> entrySet : messageListenerMap.entrySet()) {
         entrySet.getValue().setMessageRecordings(messageRecordings);
      }
   }

   /**
    * Verifies an element with a given value exists in the recording list and logs a test point
    * 
    * @param accessor
    * @param message - A generic message type
    * @param element - The element to find
    * @param value - The expected value for the element
    */
   public <T extends Message, U extends Comparable<U>> void verifyElementValueExists(ITestAccessor accessor, T message, DiscreteElement<U> element, U value) {
      check(accessor, message, element, value);
   }

   private <T extends Message, U extends Comparable<U>> void check(ITestAccessor accessor, T message, DiscreteElement<U> element, U value) {
      if (accessor != null) {
         accessor.getLogger().methodCalledOnObject(accessor, "verifyElementValueExists",
            new MethodFormatter().add(message).add(element.getElementName()).add(value), message);
      }

      boolean isPass = false;
      DiscreteElement actualElementValue;

      if (element instanceof FixedPointElement) {
         value = adjustDoubleValue(element, value);
      }

      for (Message messageRecording : messageRecordings) {
         if (messageRecording.getMessageName().equals(((Message) message).getMessageName())) {

            actualElementValue = getElementToRead(messageRecording, element);

            if (value.equals(actualElementValue.getValue())) {
               isPass = true;
               break;
            }
         }
      }

      CheckPoint passFail =
         new CheckPoint("verifyElementValueExists", String.valueOf(true), String.valueOf(isPass), isPass, 0);

      accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);

      if (accessor != null) {
         accessor.getLogger().methodEnded(accessor);
      }
   }

   private <T extends Comparable<T>, U extends Message> DiscreteElement getElementToRead(Message message, DiscreteElement<T> element) {
      DiscreteElement elementToRead = null;

      Class<? extends DiscreteElement> clazz = element.getClass();
      elementToRead = message.getElement(element.getElementName(), clazz);

      // May be a record so use path instead
      if (elementToRead == null) {
         elementToRead = clazz.cast(message.getElement(element.getElementPath()));
      }

      return elementToRead;
   }

   @SuppressWarnings("unchecked")
   private <T extends Comparable<T>> T adjustDoubleValue(DiscreteElement<T> element, T value) {
      try {
         if (element instanceof FixedPointElement) {
            Method adjustMethod = element.getClass().getDeclaredMethod("adjust", Double.class);
            adjustMethod.setAccessible(true);
            Object adjustedValue = adjustMethod.invoke(element, value);
            value = (T) adjustedValue;
         }
      } catch (Exception e) {
         OseeLog.log(OteRecorder.class, Level.SEVERE, "Could not adjust element value for " + element.getElementName());
      }

      return value;
   }

   public class OteRecorderChainedFilter {
      private List<Message> chainedFilters;

      @SuppressWarnings("unchecked")
      public <T extends Message> List<T> getList() {
         return (List<T>) chainedFilters;
      }

      protected void setChainedFilters(List<Message> chainedFilters) {
         this.chainedFilters = chainedFilters;
      }

      /**
       * Filters the message recording list to only include the elements with the given value
       * 
       * @param element - The element to filter on
       * @param value - The expected value for the element
       * @return OteRecorderChainedFilter - Returns the same classes instance to allow for chaining multiple filters
       */
      public <T extends Comparable<T>> OteRecorderChainedFilter filterMessagesWithElement(DiscreteElement<T> element, T value) {
         chainedFilters = addToChainedFilter(chainedFilters, element, value);
         return this;
      }

      private <T extends Comparable<T>, U extends Message> List<Message> addToChainedFilter(List<U> messages, DiscreteElement<T> element, T value) {
         chainedFilters = new ArrayList<>();
         DiscreteElement actualElementValue;

         if (element instanceof FixedPointElement) {
            value = adjustDoubleValue(element, value);
         }

         for (Message message : messages) {

            actualElementValue = getElementToRead(message, element);

            if (value.equals(actualElementValue.getValue())) {
               chainedFilters.add(message);
            }
         }

         return chainedFilters;
      }
   }

}
