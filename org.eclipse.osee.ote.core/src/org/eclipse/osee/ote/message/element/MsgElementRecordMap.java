/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ote.message.element;

import java.util.HashMap;

/**
 * This element type represents a list of {@link MsgElementRecord} with a static length. The records within this object
 * will only be created when needed via the {@link #get(int)} method however the entire buffer for the entire list will
 * be created at construction time.
 * 
 * @author Michael P. Masterson
 * @param <S>
 */
public class MsgElementRecordMap<S extends MsgElementRecord> {
   private final int MAX_RECORDS;
   private final HashMap<Integer, S> records;
   private final MsgElementRecordFactory factory;

   public MsgElementRecordMap(int maxRecords, MsgElementRecordFactory factory) {
      this.MAX_RECORDS = maxRecords;
      this.records = new HashMap<>(maxRecords);
      this.factory = factory;
   }

   @SuppressWarnings("unchecked")
   public S get(int index) {
      if (index >= MAX_RECORDS) {
         throw new IllegalArgumentException(
            "index(zero-based):" + index + " is greater than MAX_RECORDS(one-based):" + MAX_RECORDS);
      }

      S val = records.get(index);
      if (val == null) {
         val = (S) factory.create(index);
         records.put(index, val);
      }
      return val;
   }

   public int length() {
      return this.MAX_RECORDS;
   }
}
