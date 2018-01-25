package org.eclipse.ote.io;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.List;

public final class BasicDatagramChannelDataFactory implements DatagramChannelDataFactory {

   public static int DatagramByteBufferSize = 61248;
   
   public BasicDatagramChannelDataFactory() {
   }
   
   @Override
   public DatagramChannelData create(DatagramChannelDataPool pool) {
      return new DatagramChannelDataImpl(pool);
   }
   
   private class DatagramChannelDataImpl implements DatagramChannelData {

      private List<SocketAddress> addresses;
      private DatagramChannelDataPool pool;
      ByteBuffer buffer;
      
      DatagramChannelDataImpl(DatagramChannelDataPool pool){
         this.pool = pool;
         buffer = ByteBuffer.allocateDirect(DatagramByteBufferSize);
      }
      
      @Override
      public ByteBuffer getByteBuffer() {
         return buffer;
      }

      @Override
      public List<SocketAddress> getAddresses() {
         return addresses;
      }

      @Override
      public void setAddresses(List<SocketAddress> addresses) {
         this.addresses = addresses;
      }

      @Override
      public void postProcess() {
         pool.offer(this);
      }
   }

}
