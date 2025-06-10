/*********************************************************************
 * Copyright (c) 2024 Boeing
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ote.message.other;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.mock.TestMessage;
import org.eclipse.osee.ote.message.mock.UnitTestAccessor;
import org.eclipse.osee.ote.message.mock.UnitTestSupport;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dominic Leiner
 * @author Shandeep Singh
 */
public class TestMessageBitOperations {

   private final UnitTestAccessor accessor = new UnitTestAccessor();
   private final UnitTestSupport support = new UnitTestSupport();
   private TestMessage testMessage;

   @Before
   public void setupBeforeTest() {
      testMessage = new TestMessage();
      testMessage.zeroize();
   }

   @Test
   public void testSetIntElementBits() {
      IntegerElement intElement = testMessage.INT_ELEMENT_1;
      testMessage.setBits(accessor, intElement.getByteOffset(), intElement.getStartingBit(), intElement.getBitLength(),
         1);
      testMessage.INT_ELEMENT_1.getNoLog();
      assertTrue("INT_ELEMENT set to 1", testMessage.INT_ELEMENT_1.getNoLog().equals(1));
   }

   @Test
   public void testSetMultipleBits() {
      testMessage.setBits(accessor, 1, 0, 8, 63);
      assertTrue("Set 2nd byte to 63", testMessage.getData()[1] == 63);
   }

   @Test
   public void testSetFewBits() {
      testMessage.setBits(accessor, 2, 4, 4, 15);
      assertTrue("Set 3rd byte to 15 by only changing the specific 4 bits", testMessage.getData()[2] == 15);
   }

   @Test
   public void testSetBitSizeLargerThanValue() {
      testMessage.setBits(accessor, 2, 0, 8, 1);
      assertTrue("Set 3rd byte to 1 with a larger size", testMessage.getData()[2] == 1);
   }

   @Test
   public void testSetBitOffsetWithalue() {
      testMessage.setBits(accessor, 2, 6, 2, 3);
      assertTrue("Set 3rd byte to 3", testMessage.getData()[2] == 3);
   }

   @Test
   public void testSetBitSizeTooSmall() {
      testMessage.setBits(accessor, 2, 6, 2, 16);
      assertTrue("Set 3rd byte to bad value", testMessage.getData()[2] == 0);
      //Right now if the size isn't big enough for the value, it does nothing
   }

   public void testCheckBits() throws InterruptedException {
      testMessage.setBits(accessor, 1, 0, 5, 20);
      assertTrue(testMessage.checkBits(accessor, null, 1, 0, 5, 20));
   }

   @Test
   public void testCheckNotBits() throws InterruptedException {
      testMessage.setBits(accessor, 1, 0, 5, 20);
      assertFalse(testMessage.checkNotBits(accessor, null, 1, 0, 5, 20));
   }

   @Test
   public void testGetBits() throws InterruptedException {
      testMessage.setBits(accessor, 1, 0, 8, 30);
      assertEquals(30, testMessage.getBits(accessor, 1, 0, 8));
   }

   @Test
   public void testWaitForBits() throws InterruptedException {
      support.activateMsg(testMessage);
      support.genericTestCheckWaitForBits(testMessage, 0, 0, 8,
         new Long[] {new Long(1), new Long(5), new Long(100), new Long(200)}, new Long(10));
   }

}
