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

package org.eclipse.osee.ote.message.other;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.OteRecorder;
import org.eclipse.osee.ote.message.listener.OteRecorderListener;
import org.eclipse.osee.ote.message.mock.TestMemType;
import org.eclipse.osee.ote.message.mock.TestMessage;
import org.eclipse.osee.ote.message.mock.TestMessageData;
import org.eclipse.osee.ote.message.mock.TestOteRecorderMessage;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Shandeep Singh
 */
public class TestOteRecorder {
   private static Set<Message> messages = new HashSet<>();
   private static List<Message> messageRecordings = new ArrayList<>();;
   private OteRecorder oteRecorder;

   @Before
   public void setupBeforeTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
      messageRecordings.clear();
      messages.clear();

      TestMessage oteMessage = new TestMessage();
      messages.add(oteMessage);

      TestMessage messageRecording = new TestMessage();
      TestMessage messageRecording2 = new TestMessage();
      TestOteRecorderMessage messageRecording3 = new TestOteRecorderMessage();
      TestMessage messageRecording4 = new TestMessage();
      messageRecordings.add(messageRecording);
      messageRecordings.add(messageRecording2);
      messageRecordings.add(messageRecording3);
      messageRecordings.add(messageRecording4);

      List<Message> oteMessages = new ArrayList<>();
      oteMessages.addAll(messages);
      oteRecorder = new OteRecorder(oteMessages);
      Field messageRecordingsField = OteRecorder.class.getDeclaredField("messageRecordings");
      Field oteMessagesField = OteRecorder.class.getDeclaredField("oteMessages");
      messageRecordingsField.setAccessible(true);
      oteMessagesField.setAccessible(true);

      messageRecordingsField.set(oteRecorder, messageRecordings);
      oteMessagesField.set(oteRecorder, messages);
   }

   @Test
   public void testStartRecording() {
      boolean firstStart = oteRecorder.start();
      boolean secondStart = oteRecorder.start();

      assertEquals(true, firstStart);
      assertEquals(false, secondStart);
   }

   @Test
   public void testStartRecordingAfterPause() {
      boolean firstStart = oteRecorder.start();
      boolean firstPause = oteRecorder.pause();
      boolean startAfterPause = oteRecorder.start();

      assertEquals(true, firstStart);
      assertEquals(true, firstPause);
      assertEquals(true, startAfterPause);
   }

   @Test
   public void testPauseRecording() {
      boolean firstStart = oteRecorder.start();
      boolean firstPause = oteRecorder.pause();
      boolean secondPause = oteRecorder.pause();

      assertEquals(true, firstStart);
      assertEquals(true, firstPause);
      assertEquals(false, secondPause);
   }

   @Test
   public void testPauseRecordingWithoutStart() {
      boolean firstPause = oteRecorder.pause();

      assertEquals(false, firstPause);
   }

   @Test
   public void testAddMessageToRecorder() {
      oteRecorder.start();

      List<Message> allMessagesToRecord = oteRecorder.getCurrentOteMessagesToRecord();
      assertEquals(1, allMessagesToRecord.size());

      TestOteRecorderMessage messageToAdd = new TestOteRecorderMessage();
      oteRecorder.addMessageToRecorder(messageToAdd);
      List<Message> allMessagesToRecordAfterAdding = oteRecorder.getCurrentOteMessagesToRecord();

      assertEquals(2, allMessagesToRecordAfterAdding.size());
   }

   @Test
   public void testAddExistingMessageToRecorder() {
      messages.clear();

      TestMessage messageToAdd = new TestMessage();
      oteRecorder.addMessageToRecorder(messageToAdd);
      oteRecorder.addMessageToRecorder(messageToAdd);
      List<Message> allMessagesToRecordAfterAdding = oteRecorder.getCurrentOteMessagesToRecord();

      assertEquals(1, allMessagesToRecordAfterAdding.size());
   }

   @Test
   public void testRemoveMessageFromRecorder() {
      oteRecorder.start();

      List<Message> allMessagesToRecord = oteRecorder.getCurrentOteMessagesToRecord();
      assertEquals(1, allMessagesToRecord.size());

      TestMessage messageToRemove = (TestMessage) allMessagesToRecord.get(0);
      oteRecorder.removeMessageFromRecorder(messageToRemove);
      List<Message> allMessagesToRecordAfterRemoving = oteRecorder.getCurrentOteMessagesToRecord();

      assertEquals(0, allMessagesToRecordAfterRemoving.size());
   }

   @Test
   public void testGetAllRecordedMessages() {
      List<Message> allRecordedMessages = oteRecorder.getAllRecordedMessages();

      assertEquals(4, allRecordedMessages.size());
   }

   @Test
   public void testGetAllMessagesOfType() {
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);
      List<Message> allMessagesOfTestMessageType = oteRecorder.getAllMessagesOfType(secondRecordedMessage);

      assertEquals(3, allMessagesOfTestMessageType.size());
   }

   @Test
   public void testGetFirstIndexOf() {
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);
      int firstInstanceOfTestMessage = oteRecorder.getFirstIndexOf(secondRecordedMessage);

      assertEquals(0, firstInstanceOfTestMessage);
   }

   @Test
   public void testGetFirstIndexOfNoExistingMessage() {
      messageRecordings.clear();
      TestOteRecorderMessage recordedMessage = new TestOteRecorderMessage();
      messageRecordings.add(recordedMessage);

      TestMessage notRecordedMessage = new TestMessage();
      int firstInstanceOfTestMessage = oteRecorder.getFirstIndexOf(notRecordedMessage);

      assertEquals(-1, firstInstanceOfTestMessage);
   }

   @Test
   public void testGetFirstMessageOfType() {
      TestMessage firstRecordedMessage = (TestMessage) messageRecordings.get(0);
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);
      TestMessage firstMessageOfFirstRecordedMessage = oteRecorder.getFirstMessageOfType(firstRecordedMessage);
      TestMessage firstMessageOfSecondRecordedMessage = oteRecorder.getFirstMessageOfType(secondRecordedMessage);

      assertEquals(firstRecordedMessage, firstMessageOfFirstRecordedMessage);
      assertNotEquals(secondRecordedMessage, firstMessageOfSecondRecordedMessage);
   }

   @Test
   public void testGetFirstMessageOfTypeNoExistingMessage() {
      messageRecordings.clear();
      TestOteRecorderMessage recordedMessage = new TestOteRecorderMessage();
      messageRecordings.add(recordedMessage);

      TestMessage nonExistingMessage = oteRecorder.getFirstMessageOfType(new TestMessage());

      assertNull(nonExistingMessage);
   }

   @Test
   public void testGetMessageAtIndex() {
      TestMessage firstRecordedMessage = (TestMessage) messageRecordings.get(0);
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);
      Message secondRecordedMessageFromOteRecorder = oteRecorder.getMessageAtIndex(1);

      assertNotEquals(firstRecordedMessage, secondRecordedMessageFromOteRecorder);
      assertEquals(secondRecordedMessage, secondRecordedMessageFromOteRecorder);
   }

   @Test(expected = ClassCastException.class)
   public void testGetMessageAtIndexClassCastException() {
      TestMessage incorrectMessageCast = oteRecorder.getMessageAtIndex(2);
   }

   @Test
   public void testGetMessageAtIndexForEmptyRecordingsList() {
      messageRecordings.clear();
      Message secondRecordedMessageFromOteRecorder = oteRecorder.getMessageAtIndex(0);

      assertNull(secondRecordedMessageFromOteRecorder);
   }

   @Test
   public void testGetFirstMessageAfterOfType() {
      TestMessage firstRecordedMessage = (TestMessage) messageRecordings.get(0);
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);
      TestOteRecorderMessage thirdRecordedMessage = (TestOteRecorderMessage) messageRecordings.get(2);
      TestMessage fourthRecordedMessage = (TestMessage) messageRecordings.get(3);

      Message firstMessageAfterSecondRecordedMessage =
         oteRecorder.getFirstMessageAfterOfType(firstRecordedMessage, secondRecordedMessage);

      assertEquals(fourthRecordedMessage, firstMessageAfterSecondRecordedMessage);
      assertNotEquals(thirdRecordedMessage, firstMessageAfterSecondRecordedMessage);
   }

   @Test
   public void testGetFirstMessageAfterOfTypeLastElement() {
      TestMessage lastRecordedMessage = (TestMessage) messageRecordings.get(messageRecordings.size() - 1);

      Message firstMessageAfterSecondRecordedMessage =
         oteRecorder.getFirstMessageAfterOfType(lastRecordedMessage, lastRecordedMessage);

      assertNull(firstMessageAfterSecondRecordedMessage);
   }

   @Test
   public void testGetAllMessagesAfterOfType() {
      TestMessage firstRecordedMessage = (TestMessage) messageRecordings.get(0);
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);

      List<Message> allTestMessagesAfterSecondRecording = messageRecordings.subList(3, messageRecordings.size());
      List<TestMessage> allTestMessagesAfterSecondRecordedMessage =
         oteRecorder.getAllMessagesAfterOfType(firstRecordedMessage, secondRecordedMessage);

      assertEquals(allTestMessagesAfterSecondRecordedMessage, allTestMessagesAfterSecondRecording);
   }

   @Test
   public void testGetAllMessagesAfterOfTypeNoExistingMessage() {
      TestOteRecorderMessage thirdRecordedMessage = (TestOteRecorderMessage) messageRecordings.get(2);

      List<TestOteRecorderMessage> allTestOteRecordedMessagesAfterThirdRecordedMessage =
         oteRecorder.getAllMessagesAfterOfType(thirdRecordedMessage, thirdRecordedMessage);

      assertTrue(allTestOteRecordedMessagesAfterThirdRecordedMessage.isEmpty());
   }

   @Test
   public void testGetAllMessagesAfterOfTypeLastElement() {
      TestMessage lastRecordedMessage = (TestMessage) messageRecordings.get(messageRecordings.size() - 1);

      List<TestMessage> allMessagesAfterLastRecordedMessage =
         oteRecorder.getAllMessagesAfterOfType(lastRecordedMessage, lastRecordedMessage);

      assertTrue(allMessagesAfterLastRecordedMessage.isEmpty());
   }

   @Test
   public void testGetCurrentOteMessagesToRecord() {
      List<Message> CurrentOteMessagesToRecord = oteRecorder.getCurrentOteMessagesToRecord();

      assertTrue(Collections.isEqual(CurrentOteMessagesToRecord, messages.stream().collect(Collectors.toList())));
   }

   @Test
   public void testClearAllMessageRecordings() {
      assertFalse(oteRecorder.getAllRecordedMessages().isEmpty());

      oteRecorder.clearAllMessageRecordings();

      assertTrue(oteRecorder.getAllRecordedMessages().isEmpty());
   }

   @Test
   public void testOteRecorderListener() {
      messageRecordings.clear();
      TestOteRecorderMessage firstMessageRecording = new TestOteRecorderMessage();
      messageRecordings.add(firstMessageRecording);
      OteRecorderListener oteRecorderListener = new OteRecorderListener(firstMessageRecording, messageRecordings);

      TestOteRecorderMessage secondMessageRecording = new TestOteRecorderMessage();
      secondMessageRecording.INT_ELEMENT_1.setValue(10);
      TestMessageData messageData = (TestMessageData) secondMessageRecording.getDefaultMessageData();

      oteRecorderListener.onDataAvailable(messageData, TestMemType.ETHERNET);

      TestOteRecorderMessage originalMessage = (TestOteRecorderMessage) messageRecordings.get(0);
      TestOteRecorderMessage originalMessageCopy = (TestOteRecorderMessage) messageRecordings.get(1);

      int originalMessageIntElementValue = originalMessage.INT_ELEMENT_1.getValue();
      int originalMessageCopyIntElementValue = originalMessageCopy.INT_ELEMENT_1.getValue();

      assertEquals(0, originalMessageIntElementValue);
      assertEquals(10, originalMessageCopyIntElementValue);
   }
}
