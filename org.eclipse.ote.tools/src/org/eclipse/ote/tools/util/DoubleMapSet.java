/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.ote.tools.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Interface for an object that maps keys to {@link Set} collections of values. The interface extends the interface for
 * {@link DoubleMap}<code>&lt;Kp,Ks,{@link Set}&lt;V&gt;&gt;</code> with additional methods for directly accessing or
 * adding values to the collections contained within the map.
 *
 * @author Loren K. Ashley
 * @param <Kp> the primary key type.
 * @param <Ks> the secondary key type.
 * @param <V> the type of value saved in the {@link Set} collections associated with the map keys.
 */

public interface DoubleMapSet<Kp, Ks, V> extends DoubleMapCollection<Kp, Ks, V, Set<V>> {

   /**
    * Creates an immutable double map of immutable sets from the <code>entries</code>. The {@link Set} implementations
    * in the <code>entries</code> are copied to new immutable {@link Set}s. So the collections in the returned
    * {@link MapSet} are independent from the collections provided in the <code>entries</code>. Changes to the provided
    * collections will not be reflected in the returned {@link DoubleMapSet}. The {@link Set} values are not copies of
    * the original {@link Set} values. Changes to values will be reflected in the values of the {@link MapSet} and
    * vice-versa.
    *
    * @param <Kp> the primary key type.
    * @param <Ks> the secondary key type.
    * @param <V> the type of value saved in the {@link List} collections associated with the map keys.
    * @param entries {@link DoubleMap.Entry}s containing the keys and {@link Set} collections the map is populated with.
    * @return an immutable {@link DoubleMapSet} containing the specified mappings.
    */

   @SafeVarargs
   static <Kp, Ks, V> DoubleMapSet<Kp, Ks, V> ofEntries(DoubleMap.Entry<Kp, Ks, Set<V>>... entries) {

      if (entries == null) {
         throw new NullPointerException("DoubleMapSet::ofEntries, parameter \"entries\" is null");
      }

      DoubleMapSet<Kp, Ks, V> newMapSet = new DoubleHashMapHashSet<>(entries.length, entries.length, entries.length);

      for (DoubleMap.Entry<Kp, Ks, Set<V>> entry : entries) {
         Kp primaryKey = entry.getPrimaryKey();
         Ks secondaryKey = entry.getSecondaryKey();
         Set<V> set = entry.getValue();
         Set<V> newSet = new HashSet<V>(set);
         Set<V> immutableSet = java.util.Collections.unmodifiableSet(newSet);
         newMapSet.put(primaryKey, secondaryKey, immutableSet);
      }

      DoubleMapSet<Kp, Ks, V> mapSet = Collections.unmodifiableDoubleMapSet(newMapSet);

      return mapSet;
   }

}
