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

package org.eclipse.ote.io;

import org.eclipse.osee.ote.message.enums.DataType;

/**
 * @author Michael P. Masterson
 */
public enum GenericOteIoType implements DataType {
   PUB_SUB(5, 1024 * 64),
   MUX(64, 128);

   private final int depth;
   private final int bufferSize;

   private GenericOteIoType(int depth, int bufferSize) {
      this.depth = depth;
      this.bufferSize = bufferSize;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.enums.DataType#getToolingDepth()
    */
   @Override
   public int getToolingDepth() {
      return this.depth;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.message.enums.DataType#getToolingBufferSize()
    */
   @Override
   public int getToolingBufferSize() {
      return this.bufferSize;
   }

}
