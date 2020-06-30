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
public class UnsignedIntegerElementTest {

   @Test
   public void test8bit() {
      List<UnsignedIntegerElement> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[64], 2, 64));

      UnsignedIntegerElement element1 = new UnsignedIntegerElement(null, "Element1", hd1, 0, 0, 7);
      UnsignedInteger8Element element2 = new UnsignedInteger8Element(null, "Element2", hd2, 0, 0);
      UnsignedInteger8Element element3 = new UnsignedInteger8Element(null, "Element3", hd3, 0, 3);
      UnsignedInteger8Element element4 = new UnsignedInteger8Element(null, "Element4", hd4, 0, 24);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      // smallest value
      int setVal = 0; // 
      int expected = 0; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      setVal = 255;
      expected = 255;
      setAndCheck(uuts, setVal, expected);
      
      // Negative value should change to positive
      setVal = -128;
      expected = 128;
      setAndCheck(uuts, setVal, expected);
      
      setVal = -1;
      expected = 255;
      setAndCheck(uuts, setVal, expected);
      
      // Too big of a value, should wrap
      setVal = 256;
      expected = 0;
      setAndCheck(uuts, setVal, expected);

   }
   
   @Test
   public void test16bit() {
      List<UnsignedIntegerElement> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[64], 2, 64));

      UnsignedIntegerElement element1 = new UnsignedIntegerElement(null, "Element1", hd1, 0, 0, 15);
      UnsignedInteger16Element element2 = new UnsignedInteger16Element(null, "Element2", hd2, 0, 0);
      UnsignedInteger16Element element3 = new UnsignedInteger16Element(null, "Element3", hd3, 0, 2);
      UnsignedInteger16Element element4 = new UnsignedInteger16Element(null, "Element4", hd4, 0, 16);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      int setVal = 0; // smallest value 
      int expected = 0; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      setVal = 65535;
      expected = 65535;
      setAndCheck(uuts, setVal, expected);
      
      // largest value + 1 should wrap
      setVal = 65536;
      expected = 0;
      setAndCheck(uuts, setVal, expected);
      
      // Convert neg value
      setVal = -1;
      expected = 65535;
      setAndCheck(uuts, setVal, expected);
   }
   
   @Test
   public void test32bit() {
      List<UnsignedLongIntegerElement> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[64], 2, 64));

      UnsignedLongIntegerElement element1 = new UnsignedLongIntegerElement(null, "Element1", hd1, 0, 0, 31);
      UnsignedInteger32Element element2 = new UnsignedInteger32Element(null, "Element2", hd2, 0, 0);
      UnsignedInteger32Element element3 = new UnsignedInteger32Element(null, "Element3", hd3, 0, 4);
      UnsignedInteger32Element element4 = new UnsignedInteger32Element(null, "Element4", hd4, 0, 16);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      long setVal = 0; // smallest value 
      long expected = 0; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      setVal = 4294967295l;
      expected = 4294967295l;
      setAndCheck(uuts, setVal, expected);
      
      // largest value + 1 should wrap
      setVal = 4294967296l;
      expected = 0;
      setAndCheck(uuts, setVal, expected);
      
      // Convert neg value
      setVal = -1;
      expected = 4294967295l;
      setAndCheck(uuts, setVal, expected);
   }
   
   private void setAndCheck(List<UnsignedLongIntegerElement> uuts, long setVal, long expected) {
      for (UnsignedLongIntegerElement el : uuts) {
         setAndCheck(el, setVal, expected);
      }
   }

   private void setAndCheck(UnsignedLongIntegerElement element1, long setVal, long expected) {
      element1.setValue(setVal);
      check(element1, expected);
   }

   private void check(UnsignedLongIntegerElement e, long expectedVals) {
      Assert.assertEquals(
         String.format("corruption detect on %s: msb=%d, lsb=%d", e.getName(), e.getMsb(), e.getLsb()),
         (Long) expectedVals, e.getValue());
   }

   private void setAndCheck(List<UnsignedIntegerElement> uuts, int setVal, int expected) {
      for (UnsignedIntegerElement el : uuts) {
         setAndCheck(el, setVal, expected);
      }
   }

   private void setAndCheck(UnsignedIntegerElement element1, int setVal, int expected) {
      element1.setValue(setVal);
      check(element1, expected);
   }

   private void check(UnsignedIntegerElement e, int expectedVals) {
      Assert.assertEquals(
         String.format("corruption detect on %s: msb=%d, lsb=%d", e.getName(), e.getMsb(), e.getLsb()),
         (Integer) expectedVals, e.getValue());
   }
}
