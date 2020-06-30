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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael P. Masterson
 */
public class SignedIntegerElementTest {

   @Test
   public void test8bit() {
      List<SignedIntegerElement> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[64], 2, 64));

      SignedIntegerElement element1 = new SignedIntegerElement(null, "Element1", hd1, 0, 0, 7);
      SignedInteger8Element element2 = new SignedInteger8Element(null, "Element2", hd2, 0, 0);
      SignedInteger8Element element3 = new SignedInteger8Element(null, "Element3", hd3, 0, 3);
      SignedInteger8Element element4 = new SignedInteger8Element(null, "Element4", hd4, 0, 24);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      int setVal = -128; // smallest value 
      int expected = -128; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      setVal = 127;
      expected = 127;
      setAndCheck(uuts, setVal, expected);
      
      // largest value + 1 should wrap to negative
      setVal = 128;
      expected = -128;
      setAndCheck(uuts, setVal, expected);
      
      // smallest value - 1
      setVal = -129;
      expected = 127;
      setAndCheck(uuts, setVal, expected);

      
      // Set positive value but get negative
      setVal = 255;
      expected = -1;
      setAndCheck(uuts, setVal, expected);

   }
   
   @Test
   public void test16bit() {
      List<SignedIntegerElement> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[64], 2, 64));

      SignedIntegerElement element1 = new SignedIntegerElement(null, "Element1", hd1, 0, 0, 15);
      SignedInteger16Element element2 = new SignedInteger16Element(null, "Element2", hd2, 0, 0);
      SignedInteger16Element element3 = new SignedInteger16Element(null, "Element3", hd3, 0, 2);
      SignedInteger16Element element4 = new SignedInteger16Element(null, "Element4", hd4, 0, 16);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      int setVal = -32768; // smallest value 
      int expected = -32768; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      setVal = 32767;
      expected = 32767;
      setAndCheck(uuts, setVal, expected);
      
      // largest value + 1 should wrap to negative
      setVal = 32768;
      expected = -32768;
      setAndCheck(uuts, setVal, expected);
      
      // smallest value - 1
      setVal = -32769;
      expected = 32767;
      setAndCheck(uuts, setVal, expected);

      
      // Set full scale positive value but get negative
      setVal = 0xFFFF;
      expected = -1;
      setAndCheck(uuts, setVal, expected);
   }
   
   @Test
   public void test32bit() {
      List<SignedIntegerElement> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[64], 2, 64));

      SignedIntegerElement element1 = new SignedIntegerElement(null, "Element1", hd1, 0, 0, 31);
      SignedInteger32Element element2 = new SignedInteger32Element(null, "Element2", hd2, 0, 0);
      SignedInteger32Element element3 = new SignedInteger32Element(null, "Element3", hd3, 0, 4);
      SignedInteger32Element element4 = new SignedInteger32Element(null, "Element4", hd4, 0, 16);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      int setVal = Integer.MIN_VALUE; // smallest value 
      int expected = Integer.MIN_VALUE; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      setVal = Integer.MAX_VALUE;
      expected = Integer.MAX_VALUE;
      setAndCheck(uuts, setVal, expected);
      
      // largest value + 1 doesn't fit in int primative so skipping
      // smallest value - 1 doesn't fit in int primative so skipping
      
      // Set full scale positive value but get negative
      setVal = 0xFFFFFFFF;
      expected = -1;
      setAndCheck(uuts, setVal, expected);
   }
   
   @Test
   public void test64bit() {
      List<SignedLongIntegerElement> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[66], 2, 66));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[66], 2, 66));

      SignedLongIntegerElement element1 = new SignedLongIntegerElement(null, "Element1", hd1, 0, 0, 63);
      SignedInteger64Element element2 = new SignedInteger64Element(null, "Element2", hd2, 0, 0);
      SignedInteger64Element element3 = new SignedInteger64Element(null, "Element3", hd3, 0, 5);
      SignedInteger64Element element4 = new SignedInteger64Element(null, "Element4", hd4, 0, 16);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      long setVal = Long.MIN_VALUE; // smallest value 
      long expected = Long.MIN_VALUE; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      setVal = Long.MAX_VALUE;
      expected = Long.MAX_VALUE;
      setAndCheck(uuts, setVal, expected);
      
      setVal = 0;
      expected = 0;
      setAndCheck(uuts, setVal, expected);
      
      setVal = 1;
      expected = 1;
      setAndCheck(uuts, setVal, expected);
      
      
      // Set full scale positive value but get negative
      setVal = 0xFFFFFFFFFFFFFFFFl;
      expected = -1;
      setAndCheck(uuts, setVal, expected);
   }
   
   private void setAndCheck(List<SignedLongIntegerElement> uuts, long setVal, long expected) {
      for (SignedLongIntegerElement el : uuts) {
         setAndCheck(el, setVal, expected);
      }
   }

   private void setAndCheck(SignedLongIntegerElement element1, long setVal, long expected) {
      element1.setValue(setVal);
      check(element1, expected);
   }

   private void check(SignedLongIntegerElement e, long expectedVals) {
      Assert.assertEquals(
         String.format("corruption detect on %s: msb=%d, lsb=%d", e.getName(), e.getMsb(), e.getLsb()),
         (Long) expectedVals, e.getValue());
   }

   private void setAndCheck(List<SignedIntegerElement> uuts, int setVal, int expected) {
      for (SignedIntegerElement el : uuts) {
         setAndCheck(el, setVal, expected);
      }
   }

   private void setAndCheck(SignedIntegerElement element1, int setVal, int expected) {
      element1.setValue(setVal);
      check(element1, expected);
   }

   private void check(SignedIntegerElement e, int expectedVals) {
      Assert.assertEquals(
         String.format("corruption detect on %s: msb=%d, lsb=%d", e.getName(), e.getMsb(), e.getLsb()),
         (Integer) expectedVals, e.getValue());
   }
}
