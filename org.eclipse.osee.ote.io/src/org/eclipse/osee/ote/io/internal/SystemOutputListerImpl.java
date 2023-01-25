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
package org.eclipse.osee.ote.io.internal;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.osee.ote.io.SystemOutputListener;

public class SystemOutputListerImpl implements SystemOutputListener {

   private OutputStream outputStream;

   public SystemOutputListerImpl(OutputStream outputStream) {
      this.outputStream = outputStream;
   }

   @Override
   public void close() throws IOException {
      outputStream.close();
   }

   @Override
   public void flush() throws IOException {
      outputStream.flush();
   }

   @Override
   public void write(byte[] b, int off, int len) throws IOException {
      outputStream.write(b, off, len);
   }

   @Override
   public void write(byte[] b) throws IOException {
      outputStream.write(b);
   }

}
