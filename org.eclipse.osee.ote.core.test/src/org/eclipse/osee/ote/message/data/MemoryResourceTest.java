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
      for (int i = 0; i < 16; i++) {
         buffer.put((byte) 0xDD);
      }
      for (int i = 0; i < 16; i++) {
         buffer.put((byte) 0xFF);
      }
      buffer.position(16);
      MemoryResource mem = new MemoryResource(data, 0, 64);
      mem.copyData(0, buffer, buffer.remaining());

      byte[] answer = new byte[32];
      for (int i = 0; i < 16; i++) {
         answer[i] = (byte) 0xFF;
      }

      Assert.assertArrayEquals(answer, mem.getData());

      buffer.position(0);
      mem.copyData(0, buffer, buffer.remaining());
      for (int i = 0; i < 16; i++) {
         answer[i] = (byte) 0xDD;
      }
      for (int i = 16; i < 32; i++) {
         answer[i] = (byte) 0xFF;
      }

      Assert.assertArrayEquals(answer, mem.getData());
   }

   @Test
   public void testSignedInt8() {
      byte[] data = new byte[256];
      for (int i = 0; i < data.length; i++) {
         data[i] = (byte) i;
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
         data[i] = (byte) i;
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
         data[i] = (byte) i;
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

   @Test
   public void testMask() {
      StringBuilder sb = new StringBuilder();
      MemoryResource mem = new MemoryResource(new byte[24], 0, 24);
      String expected;

      // Begin int tests
      mem.setInt(0x22334455, 13, 0, 31);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 00 00 00 00 00 00 00 00 00 ff ff ff ff 00 00 00 00 00 00 00 ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      mem.setInt(0xff334455, 8, 0, 31);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 00 00 00 00 ff ff ff ff 00 ff ff ff ff 00 00 00 00 00 00 00 ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      // Begin long tests
      mem = new MemoryResource(new byte[24], 0, 24);

      mem.setLong(0x02L, 21, 7, 56);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 ff 80 ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      mem.setLong(0x02L, 21, 0, 56);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ff ff 80 ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      mem.setLong(0x12L, 16, 32, 63);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ff ff ff ff ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      mem.setLong(0x12L, 16, 24, 63);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ff ff ff ff ff ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      mem.setLong(0xffL, 16, 23, 43);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 ff ff ff ff ff ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      mem.setLong(0xffdd555522L, 16, 8, 63);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ff ff ff ff ff ff ff ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      mem.setLong(0x00L, 16, 7, 41);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 ff ff ff ff ff ff ff ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      mem.setLong(0x1234567890abcdefL, 16, 2, 63);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 3f ff ff ff ff ff ff ff ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      mem.setLong(0xf234567890abcdefL, 16, 0, 63);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ff ff ff ff ff ff ff ff ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      //Begin ASCII tests
      mem = new MemoryResource(new byte[24], 0, 24);

      mem.setASCIIString("Testing", 4, 0, 55);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 ff ff ff ff ff ff ff 00 00 00 00 00 00 00 00 00 00 00 00 00 ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);

      mem.setASCIIString("Boeing", 16);
      for (int i = 0; i < mem.getLength(); i++) {
         sb.append(String.format("%02x ", mem.getMask()[i]));
      }
      expected = "00 00 00 00 ff ff ff ff ff ff ff 00 00 00 00 00 ff ff ff ff ff ff ff ff ";
      Assert.assertEquals(expected, sb.toString());
      sb.setLength(0);
   }
}
