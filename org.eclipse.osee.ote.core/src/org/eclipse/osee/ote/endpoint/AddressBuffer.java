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
package org.eclipse.osee.ote.endpoint;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class AddressBuffer {
   
   private ByteBuffer buffer;
   private InetSocketAddress address;
   
   public AddressBuffer(){
      buffer = ByteBuffer.allocate(131072);
   }
   
   public ByteBuffer getBuffer(){
      return buffer;
   }
   
   public void setBytes(byte[] bytes) {
      buffer = ByteBuffer.wrap(bytes);
   }
   
   public InetSocketAddress getAddress(){
      return address;
   }
   
   public void setAddress(InetSocketAddress address){
      this.address = address;
   }

}
