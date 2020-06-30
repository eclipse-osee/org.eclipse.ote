/*********************************************************************
 * Copyright (c) 2020 Boeing
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael P. Masterson
 */
public class UnsignedInteger64ElementTest {

   @Test
   public void test64bit() {
      List<UnsignedInteger64Element> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[66], 0, 66));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[66], 2, 66));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[66], 2, 66));

      UnsignedInteger64Element element1 = new UnsignedInteger64Element(null, "Element1", hd1, 2, 3);
      UnsignedInteger64Element element2 = new UnsignedInteger64Element(null, "Element2", hd2, 0, 0);
      UnsignedInteger64Element element3 = new UnsignedInteger64Element(null, "Element3", hd3, 0, 5);
      UnsignedInteger64Element element4 = new UnsignedInteger64Element(null, "Element4", hd4, 3, 0);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      long setValLong = 0; // smallest value 
      long expectedLong = 0; 
//      setAndCheck(uuts, setValLong, expectedLong);
      
       // Largest value
      BigInteger largestAsBigInt = new BigInteger("18446744073709551615");
      BigInteger setVal = largestAsBigInt;
      BigInteger expected = largestAsBigInt;
      setAndCheck(uuts, setVal, expected);
      
      setValLong = 1;
      expectedLong = 1;
      setAndCheck(uuts, setValLong, expectedLong);
      
      
      // Set full scale positive value but get negative
      setVal = new BigInteger("FFFFFFFFFFFFFFFF", 16);
      expected = largestAsBigInt;
      setAndCheck(uuts, setVal, expected); 
      
      setVal = new BigInteger("-1");
      expected = largestAsBigInt;
      setAndCheck(uuts, setVal, expected);
      
   }
   
   private void setAndCheck(List<UnsignedInteger64Element> uuts, long setVal, long expected) {
      setAndCheck(uuts, BigInteger.valueOf(setVal), BigInteger.valueOf(expected));
   }
   
   private void setAndCheck(List<UnsignedInteger64Element> uuts, BigInteger setVal, BigInteger expected) {
      for (UnsignedInteger64Element el : uuts) {
         setAndCheck(el, setVal, expected);
      }
   }

   private void setAndCheck(UnsignedInteger64Element element1, BigInteger setVal, BigInteger expected) {
      element1.setNoLog(setVal);
      check(element1, expected);
   }

   private void check(UnsignedInteger64Element e, BigInteger expectedVal) {
      BigInteger actual = e.getValue();
      Assert.assertEquals(
         String.format("corruption detect on %s: msb=%d, lsb=%d, hexExp=%X, hexActual=%X", e.getName(), e.getMsb(), e.getLsb(), expectedVal.longValue(), actual.longValue()),
         expectedVal.longValue(), actual.longValue());
   }
   
   @Test
   public void testExtensive() {
      createLongIntTest(64);
   }
   
   private void createLongIntTest(int width) {
      Element[] e = new Element[64];
      for (int a = 0; a < 4; a++) {
         final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[64], a, 64));
         for (int i = 0; i < width; i++) {
            int count = 0;
            int j;
            for (int k = 0; k < i; k++) {
               e[count++] = new LongIntegerElement(null, "Element@" + k, hd, 0, k, k);
            }
            for (j = i; j < 65 - width; j += width) {
               e[count++] = new UnsignedInteger64Element(null, "Element@" + j, hd, 0, j);
            }
            // fill remaining bits with 1 bit signals
            for (int k = j; k < 64; k++) {
               e[count++] = new LongIntegerElement(null, "Element@" + k, hd, 0, k, k);
            }
            long[] expectedVals = new long[count];
            Random r = new Random(System.currentTimeMillis());

            for (int l = 0; l <= 256; l++) {
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

   private void generateAscending(Random r, Element[] e, long[] expectedVals, int length) {
      for (int i = 0; i < length; i++) {
         long val = r.nextLong();
         Element unassignedEl = e[i];
         if(unassignedEl instanceof UnsignedInteger64Element) {
            UnsignedInteger64Element el = (UnsignedInteger64Element) unassignedEl;
            generateUnsigned64(el, expectedVals, val, i);
         } else {
            LongIntegerElement el = (LongIntegerElement) unassignedEl;
            generateLongInteger(el, expectedVals, val, i);
         }
      }
   }

   /**
    * @param unassignedEl
    * @param expectedVals
    * @param val
    * @param i
    */
   private void generateUnsigned64(UnsignedInteger64Element el, long[] expectedVals, long val, int i) {
      el.setValue(BigInteger.valueOf(val));
      int width = el.getLsb() - el.getMsb() + 1;
      if (width < 64) {
         expectedVals[i] = val & (1L << width) - 1L;
      } else {
         expectedVals[i] = val;
      }
      long expectedValue = expectedVals[i];
      String expected = Long.toHexString(expectedValue);
      String actual = el.getValue().toString(16);
      Assert.assertEquals(
         String.format("set/get fail on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()),
         expected, actual);
   }
   
   private void generateLongInteger(LongIntegerElement el, long[] expectedVals, long val, int i) {
      el.setValue(val);
      int width = el.getLsb() - el.getMsb() + 1;
      if (width < 64) {
         expectedVals[i] = val & (1L << width) - 1L;
      } else {
         expectedVals[i] = val;
      }
      long expectedValue = expectedVals[i];
      Assert.assertEquals(
         String.format("set/get fail on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()),
         Long.toHexString(expectedValue), Long.toHexString(el.getValue()));
   }

   private void generateDescending(Random r, Element[] e, long[] expectedVals, int length) {
      for (int i = length - 1; i >= 0; i--) {
         long val = r.nextLong();
         Element unassignedEl = e[i];
         if(unassignedEl instanceof UnsignedInteger64Element) {
            UnsignedInteger64Element el = (UnsignedInteger64Element) unassignedEl;
            generateUnsigned64(el, expectedVals, val, i);
         } else {
            LongIntegerElement el = (LongIntegerElement) unassignedEl;
            generateLongInteger(el, expectedVals, val, i);
         }
      }
   }

   private void check(Element[] e, long[] expectedVals, int length) {
      for (int i = 0; i < length; i++) {
         long expected = expectedVals[i];
         Element unassignedEl = e[i];
         long actual;
         if(unassignedEl instanceof UnsignedInteger64Element) {
            UnsignedInteger64Element el = (UnsignedInteger64Element) unassignedEl;
            actual = el.getValue().longValue();
         } else {
            LongIntegerElement el = (LongIntegerElement) unassignedEl;
            actual = el.getValue();
         }
         Assert.assertEquals(
            String.format("corruption detect on %s: msb=%d, lsb=%d, hexExp=%X, hexActual=%X", unassignedEl.getName(), unassignedEl.getMsb(), unassignedEl.getLsb(), expected, actual),
         expected, actual);
      }
   }

}
