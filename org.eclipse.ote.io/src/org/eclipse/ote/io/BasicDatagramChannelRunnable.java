package org.eclipse.ote.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.List;

public class BasicDatagramChannelRunnable extends DatagramChannelRunnable {

   private static final int SEND_BUFFER_SIZE = 1024 * 512;

   public BasicDatagramChannelRunnable(InetSocketAddress address) {
      super(address);
   }
   
   @Override
   public void doSend(DatagramChannel channel, List<DatagramChannelData> dataToSend) throws IOException {
      for(DatagramChannelData data: dataToSend){
         data.getByteBuffer().flip();
         for(SocketAddress address: data.getAddresses()){
            channel.send(data.getByteBuffer(), address);
            data.getByteBuffer().rewind();
         }
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
      channel.configureBlocking(true);
      return channel;
   }
      
}
