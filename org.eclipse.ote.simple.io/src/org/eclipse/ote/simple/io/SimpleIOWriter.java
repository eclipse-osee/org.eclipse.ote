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

/**
 * @author Michael P. Masterson
 */
public class SimpleIOWriter implements IOWriter {

   private Namespace namespace;

   public SimpleIOWriter(Namespace namespace) {
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
      StringBuilder sb = new StringBuilder("SIMPLE: "); 
      sb.append(String.format("%d:\"", System.currentTimeMillis()));
      
      int headerLength = 32;
      for (int i = 0; i < headerLength && arr[i] != 0; i++) {
         sb.append((char)arr[i]);
      }
      sb.append("\" = \"");
      
      for (int i = headerLength; i < arr.length ; i++) {
         if(arr[i] == 0 && arr[i-1] != 0) {
            sb.append("\\0");
         } else {
            sb.append((char)arr[i]);
         }
      }
      sb.append("\"");
      System.out.println(sb);
   }

   @Override
   public void write(IDestination destination, ISource source, MessageData data) {
      print(data.toByteArray());
   }

   @Override
   public void write(IDestination destination, ISource source, Collection<MessageData> data) {
      for(MessageData md : data ) {
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
