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
