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

package org.eclipse.osee.ote.message.instrumentation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 */
public class BaseIOInstrumentation implements IOInstrumentation {

   private final List<InetSocketAddress> addresses;
   private final DatagramChannel channel;

   public BaseIOInstrumentation() throws IOException {
      addresses = new ArrayList<>();
      channel = DatagramChannel.open();
      channel.configureBlocking(true);

   }

   @Override
   public void command(byte[] cmd) {
   }

   @Override
   public void register(InetSocketAddress address) {
      if (address == null) {
         throw new IllegalArgumentException("address cannot be null");
      }
      addresses.add(address);
   }

   @Override
   public void unregister(InetSocketAddress address) {
      addresses.remove(address);
   }

   public void send(byte[] bytes) {
      send(ByteBuffer.wrap(bytes));
   }

   public void send(byte[] bytes, int offset, int length) {
      send(ByteBuffer.wrap(bytes, offset, length));
   }

   public void send(ByteBuffer bytes) {
      bytes.mark();
      int size = addresses.size();
      for (int i = 0; i < size; i++) {
         try {
            channel.send(bytes, addresses.get(i));
            bytes.reset();
         } catch (Exception ex) {
            OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, ex.toString(), ex);
         }
      }
   }

}
