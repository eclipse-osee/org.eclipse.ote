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
package org.eclipse.ote.io.mux.lookup;

import org.eclipse.ote.io.mux.MuxReceiveTransmit;

/**
 * Used as a key for looking up MuxMessages. 
 * 
 * @author Michael P. Masterson
 */
public class MuxLookupKey {
   public int channel;
   public int rt;
   public MuxReceiveTransmit receiveTransmit;
   public int subaddress;

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + channel;
      result = prime * result + ((receiveTransmit == null) ? 0 : receiveTransmit.hashCode());
      result = prime * result + rt;
      result = prime * result + subaddress;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      MuxLookupKey other = (MuxLookupKey) obj;
      if (channel != other.channel)
         return false;
      if (receiveTransmit != other.receiveTransmit)
         return false;
      if (rt != other.rt)
         return false;
      if (subaddress != other.subaddress)
         return false;
      return true;
   }
   
   
}
