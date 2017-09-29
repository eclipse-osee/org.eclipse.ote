package org.eclipse.ote.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;

public class BasicDatagramChannelRunnable extends DatagramChannelRunnable {

   private static final int SEND_BUFFER_SIZE = 1024 * 512 * 10;
   private static final boolean BLOCKING = System.getProperty("ote.pubsub.send.block", "true").equalsIgnoreCase("true");

   public BasicDatagramChannelRunnable(InetSocketAddress address) {
      super(address);
   }
   
   @Override
   public void doSend(DatagramChannel channel, List<DatagramChannelData> dataToSend) throws IOException {
	  int size = dataToSend.size();
      for(int i = 0; i < size; i++){
         DatagramChannelData data = dataToSend.get(i);
         doSend(channel, data);
      }
   }
   
   @Override
   public void doSend(DatagramChannel channel, DatagramChannelData data) throws IOException {
      ByteBuffer byteBuffer = data.getByteBuffer();
      byteBuffer.flip();
      List<SocketAddress> addresses = data.getAddresses();
      int innerSize = addresses.size();
      for(int j = 0; j < innerSize; j++){
         SocketAddress address = addresses.get(j);
         channel.send(byteBuffer, address);
         byteBuffer.rewind();
      }
   }
   
   @Override
   public DatagramChannel openAndInitializeDatagramChannel(InetSocketAddress address) throws IOException {
      DatagramChannel channel = DatagramChannel.open();
      if (channel.socket().getSendBufferSize() < SEND_BUFFER_SIZE) {
         channel.socket().setSendBufferSize(SEND_BUFFER_SIZE);
      }
      channel.socket().setReuseAddress(true);
      channel.socket().bind(address);
      channel.configureBlocking(BLOCKING);
      return channel;
   }

      
}
