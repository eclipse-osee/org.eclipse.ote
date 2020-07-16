/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.ote.simple.io;

import java.util.Collection;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.osee.ote.message.io.IOWriter;
import org.eclipse.osee.ote.messaging.dds.DataStoreItem;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;
import org.eclipse.ote.io.mux.MuxHeader;

/**
 * @author Michael P. Masterson
 */
public class SimpleMuxWriter implements IOWriter {

   private Namespace namespace;

   public SimpleMuxWriter(Namespace namespace) {
      this.namespace = namespace;
   }

   @Override
   public void write(IDestination destination, ISource source, DataStoreItem data) {
      print(data.getTheDataSample().getData().toByteArray());
   }

   /**
    * Prints the header and field value to standard out
    * @param arr
    */
   private void print(byte[] arr) {
      StringBuilder sb = new StringBuilder("SIMPLE MUX:"); 
      sb.append(String.format("%d:", System.currentTimeMillis()));

      sb.append("\n\tHeader: ");
      int headerLength = MuxHeader.MUX_HEADER_BYTE_SIZE;
      for (int i = 0; i < headerLength; i++) {
         sb.append(String.format("%02X ", arr[i]));
      }
      sb.append("\n\tBody: \"");
      
      for (int i = headerLength; i < arr.length ; i++) {
         byte b = arr[i];
         if(b == 0x0 && arr[i-1] != 0x0) {
            sb.append("\\0");
         } else {
            sb.append((char)b);
         }
      }
      sb.append("\"");
      System.out.println(sb);
   }

   @Override
   public void write(IDestination destination, ISource source, MessageData data) {
      System.out.print(data.toString() + ":");
      print(data.toByteArray());
   }

   @Override
   public void write(IDestination destination, ISource source, Collection<MessageData> data) {
      for(MessageData md : data ) {
         System.out.print(data.toString() + ":");
         print(md.toByteArray());
      }
   }

   @Override
   public boolean accept(String topic) {
      return true;
   }

   @Override
   public String getNamespace() {
      return namespace.toString();
   }

}