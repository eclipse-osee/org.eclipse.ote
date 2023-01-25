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
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osee.ote.io.SystemOutputListener;

public class SpecializedOutputStream extends OutputStream {

   private CopyOnWriteArrayList<SystemOutputListener> listeners;

   public SpecializedOutputStream() {
      this.listeners = new CopyOnWriteArrayList<>();
   }

   public void add(SystemOutputListener listener){
      listeners.add(listener);
   }
   
   public void remove(SystemOutputListener listner){
      listeners.remove(listner);
   }
   
   @Override
   public void write(int arg0) throws IOException {
      
   }

   @Override
   public void close() throws IOException {
      for(SystemOutputListener listner:listeners){
         listner.close();
      }
   }

   @Override
   public void flush() throws IOException {
      for(SystemOutputListener listner:listeners){
         listner.flush();
      }
   }

   @Override
   public void write(byte[] b, int off, int len) throws IOException {
      for(SystemOutputListener listner:listeners){
         listner.write(b, off, len);
      }
   }

   @Override
   public void write(byte[] b) throws IOException {
      for(SystemOutputListener listner:listeners){
         listner.write(b);
      }
   }

}
