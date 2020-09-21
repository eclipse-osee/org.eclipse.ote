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

package org.eclipse.osee.ote.message.data;

import java.nio.ByteBuffer;

/**
 * @author Andrew M. Finkbeiner
 */
public class ByteArrayHolder {
   private byte[] buffer;
   private byte[] mask;
   private ByteBuffer byteBuffer;

   public ByteArrayHolder() {

   }

   public ByteArrayHolder(byte[] buffer) {
      this.buffer = buffer;
      byteBuffer = ByteBuffer.wrap(buffer);
      this.mask = new byte[buffer.length];
   }

   public void set(byte[] buffer) {
      this.buffer = buffer;
      byteBuffer = ByteBuffer.wrap(buffer);
   }

   public byte[] get() {
      return this.buffer;
   }

   public byte[] getMask() {
      return this.mask;
   }

   public ByteBuffer getByteBuffer() {
      return byteBuffer;
   }
}
