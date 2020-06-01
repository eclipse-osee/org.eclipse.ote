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

package org.eclipse.ote.message.manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.interfaces.MessageDataLookup;

/**
 * This is a simple (and slow) implementation of a data lookup that uses the topic name as a hashmap
 * key. This class can be extended to provide message specific lookup for some of the API (like the
 * unimplemented getById) or lookups that perform faster than using a String hash. 
 * 
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public class MessageDataLookupImpl implements MessageDataLookup {

   private final Map<String, WeakReference<MessageData>> lookup = new ConcurrentHashMap<String, WeakReference<MessageData>>();

   @Override
   public Collection<MessageData> allValues() {
      List<MessageData> dataList = new ArrayList<MessageData>();
      for (WeakReference<MessageData> weakData : lookup.values()) {
         MessageData data = weakData.get();
         if (data != null) {
            dataList.add(data);
         }
      }
      return dataList;
   }

   @Override
   public MessageData getByName(String name) {
      WeakReference<MessageData> weakData = lookup.get(name);
      if (weakData != null) {
         MessageData data = weakData.get();
         if (data == null) {
            lookup.remove(name);
         }
         else {
            return data;
         }
      }
      return null;
   }

   @Override
   public void put(MessageData data) {
      lookup.put(data.getTopicName(), new WeakReference<MessageData>(data));
   }

   @Override
   public MessageData getById(int id) {
      return null;
   }

   public void clearAllMessages() {
      lookup.clear();
   }
}
