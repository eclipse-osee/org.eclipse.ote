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

package org.eclipse.ote.ui.mux.datatable;

/**
 * @author Ky Komadino
 */
public class RowNode {
   private final byte[] databytes;
   private int numDatabytes;

   public RowNode() {
      databytes = new byte[16];
      numDatabytes = 0;
   }

   public synchronized void setData(byte[] data) {
      numDatabytes = data.length;
      for (int i = 0; i < data.length && i < databytes.length; i++) {
         databytes[i] = data[i];
      }
   }

   public synchronized void setDataword(int data, int index) {
      databytes[index * 2] = (byte) ((data & 0x0000FF00) >> 8);
      databytes[index * 2 + 1] = (byte) (data & 0x000000FF);
   }

   public synchronized byte getDatabyte(int index) {
      return databytes[index];
   }

   public synchronized String getDataword(int index) {
      if (numDatabytes > index * 2 + 1) {
         return String.format("%02X%02X", (short) (databytes[index * 2] & 0x00FF),
            (short) (databytes[index * 2 + 1] & 0x00FF));
      } else {
         return "";
      }
   }
}
