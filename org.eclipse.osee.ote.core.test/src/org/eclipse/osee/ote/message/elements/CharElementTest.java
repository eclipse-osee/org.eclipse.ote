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
package org.eclipse.osee.ote.message.elements;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CharElementTest {

   public class TestMessage extends Message {
      public TestMessage(String name, int defaultByteSize, int defaultOffset, boolean isScheduled, int phase, double rate) {
         super(name, defaultByteSize, defaultOffset, isScheduled, phase, rate);
      }
   }

   @Mock
   private TestMessage message;
   @Mock
   private MessageData msgData;
   private final byte[] bytes = new byte[5];
   private final MemoryResource memoryResource = new MemoryResource(bytes, 0, bytes.length);

   @Before
   public void before() {
      MockitoAnnotations.initMocks(this);
      when(message.getData()).thenReturn(bytes);
      when(message.getName()).thenReturn("MSG");
      when(msgData.getMem()).thenReturn(memoryResource);
   }

   @Test
   public void parseAndSetWithByteAlignedElementTest() throws Exception {
      CharElement sut = new CharElement(message, "TEST", msgData, 0, 0, 7);
      assertEquals("Start with all zeros in the byte array", "[0, 0, 0, 0, 0]",
         Arrays.toString(memoryResource.getData()));
      sut.parseAndSet(null, " ");
      assertEquals("getValue should return space character", ' ', sut.getValue().charValue());
      sut.parseAndSet(null, "A");
      assertEquals("getValue should return 'A'", 'A', sut.getValue().charValue());
      sut.parseAndSet(null, "");
      assertEquals("getValue should return null character", '\0', sut.getValue().charValue());
      sut.parseAndSet(null, "FOO");
      assertEquals("Decimal ASCII values for FOO", "[70, 79, 79, 0, 0]", Arrays.toString(memoryResource.getData()));
      assertEquals("FOO should be returned by the getString method", "FOO", sut.getString(null, 5));
   }

   @Test
   public void parseAndSetWithNonByteAlignedElementTest() throws Exception {
      CharElement sut = new CharElement(message, "TEST1", msgData, 0, 1, 7);
      List<Element> list = new ArrayList<>();
      list.add(sut);
      list.add(new CharElement(message, "TEST2", msgData, 1, 1, 7));
      list.add(new CharElement(message, "TEST3", msgData, 2, 1, 7));
      list.add(new CharElement(message, "TEST4", msgData, 3, 1, 7));
      list.add(new CharElement(message, "TEST5", msgData, 4, 1, 7));

      when(message.getElementIterator(sut)).thenReturn(list.listIterator());
      assertEquals("Start with all zeros in the byte array", "[0, 0, 0, 0, 0]",
         Arrays.toString(memoryResource.getData()));
      Arrays.fill(bytes, (byte) 0xFF);
      sut.parseAndSet(null, " ");
      assertEquals("getValue should return space character", ' ', sut.getValue().charValue());
      sut.parseAndSet(null, "A");
      assertEquals("getValue should return 'A'", 'A', sut.getValue().charValue());
      sut.parseAndSet(null, "");
      assertEquals("getValue should return null character", '\0', sut.getValue().charValue());
      sut.parseAndSet(null, "FOO");
      
      when(message.getElementIterator(sut)).thenReturn(list.listIterator());
      assertEquals("getString should return 'FOO' ", "FOO", sut.getString(null, 3));
      // We set the entire array to 0xFF above and since the first bit is "skipped"
      // when setting the ascii, the values are negative
      assertEquals("Unfortunately we tromple the spare bit data", "[-58, -49, -49, -1, -1]",
         Arrays.toString(memoryResource.getData()));
   }

   @Test(expected = IllegalArgumentException.class)
   public void parseAndSetOverflowExceptionTest() throws Exception {
      CharElement sut = new CharElement(message, "TEST", msgData, 0, 0, 7);
      sut.parseAndSet(null, "TOOLONG");
   }
}
