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
package org.eclipse.ote.io;


import org.junit.Assert;
import org.junit.Test;

public class CircularBufferTest {

   @Test
   public void test() {
      
      CircularBuffer<Integer> buffer = new CircularBuffer<Integer>(5);
      
      for(int i = 1; i <= 5; i++){
         buffer.add(i);
      }
      
      for(int i = 1; i <= 5; i++){
         Assert.assertEquals(i, buffer.remove().intValue());
      }
      
      Assert.assertNull(buffer.remove());
   
   }
   
   @Test
   public void testHead() {
      
      CircularBuffer<Integer> buffer = new CircularBuffer<Integer>(5);
      
      for(int i = 1; i <= 5; i++){
         buffer.add(i);
      }
      
      Assert.assertEquals(5, buffer.head().intValue());
      
   }
   
   @Test
   public void testWrap() {

      CircularBuffer<Integer> buffer = new CircularBuffer<Integer>(5);
      
      for(int i = 1; i <= 10; i++){
         buffer.add(i);
      }
      for(int i = 6; i <= 10; i++){
         Assert.assertEquals(i, buffer.remove().intValue());
      }
      
      buffer.clear();
      for(int i = 1; i <= 20; i++){
         buffer.add(i);
      }
      for(int i = 16; i <= 20; i++){
         Assert.assertEquals(i, buffer.remove().intValue());
      }
      for(int i = 21; i <= 23; i++){
         buffer.add(i);
      }
      
      Integer[] copy = new Integer[5];
      copy = buffer.getCopy(Integer.class);
      Assert.assertEquals(3, copy.length);
      for(int i = 0; i < copy.length; i++){
         Assert.assertEquals(21+i, copy[i].intValue());   
      }
      
      buffer.clear();
      
      for(int i = 1; i <= 8; i++){
         buffer.add(i);
      }
      copy = new Integer[5];
      copy = buffer.getCopy(Integer.class);
      Assert.assertEquals(5, copy.length);
      for(int i = 0; i < copy.length; i++){
         Assert.assertEquals(4+i, copy[i].intValue());   
      }
   }

   @Test
   public void testArrayAdd() {
      CircularBuffer<Integer> buffer = new CircularBuffer<Integer>(20);
      
      addArray(buffer, 1, 1, 6);
      addArray(buffer, 7, 1, 12);
      addArray(buffer, 13, 1, 18);
      addArray(buffer, 19, 5, 20);
      addArray(buffer, 25, 11, 20);
      addArray(buffer, 31, 17, 20);
      addArray(buffer, 37, 23, 20);
      addArray(buffer, 43, 29, 20);
      
   }
   
   @Test
   public void testArrayAdd2() {
      CircularBuffer<Integer> buffer = new CircularBuffer<Integer>(6);
      
      addArray(buffer, 1, 1, 6);
      addArray(buffer, 7, 7, 6);
      addArray(buffer, 13, 13, 6);
      addArray(buffer, 19, 19, 6);
      addArray(buffer, 25, 25, 6);
      addArray(buffer, 31, 31, 6);
      addArray(buffer, 37, 37, 6);
      addArray(buffer, 43, 43, 6);
      
   }
   
   @Test
   public void testException() {
      CircularBuffer<Integer> buffer = new CircularBuffer<Integer>(4);
      boolean exception = false;
      try{
         addArray(buffer, 1, 1, 6);
      } catch (IllegalArgumentException ex){
         exception = true;
      }
      Assert.assertTrue(exception);
   }
   
   private void addArray(CircularBuffer<Integer> buffer, int offset, int offset2, int size){
      Integer[] newData = new Integer[6];
      for(int i = 0; i < newData.length; i++){
         newData[i] = i+offset;
      }
      
      Integer[] older = buffer.add(newData, 0, newData.length);
      if(size < buffer.getTotalSize()){
         Assert.assertEquals(0, older.length);
      } else {
         Assert.assertEquals(Math.min((offset+(newData.length-1)) - buffer.getTotalSize(), 6),older.length);
         int olderOffset = Math.max(1, offset2-older.length);
         for(int i = 0; i < older.length; i++){
            Assert.assertEquals(i + olderOffset, older[i].intValue());   
         }
      }
      Integer[] copy = buffer.getCopy(Integer.class);
      Assert.assertEquals(size, copy.length);
      for(int i = 0; i < copy.length; i++){
         Assert.assertEquals(i + offset2, copy[i].intValue());   
      }
   }
}
