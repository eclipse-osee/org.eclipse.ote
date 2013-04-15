package org.eclipse.ote.io.internal;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.List;

import org.eclipse.ote.io.DatagramChannelData;

public class PoisonPill implements DatagramChannelData {

   @Override
   public ByteBuffer getByteBuffer() {
      return null;
   }

   @Override
   public List<SocketAddress> getAddresses() {
      return null;
   }

   @Override
   public void setAddresses(List<SocketAddress> addresses) {
      
   }

   @Override
   public void postProcess() {
      
   }

}
