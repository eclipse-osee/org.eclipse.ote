package org.eclipse.osee.ote.message.data;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

public class MemoryResourceTest {

   @Test
   public void testcopyDataByteBuffer() {
      byte[] data = new byte[32];
      ByteBuffer buffer = ByteBuffer.allocate(32);
      for(int i = 0; i < 16; i++){
         buffer.put((byte)0xDD);
      }
      for(int i = 0; i < 16; i++){
         buffer.put((byte)0xFF);
      }
      buffer.position(16);
      MemoryResource mem = new MemoryResource(data, 0, 64);
      mem.copyData(0, buffer, buffer.remaining());
      
      byte[] answer = new byte[32];
      for(int i = 0; i < 16; i++){
         answer[i] = (byte)0xFF;
      }
      
      Assert.assertArrayEquals(answer, mem.getData());
      
      buffer.position(0);
      mem.copyData(0, buffer, buffer.remaining());
      for(int i = 0; i < 16; i++){
         answer[i] = (byte)0xDD;
      }
      for(int i = 16; i < 32; i++){
         answer[i] = (byte)0xFF;
      }
      
      Assert.assertArrayEquals(answer, mem.getData());
   }
   
   @Test
   public void testSignedInt8() {
      byte[] data = new byte[256];
      for (int i = 0; i < data.length; i++) {
         data[i] = (byte)i;
      }
      MemoryResource mem = new MemoryResource(data, 0, 256);

      
      int val = mem.getSignedInt(8, 0, 7);
      Assert.assertEquals(8, val);
      
      val = mem.getSignedInt(127, 0, 7);
      Assert.assertEquals(127, val);
      
      val = mem.getSignedInt(128, 0, 7);
      Assert.assertEquals(-128, val);
      
      val = mem.getSignedInt(255, 0, 7);
      Assert.assertEquals(-1, val);
      
      mem.setInt(255, 0, 0, 7);
      val = mem.getSignedInt(0, 0, 7);
      Assert.assertEquals(-1, val);
      
      mem.setInt(-1, 1, 0, 7);
      val = mem.getSignedInt(1, 0, 7);
      Assert.assertEquals(-1, val);
      
      mem.setInt(-1, 4, 2, 5);
      val = mem.getSignedInt(4, 2, 5);
      Assert.assertEquals(-1, val);
      
      val = mem.getInt(4, 2, 5);
      Assert.assertEquals(15, val);
   }
   
   @Test
   public void testSignedInt() {
      byte[] data = new byte[256];
      for (int i = 0; i < data.length; i++) {
         data[i] = (byte)i;
      }
      MemoryResource mem = new MemoryResource(data, 0, 256);

      mem.setInt(14, 0, 0, 31);
      int val = mem.getSignedInt(0, 0, 31);
      Assert.assertEquals(14, val);
      
      mem.setInt(-450, 0, 0, 31);
      val = mem.getSignedInt(0, 0, 31);
      Assert.assertEquals(-450, val);
      
   }
   
   @Test
   public void testBigInt() {
      byte[] data = new byte[256];
      for (int i = 0; i < data.length; i++) {
         data[i] = (byte)i;
      }
      MemoryResource mem = new MemoryResource(data, 0, 256);
      
      mem.setLong(-1, 0, 0, 63);
      BigInteger val = mem.getUnsigned64(0, 0, 63);
      String hex = val.toString(16).toUpperCase();
      Assert.assertEquals("FFFFFFFFFFFFFFFF", hex);
      
      mem.setLong(-100, 0, 0, 63);
      val = mem.getUnsigned64(0, 0, 63);
      hex = val.toString(16).toUpperCase();
      System.out.println(hex);
      String expected = "FFFFFFFFFFFFFF9C";
      Assert.assertEquals(expected, hex);
   }

}
