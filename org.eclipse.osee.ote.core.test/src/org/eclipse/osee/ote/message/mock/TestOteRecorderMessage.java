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

package org.eclipse.osee.ote.message.mock;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.elements.Float32Element;
import org.eclipse.osee.ote.message.elements.IRecordFactory;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.elements.RecordMap;
import org.eclipse.osee.ote.message.elements.StringElement;

/**
 * @author Shandeep Singh
 */
public class TestOteRecorderMessage extends Message {

   public final IntegerElement INT_ELEMENT_1;
   public final StringElement STRING_ELEMENT_1;
   public final EnumeratedElement<TestEnum> ENUM_ELEMENT_1;
   public final Float32Element FLOAT32_ELEMENT_1;
   public final RecordMap<TestRecordElement> RECORD_MAP_1;

   public TestOteRecorderMessage() {
      super("TEST_RECORDED_MSG", 100, 0, true, 0, 50.0);
      TestMessageData ethData = new TestMessageData(this.getClass().getName(), getName(), getDefaultByteSize(),
         getDefaultOffset(), TestMemType.ETHERNET);
      new TestMessageData(this.getClass().getName(), getName(), getDefaultByteSize(), getDefaultOffset(),
         TestMemType.SERIAL);
      setDefaultMessageData(ethData);
      INT_ELEMENT_1 = new IntegerElement(this, "INT_ELEMENT_1", ethData, 0, 0, 15);
      STRING_ELEMENT_1 = new StringElement(this, "STRING_ELEMENT_1", ethData, 2, 0, 159);
      ENUM_ELEMENT_1 = new EnumeratedElement<>(this, "ENUM_ELEMENT_1", TestEnum.class, ethData, 22, 0, 7);
      FLOAT32_ELEMENT_1 = new Float32Element(this, "FLOAT32_ELEMENT_1", ethData, 23, 0, 31);
      RECORD_MAP_1 =
         new RecordMap<TestRecordElement>(this, ethData, "RECORD_MAP_1", 2, new RECORD_MAP_1_factory(ethData));
      addElements(INT_ELEMENT_1, STRING_ELEMENT_1, ENUM_ELEMENT_1, FLOAT32_ELEMENT_1, RECORD_MAP_1);
      setMemSource(TestMemType.ETHERNET);
   }

   public class RECORD_MAP_1_factory implements IRecordFactory {
      private final MessageData data;

      public RECORD_MAP_1_factory(MessageData data) {
         this.data = data;
      }

      @Override
      public RecordElement create(int i) {
         TestRecordElement temp =
            new TestRecordElement(TestOteRecorderMessage.this, "RECORD_MAP_1", i, getBitOffset() + 0, this.data);
         temp.addPath(RECORD_MAP_1.getElementPath().toArray());
         return temp;
      }

      @Override
      public int getBitLength() {
         return 512;
      }

   }
}