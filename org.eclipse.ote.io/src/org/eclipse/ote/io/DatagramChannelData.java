package org.eclipse.ote.io;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.List;

public interface DatagramChannelData {
   ByteBuffer getByteBuffer();
   List<SocketAddress> getAddresses();
   void setAddresses(List<SocketAddress> addresses);
   void postProcess();
}
