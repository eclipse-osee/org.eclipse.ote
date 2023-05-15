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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.OteRecorder;
import org.eclipse.osee.ote.message.mock.TestMessage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Shandeep Singh
 */
public class TestOteRecorder {
   private static List<Message> messages = new ArrayList<>();
   private static List<Message> messageRecordings = new ArrayList<>();;
   private OteRecorder oteRecorder;

   @BeforeClass
   public static void setUpBeforeClass() {
      TestMessage oteMessage = new TestMessage();
      TestMessage oteMessag2 = new TestMessage();
      messages.add(oteMessage);
      messages.add(oteMessag2);
   }

   @Before
   public void setupBeforeTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
      messageRecordings.clear();
      TestMessage messageRecording = new TestMessage();
      TestMessage messageRecording2 = new TestMessage();
      TestMessage messageRecording3 = new TestMessage();
      TestMessage messageRecording4 = new TestMessage();
      messageRecordings.add(messageRecording);
      messageRecordings.add(messageRecording2);
      messageRecordings.add(messageRecording3);
      messageRecordings.add(messageRecording4);

      oteRecorder = new OteRecorder(messages);
      Field messageRecordingsField = OteRecorder.class.getDeclaredField("messageRecordings");
      messageRecordingsField.setAccessible(true);

      messageRecordingsField.set(oteRecorder, messageRecordings);
   }

   @Test
   public void testStartRecording() {
      boolean firstStart = oteRecorder.start();
      boolean secondStart = oteRecorder.start();

      assertEquals(firstStart, true);
      assertEquals(secondStart, false);
   }

   @Test
   public void testStartRecordingAfterPause() {
      boolean firstStart = oteRecorder.start();
      boolean firstPause = oteRecorder.pause();
      boolean startAfterPause = oteRecorder.start();

      assertEquals(firstStart, true);
      assertEquals(firstPause, true);
      assertEquals(startAfterPause, true);
   }

   @Test
   public void testPauseRecording() {
      boolean firstStart = oteRecorder.start();
      boolean firstPause = oteRecorder.pause();
      boolean secondPause = oteRecorder.pause();

      assertEquals(firstStart, true);
      assertEquals(firstPause, true);
      assertEquals(secondPause, false);
   }

   @Test
   public void testPauseRecordingWithoutStart() {
      boolean firstPause = oteRecorder.pause();

      assertEquals(firstPause, false);
   }

   @Test
   public void testAddMessageToRecorder() {
      oteRecorder.start();

      List<?> allMessagesToRecord = oteRecorder.getCurrentOteMessagesToRecord();
      assertEquals(allMessagesToRecord.size(), 2);

      TestMessage messageToAdd = new TestMessage();
      oteRecorder.addMessageToRecorder(messageToAdd);
      List<?> allMessagesToRecordAfterAdding = oteRecorder.getCurrentOteMessagesToRecord();
      assertEquals(allMessagesToRecordAfterAdding.size(), 3);
   }

   @Test
   public void testRemoveMessageFromRecorder() {
      oteRecorder.start();

      List<?> allMessagesToRecord = oteRecorder.getCurrentOteMessagesToRecord();
      assertEquals(allMessagesToRecord.size(), 2);

      TestMessage messageToRemove = (TestMessage) allMessagesToRecord.get(0);
      oteRecorder.removeMessageFromRecorder(messageToRemove);
      List<?> allMessagesToRecordAfterRemoving = oteRecorder.getCurrentOteMessagesToRecord();
      assertEquals(allMessagesToRecordAfterRemoving.size(), 1);
   }

   @Test
   public void testGetAllRecordedMessages() {
      List<?> allRecordedMessages = oteRecorder.getAllRecordedMessages();

      assertEquals(allRecordedMessages.size(), 4);
   }

   @Test
   public void testGetAllMessagesOfType() {
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);
      List<?> allMessagesOfTestMessageType = oteRecorder.getAllMessagesOfType(secondRecordedMessage);

      assertEquals(allMessagesOfTestMessageType.size(), 4);
   }

   @Test
   public void testGetFirstIndexOf() {
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);
      int firstInstanceOfTestMessage = oteRecorder.getFirstIndexOf(secondRecordedMessage);

      assertEquals(firstInstanceOfTestMessage, 0);
   }

   @Test
   public void testGetFirstMessageOfType() {
      TestMessage firstRecordedMessage = (TestMessage) messageRecordings.get(0);
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);
      TestMessage firstMessageOfFirstRecordedMessage = oteRecorder.getFirstMessageOfType(firstRecordedMessage);
      TestMessage firstMessageOfSecondRecordedMessage = oteRecorder.getFirstMessageOfType(secondRecordedMessage);

      assertEquals(firstMessageOfFirstRecordedMessage, firstRecordedMessage);
      assertNotEquals(firstMessageOfSecondRecordedMessage, secondRecordedMessage);
   }

   @Test
   public void testGetMessageAtIndex() {
      TestMessage firstRecordedMessage = (TestMessage) messageRecordings.get(0);
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);
      Message secondRecordedMessageFromOteRecorder = oteRecorder.getMessageAtIndex(1);

      assertNotEquals(secondRecordedMessageFromOteRecorder, firstRecordedMessage);
      assertEquals(secondRecordedMessageFromOteRecorder, secondRecordedMessage);
   }

   @Test
   public void testGetMessageAtIndexForEmptyRecordingsList() {
      messageRecordings.clear();
      Message secondRecordedMessageFromOteRecorder = oteRecorder.getMessageAtIndex(0);

      assertEquals(secondRecordedMessageFromOteRecorder, null);
   }

   @Test
   public void testGetFirstMessageAfterOfType() {
      TestMessage firstRecordedMessage = (TestMessage) messageRecordings.get(0);
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);
      TestMessage thirdRecordedMessage = (TestMessage) messageRecordings.get(2);

      Message firstMessageAfterSecondRecordedMessage =
         oteRecorder.getFirstMessageAfterOfType(firstRecordedMessage, secondRecordedMessage);

      assertEquals(firstMessageAfterSecondRecordedMessage, thirdRecordedMessage);
      assertNotEquals(firstMessageAfterSecondRecordedMessage, secondRecordedMessage);
   }

   @Test
   public void testGetFirstMessageAfterOfTypeLastElement() {
      TestMessage lastRecordedMessage = (TestMessage) messageRecordings.get(messageRecordings.size() - 1);

      Message firstMessageAfterSecondRecordedMessage =
         oteRecorder.getFirstMessageAfterOfType(lastRecordedMessage, lastRecordedMessage);

      assertEquals(firstMessageAfterSecondRecordedMessage, null);
   }

   @Test
   public void testGetAllMessagesAfterOfType() {
      TestMessage firstRecordedMessage = (TestMessage) messageRecordings.get(0);
      TestMessage secondRecordedMessage = (TestMessage) messageRecordings.get(1);

      List<Message> allMessagesAfterSecondRecording = messageRecordings.subList(2, messageRecordings.size());
      List<TestMessage> allMessagesAfterSecondRecordedMessage =
         oteRecorder.getAllMessagesAfterOfType(firstRecordedMessage, secondRecordedMessage);

      assertEquals(allMessagesAfterSecondRecording, allMessagesAfterSecondRecordedMessage);
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
      List<?> CurrentOteMessagesToRecord = oteRecorder.getCurrentOteMessagesToRecord();

      assertEquals(CurrentOteMessagesToRecord, messages);
   }

   @Test
   public void testClearAllMessageRecordings() {
      assertTrue(!oteRecorder.getAllRecordedMessages().isEmpty());

      oteRecorder.clearAllMessageRecordings();
      assertTrue(oteRecorder.getAllRecordedMessages().isEmpty());
   }
}
