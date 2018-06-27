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
package org.eclipse.osee.ote.message.elements;

import java.util.Random;
import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.LongIntegerElement;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class LongIntegerElementTest {
   @Test
   public void test1BitInts() {
      createLongIntTest(1);
   }

   @Test
   public void test2BitInts() {
      createLongIntTest(2);
   }

   @Test
   public void test3BitInts() {
      createLongIntTest(3);
   }

   @Test
   public void test4BitInts() {
      createLongIntTest(4);
   }

   @Test
   public void test5BitInts() {
      createLongIntTest(5);
   }

   @Test
   public void test8BitInts() {
      createLongIntTest(8);
   }

   @Test
   public void test10BitInts() {
      createLongIntTest(10);
   }

   @Test
   public void test12BitInts() {
      createLongIntTest(12);
   }

   @Test
   public void test16BitInts() {
      createLongIntTest(16);
   }

   @Test
   public void test20BitInts() {
      createLongIntTest(20);
   }

   @Test
   public void test22BitInts() {
      createLongIntTest(22);
   }

   @Test
   public void test24BitInts() {
      createLongIntTest(24);
   }

   @Test
   public void test25BitInts() {
      createLongIntTest(25);
   }

   @Test
   public void test26BitInts() {
      createLongIntTest(26);
   }

   @Test
   public void test27BitInts() {
      createLongIntTest(27);
   }

   @Test
   public void test30BitInts() {
      createLongIntTest(30);
   }

   @Test
   public void test31BitInts() {
      createLongIntTest(31);
   }

   @Test
   public void test32BitInts() {
      createLongIntTest(32);
   }

   @Test
   public void test40BitInts() {
      createLongIntTest(40);
   }

   @Test
   public void test48BitInts() {
      createLongIntTest(48);
   }

   @Test
   public void test50BitInts() {
      createLongIntTest(50);
   }

   @Test
   public void test64BitInts() {
      createLongIntTest(64);
   }

   private void createLongIntTest(int width) {
      LongIntegerElement[] e = new LongIntegerElement[64];
      for (int a = 0; a < 4; a++) {
         final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[64], a, 64));
         for (int i = 0; i < width; i++) {
            int count = 0;
            int j;
            for (int k = 0; k < i; k++) {
               e[count++] = new LongIntegerElement(null, "Element@" + k, hd, 0, k, k);
            }
            for (j = i; j < 65 - width; j += width) {
               e[count++] = new LongIntegerElement(null, "Element@" + j, hd, 0, j, j + width - 1);
            }
            // fill remaining bits with 1 bit signals
            for (int k = j; k < 64; k++) {
               e[count++] = new LongIntegerElement(null, "Element@" + k, hd, 0, k, k);
            }
            long[] expectedVals = new long[count];
            Random r = new Random(System.currentTimeMillis());

            for (int l = 0; l <= 1024; l++) {
               /*
                * perform sets going through the array. We do this so that we can catch sets that modified bits before
                * the element
                */
               generateAscending(r, e, expectedVals, count);
               check(e, expectedVals, count);

               /*
                * perform sets going backwards through the array. We do this so that we can catch sets that modified
                * bits after the element
                */
               generateDescending(r, e, expectedVals, count);
               check(e, expectedVals, count);

               // zeroize test
               for (int z = 0; z < count; z += 2) {
                  e[z].zeroize();
                  expectedVals[z] = 0;
               }

               check(e, expectedVals, count);
            }
         }
      }
   }

   private void generateAscending(Random r, LongIntegerElement[] e, long[] expectedVals, int length) {
      for (int i = 0; i < length; i++) {
         long val = r.nextLong();
         LongIntegerElement el = e[i];
         el.setValue(val);
         int width = el.getLsb() - el.getMsb() + 1;
         if (width < 64) {
            expectedVals[i] = val & (1L << width) - 1L;
         } else {
            expectedVals[i] = val;
         }
         Assert.assertEquals(
            String.format("set/get fail on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()),
            Long.toHexString(expectedVals[i]), Long.toHexString(el.getValue()));
      }
   }

   private void generateDescending(Random r, LongIntegerElement[] e, long[] expectedVals, int length) {
      for (int i = length - 1; i >= 0; i--) {
         long val = r.nextLong();
         LongIntegerElement el = e[i];
         el.setValue(val);
         int width = el.getLsb() - el.getMsb() + 1;
         if (width < 64) {
            expectedVals[i] = val & (1l << width) - 1l;
         } else {
            expectedVals[i] = val;
         }
         Assert.assertEquals(
            String.format("set/get fail on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()),
            (Long) expectedVals[i], el.getValue());
      }
   }

   private void check(LongIntegerElement[] e, long[] expectedVals, int length) {
      for (int i = 0; i < length; i++) {
         LongIntegerElement el = e[i];
         Assert.assertEquals(
            String.format("corruption detect on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()),
            (Long) expectedVals[i], e[i].getValue());
      }
   }

}
