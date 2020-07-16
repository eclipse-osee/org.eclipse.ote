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
      List<DiscreteElement<Byte>> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[64], 2, 64));

      SignedInteger8Element element1 = new SignedInteger8Element(null, "Element1", hd1, 2, 0, 7);
      SignedInteger8Element element2 = new SignedInteger8Element(null, "Element2", hd2, 0, 0, 7);
      SignedInteger8Element element3 = new SignedInteger8Element(null, "Element3", hd3, 0, 3, 10);
      SignedInteger8Element element4 = new SignedInteger8Element(null, "Element4", hd4, 0, 24, 31);
      
      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      byte setVal = -128; // smallest value 
      byte expected = -128; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      setVal = 127;
      expected = 127;
      setAndCheck(uuts, setVal, expected);
      
      // Set positive value but get negative
      setVal = (byte) 0xFF;
      expected = -1;
      setAndCheck(uuts, setVal, expected);

   }
   
   @Test
   public void test16bit() {
      List<DiscreteElement<Short>> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4= new HeaderData("test_data 4", new MemoryResource(new byte[64], 0, 64));

      DiscreteElement<Short> element1 = new SignedInteger16Element(null, "Element1", hd1, 0, 16, 31);
      DiscreteElement<Short> element2 = new SignedInteger16Element(null, "Element2", hd2, 0, 0, 15);
      DiscreteElement<Short> element3 = new SignedInteger16Element(null, "Element3", hd3, 0, 2, 17);
      DiscreteElement<Short> element4 = new SignedInteger16Element(null, "Element4", hd4, 2, 0, 15);

      uuts.add(element1);
      uuts.add(element2);
      uuts.add(element3);
      uuts.add(element4);

      short setVal = -32768; // smallest value 
      short expected = -32768; 
      setAndCheck(uuts, setVal, expected);
      
       // Largest value
      setVal = 32767;
      expected = 32767;
      setAndCheck(uuts, setVal, expected);
      
      // Set full scale positive value but get negative
      setVal = (short) 0xFFFF;
      expected = -1;
      setAndCheck(uuts, setVal, expected);
   }
   
   @Test
   public void test32bit() {
      List<DiscreteElement<Integer>> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[64], 2, 64));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[64], 2, 64));

      SignedInteger32Element element1 = new SignedInteger32Element(null, "Element1", hd1, 2, 0, 31);
      SignedInteger32Element element2 = new SignedInteger32Element(null, "Element2", hd2, 0, 0, 31);
      SignedInteger32Element element3 = new SignedInteger32Element(null, "Element3", hd3, 0, 4, 35);
      SignedInteger32Element element4 = new SignedInteger32Element(null, "Element4", hd4, 0, 16, 47);
      
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
      
      // Set full scale positive value but get negative
      setVal = 0xFFFFFFFF;
      expected = -1;
      setAndCheck(uuts, setVal, expected);
   }
   
   @Test
   public void test64bit() {
      List<DiscreteElement<Long>> uuts = new ArrayList<>();
      final HeaderData hd1 = new HeaderData("test_data 1", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd2 = new HeaderData("test_data 2", new MemoryResource(new byte[66], 2, 66));
      final HeaderData hd3= new HeaderData("test_data 3", new MemoryResource(new byte[64], 0, 64));
      final HeaderData hd4 = new HeaderData("test_data 4", new MemoryResource(new byte[66], 2, 66));

      SignedInteger64Element element1 = new SignedInteger64Element(null, "Element1", hd1, 0, 0, 63);
      SignedInteger64Element element2 = new SignedInteger64Element(null, "Element2", hd2, 0, 0, 63);
      SignedInteger64Element element3 = new SignedInteger64Element(null, "Element3", hd3, 0, 5, 68);
      SignedInteger64Element element4 = new SignedInteger64Element(null, "Element4", hd4, 0, 16, 79);
      
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
