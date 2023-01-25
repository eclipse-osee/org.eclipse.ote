/*********************************************************************
 * Copyright (c) 2023 Boeing
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
