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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.ote.io.mux.MuxData;
import org.eclipse.ote.message.manager.MessageDataLookupImpl;

/**
 * A simple message lookup that uses the 1553 Mux address information as a key.  This class
 * must be manually registered with the message manager service which is generally done in
 * an IO configuration component.
 *  
 * @author Michael P. Masterson
 */
public class MuxDataLookup extends MessageDataLookupImpl {
   
   private Map<MuxLookupKey, WeakReference<MessageData>> lookup = new HashMap<>(32);
   
   public MessageData get(MuxLookupKey key) {
      if(lookup.containsKey(key)) {
         return lookup.get(key).get();
      } else {
         return null;
      }
   }
   
   @Override
   public void put(MessageData data) {
      super.put(data);
      
      if(data instanceof MuxData) {
         MuxData muxData = (MuxData) data;
        
         MuxLookupKey key = new MuxLookupKey();
         key.channel = muxData.getChannelNumber();
         key.rt = muxData.getRemoteTerminalNumber();
         key.receiveTransmit = muxData.getReceiveTransmitFlag();
         key.subaddress = muxData.getSubaddressNumber();
         
         WeakReference<MessageData> weakData = new WeakReference<MessageData>(data);
         lookup.put(key, weakData);
      }
   }
}
