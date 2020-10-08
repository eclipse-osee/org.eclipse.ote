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

package org.eclipse.osee.ote.message.elements;

import java.util.Random;

import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class IntegerElementTest {

   @Test
   public void test1BitInts() {
      createTest(1);
   }

   @Test
   public void test2BitInts() {
      createTest(2);
   }

   @Test
   public void test7BitInts() {
      createTest(7);
   }

   @Test
   public void test8BitInts() {
      createTest(8);
   }

   @Test
   public void test9BitInts() {
      createTest(9);
   }

   @Test
   public void test15BitInts() {
      createTest(15);
   }

   @Test
   public void test16BitInts() {
      createTest(16);
   }

   @Test
   public void test17BitInts() {
      createTest(17);
   }

   @Test
   public void test32BitInts() {
      createTest(32);
   }

   @SuppressWarnings("deprecation")
   private void createTest(int width) {
      final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[64], 2, 64));
      IntegerElement[] e = new IntegerElement[32];
      for (int a = 0; a < 8; a++) {
         for (int i = 0; i < width; i++) {
            int count = 0;
            int j;
            // fill all bits before the first Integer Element with 1 bit elements
            for (int k = 0; k < i; k++) {
               e[count++] = new IntegerElement(null, "Element@" + k, hd, a, k, k);
            }
            for (j = i; j < 33 - width; j += width) {
               e[count++] = new IntegerElement(null, "Element@" + j, hd, a, j, j + width - 1);
            }
            // fill remaining bits with 1 bit signals
            for (int k = j; k < 32; k++) {
               e[count++] = new IntegerElement(null, "Element@" + k, hd, a, k, k);
            }
            int[] expectedVals = new int[count];
            Random r = new Random(System.currentTimeMillis());

            for (int l = 0; l <= 128; l++) {
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

   @SuppressWarnings("deprecation")
   private void generateAscending(Random r, IntegerElement[] e, int[] expectedVals, int length) {
      for (int i = 0; i < length; i++) {
         int val = r.nextInt();
         IntegerElement el = e[i];
         el.setValue(val);
         int width = el.getLsb() - el.getMsb() + 1;
         if (width < 32) {
            expectedVals[i] = val & (1 << width) - 1;
         } else {
            expectedVals[i] = val;
         }
         Assert.assertEquals(
            String.format("set/get fail on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()),
            (Integer) expectedVals[i], el.getValue());
      }
   }

   @SuppressWarnings("deprecation")
   private void generateDescending(Random r, IntegerElement[] e, int[] expectedVals, int length) {
      for (int i = length - 1; i >= 0; i--) {
         int val = r.nextInt();
         IntegerElement el = e[i];
         el.setValue(val);
         int width = el.getLsb() - el.getMsb() + 1;
         if (width < 32) {
            expectedVals[i] = val & (1 << width) - 1;
         } else {
            expectedVals[i] = val;
         }
         Assert.assertEquals(
            String.format("set/get fail on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()),
            Long.toHexString(expectedVals[i]), Long.toHexString(el.getValue()));
      }
   }

   @SuppressWarnings("deprecation")
   private void check(IntegerElement[] e, int[] expectedVals, int length) {
      for (int i = 0; i < length; i++) {
         IntegerElement el = e[i];
         Assert.assertEquals(
            String.format("corruption detect on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()),
            (Integer) expectedVals[i], el.getValue());
      }
   }
}
