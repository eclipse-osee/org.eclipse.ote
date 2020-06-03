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

package org.eclipse.osee.ote.message.tool;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;

public final class TransferConfig {
   public static enum Direction {
      SOCKET_TO_FILE(SelectionKey.OP_READ),
      FILE_TO_SOCKET(SelectionKey.OP_WRITE);

      private int accessType;

      private Direction(int accessType) {
         this.accessType = accessType;
      }

      public int getSelectionAccessOperation() {
         return accessType;
      }

   }
   private final String fileName;
   private final InetSocketAddress sourceAddress;
   private final InetSocketAddress destinationAddress;
   private final Direction direction;
   private final int blockCount;
   private final boolean appendMode;
   
   public TransferConfig(final String fileName, final InetSocketAddress sourceAddress, final InetSocketAddress destinationAddress, final Direction direction, final int blockCount) {
	   this(fileName, sourceAddress, destinationAddress, direction, blockCount, false);
   }

   public TransferConfig(final String fileName, final InetSocketAddress sourceAddress, final InetSocketAddress destinationAddress, final Direction direction, final int blockCount, boolean appendMode) {
	      super();
	      this.fileName = fileName;
	      this.sourceAddress = sourceAddress;
	      this.destinationAddress = destinationAddress;
	      this.direction = direction;
	      this.blockCount = blockCount;
	      this.appendMode = appendMode;
	   }

   /**
    * @return the direction
    */
   public Direction getDirection() {
      return direction;
   }

   /**
    * @return the fileChannel
    */
   public String getFileName() {
      return fileName;
   }

   /**
    * @return the destination of the data
    */
   public InetSocketAddress getDestinationAddress() {
      return destinationAddress;
   }

   /**
    * @return the sourceAddress
    */
   public InetSocketAddress getSourceAddress() {
      return sourceAddress;
   }

   /**
    * @return the blockCount
    */
   public int getBlockCount() {
      return blockCount;
   }

   public boolean isAppendMode() {
	   return appendMode;
   }

}