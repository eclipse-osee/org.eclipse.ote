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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.listener.OteRecorderListener;

/**
 * @author Shandeep Singh
 */
public class OteRecorder {
   private Set<Message> oteMessages;
   private List<Message> messageRecordings;
   private Map<Message, OteRecorderListener> messageListenerMap;
   private boolean isRecordingStarted;

   public OteRecorder(List<Message> oteMessages) {
      this.oteMessages = new HashSet<>();
      this.oteMessages.addAll(oteMessages);
      this.messageRecordings = new ArrayList<>();
      this.messageListenerMap = new HashMap<>();
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
         OteRecorderListener oteRecorderListener = new OteRecorderListener(oteMessage, messageRecordings);
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
    * Returns a List of all recorded messages
    * 
    * @return List
    */
   public List<Message> getAllRecordedMessages() {
      return messageRecordings;
   }

   /**
    * Returns a List of all recorded messages of a specific type of message
    * 
    * @param oteMessage - A Message type
    * @return List
    */
   @SuppressWarnings("unchecked")
   public <T extends Message> List<T> getAllMessagesOfType(T oteMessage) {
      return (List<T>) messageRecordings.stream().filter(
         message -> message.getMessageName().equals(oteMessage.getMessageName())).collect(Collectors.toList());
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
    * Returns a List of all the specified messages to find after a specific message
    * 
    * @param messageToFind - A generic Message type to find
    * @param messageToFindAfter - The message to start at
    * @return List - A List of all messageToFind
    */
   public <T extends Message> List<T> getAllMessagesAfterOfType(T messageToFind, Message messageToFindAfter) {
      List<T> messagesAfterList = new ArrayList<>();
      int indexOfStartingMessage = messageRecordings.indexOf(messageToFindAfter) + 1;

      if (indexOfStartingMessage != -1) {
         for (int i = indexOfStartingMessage; i < messageRecordings.size(); i++) {
            if (messageRecordings.get(i).getMessageName() == ((Message) messageToFind).getMessageName()) {
               messagesAfterList.add((T) messageRecordings.get(i));
            }
         }
      }

      return messagesAfterList;
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
}
