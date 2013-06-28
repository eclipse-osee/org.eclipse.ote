package org.eclipse.ote.io;

import java.lang.reflect.Array;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;

/**
 * This class implements a circular buffer backed by an Object[].  It is useful for high IO application that 
 * have high throughput.  It is not thread-safe, synchronization must be handled externally. 
 * 
 * @author Andrew M. Finkbeiner
 *
 * @param <T>
 */
public class CircularBuffer<T> {
   
   private static final String SIZE_COPY_ERROR__BUFFERSIZE__ADDEDSIZE = "Cannot copy array of size[%d] into circular buffer of size[%d]";
   
   private int head = -1;
   private int tail = -1;
   private int size;
   private Object[] data;
   
   public CircularBuffer(int limit) {
      size = limit;
      data = new Object[limit];
   }
   
   public T add(T entry){
      T returnValue = null;
      if(hasData() && isHeadPassingTail(1)){
         returnValue = remove();
      }
      data[incrementAndGetHead()] = entry;
      if(tail == -1){
         tail = 0;
      }
      return returnValue;
   }

   @SuppressWarnings("unchecked")
   public T[] add(T[] entry, int offset, int lengthToCopy){
      if(lengthToCopy > size){
         throw new IllegalArgumentException(String.format(SIZE_COPY_ERROR__BUFFERSIZE__ADDEDSIZE, lengthToCopy, size));
      }
      T[] returnValue = null;
      if(hasData() && isHeadPassingTail(lengthToCopy)){
         returnValue = getCopy(entry.getClass().getComponentType(), tail, lengthToCopy - getRemaining());
         tail += returnValue.length;
         if(tail > size){
            tail-=size;
         }
      }
      
      int toEnd = (head == -1 ? size : (size) - (head+1));
      if(toEnd >= lengthToCopy){
         try{
            System.arraycopy(entry, offset, data, head+1, lengthToCopy);
            head+=lengthToCopy;
         } catch (ArrayIndexOutOfBoundsException ex){
            OseeLog.log(getClass(), Level.SEVERE, String.format("offset[%d], offset2[%d], length[%d] sourcesz[%d] destsz[%d]", offset, head+1, lengthToCopy, entry.length, data.length));
         }
      } else {
         try{
            System.arraycopy(entry, offset, data, head+1, toEnd);
         } catch (ArrayIndexOutOfBoundsException ex){
            OseeLog.log(getClass(), Level.SEVERE, String.format("offset[%d], offset2[%d], length[%d] sourcesz[%d] destsz[%d]", offset, head+1, toEnd, entry.length, data.length));
         }
         int left = (lengthToCopy - toEnd);
         try{
            System.arraycopy(entry, offset+toEnd, data, 0, left);
         } catch (ArrayIndexOutOfBoundsException ex){
            OseeLog.log(getClass(), Level.SEVERE, String.format("offset[%d], offset2[%d], length[%d] sourcesz[%d] destsz[%d]", offset+toEnd, 0, left, entry.length, data.length));
         }
         head = left-1;
      }
      if(tail == -1){
         tail = 0;
      }
      if(returnValue == null){
         returnValue = (T[]) Array.newInstance(entry.getClass().getComponentType(), 0);
      }
      return returnValue;
   }
   
   private boolean isHeadPassingTail(int sizeToAdd) {
      if(sizeToAdd == size){
         return true;
      }
      return sizeToAdd > getRemaining();
   }
   
   private int getRemaining(){
      return head >= tail ? (size -1) - head + tail: tail - (head + 1);
   }

   private boolean hasData() {
      if(head > -1 && tail > -1){
         return true;
      }
      return false;
   }

   private int incrementAndGetHead() {
      head++;
      if(head == size){
         head = 0;
      }
      return head;
   }

   @SuppressWarnings("unchecked")
   public T head() {
      return (T)data[head];
   }

   @SuppressWarnings("unchecked")
   public T[] getCopy(Class<?> clazz) {
      int currentSize = getSize();
      T[] copy = (T[]) Array.newInstance(clazz, currentSize);
      if(currentSize > 0){
         if(head >= tail){
            System.arraycopy(data, tail, copy, 0, currentSize);
         } else {
            System.arraycopy(data, tail, copy, 0, size-tail);
            System.arraycopy(data, 0, copy, size-tail, head + 1);
         }
      }
      return copy;
   }
   
   @SuppressWarnings("unchecked")
   private T[] getCopy(Class<?> clazz, int offset, int length){
      T[] copy = (T[]) Array.newInstance(clazz, length);
      if(offset + length > data.length){
         int firstCopyLength = size-offset;
         System.arraycopy(data, offset, copy, 0, firstCopyLength);
         int remaining = length - firstCopyLength;
         System.arraycopy(data, 0, copy, firstCopyLength, remaining);
      } else {
         System.arraycopy(data, offset, copy, 0, length);
      }
      return copy;
   }
   
   private int getSize() {
      if(head == -1 && tail == -1){
         return 0;
      } else if(head > tail){
         return head - tail + 1;
      } else {
         return size - tail + head + 1;
      }
   }

   @SuppressWarnings("unchecked")
   public T remove(){
      T value = null;
      if(tail > -1){
         value = (T)data[tail];
         if(tail == head){
            tail = -1;
            head = -1;
         } else {
            tail++;
            if(tail == size){
               tail = 0;
            }
         }
      }
      return value;
   }
   
   public void clear() {
      head = -1;
      tail = -1;
   }

   public int getTotalSize() {
      return size;
   }
   
}
