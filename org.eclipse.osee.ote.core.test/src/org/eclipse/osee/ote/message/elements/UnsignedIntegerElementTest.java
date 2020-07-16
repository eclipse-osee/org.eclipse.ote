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
      List<DiscreteElement<Byte>> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[64], 2, 64));

      UnsignedInteger8Element element1 = new UnsignedInteger8Element(null, "Element1", hd1, 3, 0, 6);
      UnsignedInteger8Element element2 = new UnsignedInteger8Element(null, "Element2", hd2, 0, 0, 6);
      UnsignedInteger8Element element3 = new UnsignedInteger8Element(null, "Element3", hd3, 0, 3, 9);
      UnsignedInteger8Element element4 = new UnsignedInteger8Element(null, "Element4", hd4, 0, 24, 30);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      // smallest value
      byte setVal = 0; // 
      byte expected = 0; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      setVal = 127;
      expected = 127;
      setAndCheck(uuts, setVal, expected);
      
      // This negative value is one bit too large for the 7 bit field, should go to zero
      setVal = -128;
      expected = 0;
      setAndCheck(uuts, setVal, expected);
      
      setVal = -1;
      expected = 127;
      setAndCheck(uuts, setVal, expected);
   }
   
   @Test
   public void test16bit() {
      List<DiscreteElement<Short>> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[64], 2, 64));

      UnsignedInteger16Element element1 = new UnsignedInteger16Element(null, "Element1", hd1, 0, 0, 14);
      UnsignedInteger16Element element2 = new UnsignedInteger16Element(null, "Element2", hd2, 3, 0, 14);
      UnsignedInteger16Element element3 = new UnsignedInteger16Element(null, "Element3", hd3, 0, 2, 16);
      UnsignedInteger16Element element4 = new UnsignedInteger16Element(null, "Element4", hd4, 0, 16, 30);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      short setVal = 0; // smallest value 
      short expected = 0; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      setVal = 32767;
      expected = 32767;
      setAndCheck(uuts, setVal, expected);
      
      // largest value + 1 should wrap
      setVal = (short) 0x8000;
      expected = 0;
      setAndCheck(uuts, setVal, expected);
      
      // Convert neg value
      setVal = -1;
      expected = 32767;
      setAndCheck(uuts, setVal, expected);
   }
   
   @Test
   public void test32bit() {
      List<DiscreteElement<Integer>> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[64], 2, 64));

      UnsignedInteger32Element element1 = new UnsignedInteger32Element(null, "Element1", hd1, 3, 0, 30);
      UnsignedInteger32Element element2 = new UnsignedInteger32Element(null, "Element2", hd2, 0, 0, 30);
      UnsignedInteger32Element element3 = new UnsignedInteger32Element(null, "Element3", hd3, 0, 4, 34);
      UnsignedInteger32Element element4 = new UnsignedInteger32Element(null, "Element4", hd4, 0, 16, 46);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      int setVal = 0; // smallest value 
      int expected = 0; 
      setAndCheck(uuts, setVal, expected);
      
      // Largest value
      setVal = 2147483647;
      expected = 2147483647;
      setAndCheck(uuts, setVal, expected);
      
      // largest value + 1 should wrap
      setVal = 0x80000000;
      expected = 0;
      setAndCheck(uuts, setVal, expected);
      
      // Convert neg value
      setVal = -1;
      expected = 2147483647;
      setAndCheck(uuts, setVal, expected);
   }
   
   @Test
   public void test64bit() {
      List<DiscreteElement<Long>> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[66], 0, 66));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[66], 2, 66));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[66], 2, 66));

      UnsignedInteger64Element element1 = new UnsignedInteger64Element(null, "Element1", hd1, 2, 3, 65);
      UnsignedInteger64Element element2 = new UnsignedInteger64Element(null, "Element2", hd2, 0, 0, 62);
      UnsignedInteger64Element element3 = new UnsignedInteger64Element(null, "Element3", hd3, 0, 5, 67);
      UnsignedInteger64Element element4 = new UnsignedInteger64Element(null, "Element4", hd4, 3, 0, 62);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      long setVal = 0; // smallest value 
      long expected = 0; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      long largestValue = 9223372036854775807l;
      setVal = largestValue;
      expected = largestValue;
      setAndCheck(uuts, setVal, expected);
      
      setVal = 1;
      expected = 1;
      setAndCheck(uuts, setVal, expected);
      
      // out of range should wrap
      setVal = 0xFFFFFFFFFFFFFFFFl;
      expected = largestValue;
      setAndCheck(uuts, setVal, expected);
   }
   
   private <T extends Comparable<T>> void setAndCheck(List<DiscreteElement<T>> uuts, T setVal, T expected) {
      for (DiscreteElement<T> el : uuts) {
         setAndCheck(el, setVal, expected);
      }
   }

   private <T extends Comparable<T>> void setAndCheck(DiscreteElement<T> element1, T setVal, T expected) {
      element1.setValue(setVal);
      check(element1, expected);
   }

   private <T extends Comparable<T>> void check(DiscreteElement<T> e, T expectedVals) {
      Assert.assertEquals(
         String.format("corruption detect on %s: msb=%d, lsb=%d", e.getName(), e.getMsb(), e.getLsb()),
         expectedVals, e.getValue());
   }
}
