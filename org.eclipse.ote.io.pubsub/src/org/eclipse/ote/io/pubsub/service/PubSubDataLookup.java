/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.io.pubsub.service;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.ote.io.pubsub.PubSubData;
import org.eclipse.ote.message.manager.MessageDataLookupImpl;

/**
 * @author Michael P. Masterson
 */
public class PubSubDataLookup extends MessageDataLookupImpl {

   private final Map<Integer, WeakReference<MessageData>> lookup = new HashMap<>(32);

   @Override
   public MessageData getById(int id) {
      if (lookup.containsKey(id)) {
         return lookup.get(id).get();
      }

      return super.getById(id);
   }

   @Override
   public void put(MessageData data) {
      super.put(data);

      if (data instanceof PubSubData) {
         PubSubData pubsubData = (PubSubData) data;

         WeakReference<MessageData> weakData = new WeakReference<MessageData>(data);
         lookup.put(pubsubData.getId(), weakData);
      }
   }
}
