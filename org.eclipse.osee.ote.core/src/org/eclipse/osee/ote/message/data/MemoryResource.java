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

package org.eclipse.osee.ote.message.data;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Level;

import org.eclipse.osee.ote.message.MessageSystemException;

/**
 * @author Andrew M. Finkbeiner
 */
public class MemoryResource {
   private static final Charset US_ASCII_CHARSET = Charset.forName("US-ASCII");

   private final ByteArrayHolder byteArray;
   private int _offset;
   private final int _length;
   private volatile boolean _dataHasChanged;

   public MemoryResource(byte data[], int offset, int length) {
      byteArray = new ByteArrayHolder(data);
      _length = length;
      _offset = offset;
      _dataHasChanged = false;
   }

   protected MemoryResource(ByteArrayHolder byteArray, int offset, int length) {
      this.byteArray = byteArray;
      _length = length;
      _offset = offset;
      _dataHasChanged = false;
   }

   public final String getUnfilteredASCIIString(int offset, int msb, int lsb) {
      offset += _offset;
      int size = (lsb - msb + 1) / 8;

      StringBuilder str = new StringBuilder(size);
      int limit = Math.min(size, byteArray.get().length-offset);
      for (int i = 0; i < limit; i++) {
         str.append(getASCIICharFromOffset(offset + i));
      }
      return str.toString();
   }
   public void setData(byte data[]) {
      byteArray.set(data);
      _dataHasChanged = true;
   }

   public byte[] getData() {
      return byteArray.get();
   }

   public byte getByte(int offset, int msb, int lsb) {
      offset += _offset;
      if (msb == 0 && lsb == 7) {
         return byteArray.get()[offset];
      } else {
         int b = byteArray.get()[offset];
         int mask = (1 << 8 - msb) - 1;
         mask = mask & 0xFFFFFF80 >>> lsb;
         return (byte) ((b & mask) >> 7 - lsb);
      }
   }

   private byte getByteFromOffset(int offset) {
      return byteArray.get()[offset];
   }

   private final char getASCIICharFromOffset(int offset) {
      return (char) byteArray.get()[offset];
   }
   
   public final BigInteger getUnsigned64(int offset, int msb, int lsb) {
      int fieldSize = lsb - msb + 1;
      if(fieldSize > 64) {
         throw new IllegalArgumentException(String.format("Field must be smaller than 64 bits, actual size = %d", fieldSize));
      }
      int byteSize = fieldSize / 8;
      byte[] bytes = new byte[byteSize + 1]; 
      long bitsAsLong = getLong(offset, msb, lsb);
      for(int i = bytes.length - 1 ; i > 0 ; i--) {
         bytes[i] = (byte) bitsAsLong;
         bitsAsLong >>>= 8;
      }
      bytes[0] = 0; // forcing the value to be non-negative by setting the first byte to all zeroes
      BigInteger retVal;
      retVal = new BigInteger(bytes);

      return retVal;
      
   }
   
   public int getInt(int offset, int msb, int lsb, boolean isSigned) {
      offset += _offset;
      if (lsb - msb <= 64) {
         final byte[] data = byteArray.get();
         final int length = data.length;
         final int beginByte = offset + msb / 8;
         int endByte = offset + lsb / 8;
         int fieldSize = lsb - msb + 1;
         endByte = endByte < length ? endByte : length;
         int lsbShift = lsb % 8;
         int v = data[beginByte] & 0xFF;
         if (endByte != beginByte) {
            for (int i = beginByte + 1; i <= endByte - 1; i++) {
               v <<= 8;
               v |= data[i] & 0xFF;
            }
            v <<= lsbShift + 1;
            int lastByteShifted = (data[endByte] & 0xFF) >>> (7 - lsbShift);
            v |= lastByteShifted;
         } else {
            v >>>= (7 - lsbShift);
         }
         int signExtensionShift = 32 - fieldSize;
         int retVal = v << signExtensionShift; // remove the leading bits outside of the field
         if(isSigned) {
            retVal >>= signExtensionShift; // Guarantee sign extension
         } else {
            retVal >>>= signExtensionShift;
         }
         return retVal;
      } else {
         throw new IllegalArgumentException("gettting long with bits not supported");
      }
   }

   /**
    * @param offset
    * @param msb
    * @param lsb
    * @return The int representation of the data with the sign extension removed.  The sign will remain if the field
    * is 32 bits
    */
   public final int getInt(int offset, int msb, int lsb) {
      return getInt(offset, msb, lsb, false);
   }
   
   public final int getSignedInt(int offset, int msb, int lsb) {
      return getInt(offset, msb, lsb, true);
   }

   /**
    * Retrieves data from the buffer at the given location as a long.  this will not sign extend but if the
    * field is 64 bits long and is large enough the value returned may still appear negative
    * 
    * @param offset
    * @param msb
    * @param lsb
    * @return The long representation of the data with the sign extension removed.  The sign will remain if the field
    * is 64 bits
    */
   public final long getLong(int offset, int msb, int lsb) {
      return getLong(offset, msb, lsb, false);
   }
   
   public long getSignedLong(int offset, int msb, int lsb) {
      return getLong(offset, msb, lsb, true);
   }

   /**
    * Return bits described as a long.
    * @param offset
    * @param msb
    * @param lsb
    * @param isSigned true if the MSB should be considered the 2's compliment sign bit. 
    * @return The long value of the bits described
    */
   public long getLong(int offset, int msb, int lsb, boolean isSigned) {
      offset += _offset;
      if (lsb - msb <= 64) {
         final byte[] data = byteArray.get();
         final int length = data.length;
         final int beginByte = offset + msb / 8;
         int endByte = offset + lsb / 8;
         int fieldSize = lsb - msb + 1;
         endByte = endByte < length ? endByte : length;
         int lsbShift = lsb % 8;
         long v = data[beginByte] & 0xFF;
         if (endByte != beginByte) {
            for (int i = beginByte + 1; i <= endByte - 1; i++) {
               v <<= 8;
               v |= data[i] & 0xFF;
            }
            v <<= lsbShift + 1;
            int lastByteShifted = (data[endByte] & 0xFF) >>> (7 - lsbShift);
            v |= lastByteShifted;
         } else {
            v >>>= (7 - lsbShift);
         }
         int signShift = 64 - fieldSize;
         long retVal = v << signShift; // remove the leading bits outside of the field
         if(isSigned) {
            retVal >>= signShift; // Guarantee sign extension
         } else {
            retVal >>>= signShift;
         }
         return retVal;
      } else {
         throw new IllegalArgumentException("gettting long with bits not supported");
      }
   }

   public final String getASCIIString(int offset, int length) {
      offset += _offset;

      StringBuilder str = new StringBuilder(length);
      for (int i = 0; i < length; i++) {
         char ch = getASCIICharFromOffset(offset + i);
         if (ch != 0) {// NOTE this was done to be compatible with java
            // Strings that don't null char termination
            str.append(getASCIICharFromOffset(offset + i));
         }
      }
      return str.toString();
   }

   public final String getASCIIString(int offset, int msb, int lsb) {
      offset += _offset;
      int size = (lsb - msb + 1) / 8;

      StringBuilder str = new StringBuilder(size);
      for (int i = 0; i < size; i++) {
         if (offset + i >= byteArray.get().length) {
            break;
         }
         char ch = getASCIICharFromOffset(offset + i);
         if (ch == 0) {
            break; //Terminate on null characters.
         }
         str.append(getASCIICharFromOffset(offset + i));
      }
      return str.toString();
   }

   public final int getASCIIChars(int offset, int msb, int lsb, char[] destination) {
      offset += _offset;
      int size = (lsb - msb + 1) / 8;
      int destIndex = 0;

      for (int i = 0; i < size; i++) {
         if (offset + i >= byteArray.get().length) {
            break;
         }
         char ch = getASCIICharFromOffset(offset + i);
         destination[destIndex] = ch;
         destIndex++;
      }
      return destIndex;
   }

   public boolean asciiEquals(int offset, int msb, int lsb, String other) {
      offset += _offset;
      int size = (lsb - msb + 1) / 8;
      if (other.length() > size) {
         return false;
      }
      boolean isEqual = true;
      for (int i = 0; i < size && isEqual; i++) {
         char ch = getASCIICharFromOffset(offset + i);
         if (ch != 0) {// NOTE this was done to be compatible with java
            // Strings that don't null char termination
            isEqual = ch == other.charAt(i);
         }
      }
      return isEqual;
   }

   public void setBoolean(boolean v, int offset, int msb, int lsb) {
      int i = v ? 1 : 0;
      if (lsb < 32) {
         setInt(i, offset, msb, lsb);
      } else {
         throw new RuntimeException("Not supported lsb = " + lsb);
      }
   }

   public final void setByte(int v, int offset, int msb, int lsb) {
      offset += _offset;
      if (msb == 0 && lsb == 7) {
         setByteFromOffset(v, offset);
      } else {
         if ((v & 1 >>> 7 - (lsb - msb)) != 0) {
            throw new IllegalArgumentException("Tried to set signal to value that is too large");
         }
         int mask = createMask(msb, lsb, 7);
         v = v << 7 - lsb;
         v &= ~mask;
         setByteFromOffset(v | getByteFromOffset(offset) & mask, offset);
      }
   }

   public final void setBytesInHeader(int v, int offset, int msb, int lsb) {
      if (offset > _offset) {
         throw new IllegalArgumentException("Data beyond header attempting to be set!!!");
      }
      if (msb == 0 && lsb == 7) {
         setByteFromOffset(v, offset);
      } else {
         if ((v & 1 >>> 7 - (lsb - msb)) != 0) {
            throw new IllegalArgumentException("Tried to set signal to value that is too large");
         }
         int mask = (1 << 7 - lsb) - 1;
         mask = mask | 0xFFFFFF00 >>> msb;
         v = v << 7 - lsb;
         setByteFromOffset(v | getByteFromOffset(offset) & mask, offset);
      }
   }

   private final void setByteFromOffset(int v, int offset) {
      byteArray.get()[offset] = (byte) v;
      _dataHasChanged = true;
   }

   public void setOffset(int offset) {
      this._offset = offset;
   }

   public final void setInt(int v, int offset, int msb, int lsb) {
      offset += _offset;
      final byte[] data = byteArray.get();
      final int length = data.length;
      final int beginByte = offset + msb / 8;
      int endByte = offset + lsb / 8;
      endByte = endByte < length ? endByte : length - 1;
      final int lsbMod = lsb % 8;
      if (endByte != beginByte) {
         byte mask = (byte) (0xFF >>> lsbMod + 1); // mask used to mask off bits we shouldn't touch
         data[endByte] &= mask; // zero out bits that will be set by v
         int lastByteVShifted = v << 7 - lsbMod; // shift v so that it lines up
         data[endByte] |= lastByteVShifted;
         v >>>= lsbMod + 1; // shift to the next byte
         for (int i = endByte - 1; i >= beginByte + 1; i--) {
            byte rightMostByteOfV = (byte) v;
            data[i] = rightMostByteOfV;
            v >>>= 8; // shift to the next byte
         }
         int msbShift = msb % 8;
         mask = (byte) (0xFF >>> msbShift);
         v &= mask;
         data[beginByte] &= ~mask;
         data[beginByte] |= v;
      } else {
         byte mask = (byte) (-1 << lsb - msb + 1); // create mask for everything left of msb
         v &= ~mask; // mask off everything to the left of the msb in the value
         int shift = 7 - lsbMod;
         mask <<= shift; // shift mask to align with the lsb
         v <<= shift; // shift value so that it aligns with the lsb
         mask |= (byte) (0xFF >>> lsbMod + 1); // union the mask so that it mask everything to the right of the lsb
         data[beginByte] &= mask; // zero out the bits about to be written to
         data[beginByte] |= v; // logical 'OR' in the value
      }
      _dataHasChanged = true;
   }

   private int createMask(int msb, int lsb, int maxBitPosition) {
      int maximumElementValue = (int) Math.pow(2, lsb - msb + 1) - 1;
      int maxValueInPosition = maximumElementValue << maxBitPosition - lsb;
      //the mask is all ones except at the bit positions we are setting
      int mask = ~maxValueInPosition;
      return mask;
   }
   
   public final void setBigInt(BigInteger v, int offset, int msb, int lsb) {
      long valAsLong = v.longValue();
      this.setLong(valAsLong, offset, msb, lsb);
   }


   public final void setLong(long v, int offset, int msb, int lsb) {
      if (lsb - msb < 64) {
         offset += _offset;
         final byte[] data = byteArray.get();
         final int length = data.length;
         final int beginByte = offset + msb / 8;
         int endByte = offset + lsb / 8;
         endByte = endByte < length ? endByte : length - 1;
         final int lsbMod = lsb % 8;
         if (endByte != beginByte) {
            byte mask = (byte) (0xFF >>> lsbMod + 1); // mask used to mask off bits we shouldn't touch
            data[endByte] &= mask; // zero out bits that will be set by v
            long lastByteVShifted = v << 7 - lsbMod; // shift last byte so that it lines up
            data[endByte] |= lastByteVShifted;
            v >>>= lsbMod + 1; // shift off the bits we just set in the endByte
            for (int i = endByte - 1; i >= beginByte + 1; i--) {
               data[i] = (byte) v;
               v >>>= 8;
            }
            mask = (byte) (0xFF >>> msb % 8);
            v &= mask;
            data[beginByte] &= ~mask;
            data[beginByte] |= v;
         } else {
            byte mask = (byte) (-1 << lsb - msb + 1);
            v &= ~mask;
            int shift = 7 - lsbMod;
            mask <<= shift;
            v <<= shift;
            mask |= (byte) (0xFF >>> lsbMod + 1);
            data[beginByte] &= mask;
            data[beginByte] |= v;
         }
         _dataHasChanged = true;
      } else {
         throw new IllegalArgumentException("not supported bit width of " + (lsb - msb + 1));
      }
   }

   public final void setASCIIString(String s, int offset, int msb, int lsb) {
      int size = (lsb - msb + 1) / 8;
      int limit = Math.min(s.length(), size);
      System.arraycopy(s.getBytes(US_ASCII_CHARSET), 0, byteArray.get(), _offset + offset, limit);
      zeroizeFromOffset(limit + offset, size - limit);
      _dataHasChanged = true;
   }

   public final void setASCIIString(String s, int offset) {
      System.arraycopy(s.getBytes(US_ASCII_CHARSET), 0, byteArray.get(), _offset + offset, s.length());
      _dataHasChanged = true;
   }

   public void zeroizeFromOffset(int offset, int size) {
      offset += _offset;
      Arrays.fill(byteArray.get(), offset, offset + size, (byte) 0);
      _dataHasChanged = true;
   }

   public boolean getBoolean(int offset, int msb, int lsb) {
      return getInt(offset, msb, lsb) != 0;
   }

   public void copyData(int offset, byte[] src, int srcOffset, int length) {
      //		assert(byteArray.get().length >= length );
      if (length + offset > byteArray.get().length) {
         throw new MessageSystemException("backing byte[] is too small for copy operation", Level.SEVERE);
      }
      System.arraycopy(src, srcOffset, byteArray.get(), offset, length);
      Arrays.fill(byteArray.get(), offset + length, byteArray.get().length, (byte) 0);
      _dataHasChanged = true;
   }

   public void copyData(ByteBuffer src) {
      copyData(0, src, src.remaining());
   }

   /**
    * @param destOffset offset in this memory resource in which the copy will begin
    */
   public void copyData(int destOffset, ByteBuffer src, int length) throws MessageSystemException {
      if (length + destOffset > byteArray.get().length) {
         throw new MessageSystemException("backing byte[] is too small for copy operation", Level.INFO);
      }
      if (src.hasArray()) {
         System.arraycopy(src.array(), src.arrayOffset() + src.position(), byteArray.get(), destOffset, length);
      } else {
         synchronized (src) {
            src.mark();
            src.get(byteArray.get(), destOffset, length);
            src.reset();
         }
      }
      Arrays.fill(byteArray.get(), destOffset + length, byteArray.get().length, (byte) 0);
      _dataHasChanged = true;
   }

   public ByteBuffer getAsBuffer() {
      return ByteBuffer.wrap(byteArray.get());
   }

   public ByteBuffer getBuffer() {
      return byteArray.getByteBuffer();
   }

   //	public void set(ByteBuffer other) {
   //	buffer.put(other);
   //	}
   public ByteBuffer getAsBuffer(int offset, int length) {
      if (offset > byteArray.get().length) {
         throw new IllegalArgumentException(
               "offset of " + offset + " cannot be bigger than data length of " + byteArray.get().length);
      }
      if (offset + length > byteArray.get().length) {
         throw new IllegalArgumentException(
               "offset (" + offset + ") plus length (" + length + ") is greater than data length of " + byteArray.get().length);
      }
      return ByteBuffer.wrap(byteArray.get(), offset, length);
   }

   public int getOffset() {
      return _offset;
   }

   public int getLength() {
      return _length;
   }

   public MemoryResource slice(int offset, int length) {
      return new MemoryResource(byteArray, offset, length);
   }

   /**
    * @return the _dataHasChanged
    */
   public boolean isDataChanged() {
      return _dataHasChanged;
   }

   /**
    * @param hasChanged the _dataHasChanged to set
    */
   public void setDataHasChanged(boolean hasChanged) {
      _dataHasChanged = hasChanged;
   }

   public static void main(String[] args) {

      MemoryResource mem = new MemoryResource(new byte[24], 4, 24);
      mem.setLong(0x1234567890abcdefL, 12, 0, 63);
      for (int i = 0; i < 24; i++) {
         System.out.printf("%02x ", mem.getData()[i]);
      }
      System.out.printf("\nget=%016x\n", mem.getLong(12, 0, 63));

      mem.setLong(0xff22cc00aa11L, 12, 0, 43);
      for (int i = 0; i < 24; i++) {
         System.out.printf("%02x ", mem.getData()[i]);
      }
      System.out.printf("\nget=%016x\n", mem.getLong(12, 0, 43));

      mem.setLong(0xffdd555522L, 12, 2, 41);
      for (int i = 0; i < 24; i++) {
         System.out.printf("%02x ", mem.getData()[i]);
      }
      System.out.printf("\nget=%016x\n", mem.getLong(12, 2, 41));

      mem.setLong(0x00L, 12, 2, 41);
      for (int i = 0; i < 24; i++) {
         System.out.printf("%02x ", mem.getData()[i]);
      }
      System.out.printf("\nget=%016x\n", mem.getLong(12, 2, 41));

      mem.setLong(0x035544332211L, 12, 2, 43);
      for (int i = 0; i < 24; i++) {
         System.out.printf("%02x ", mem.getData()[i]);
      }
      System.out.printf("\nget=%016x\n", mem.getLong(12, 2, 43));
   }
}
