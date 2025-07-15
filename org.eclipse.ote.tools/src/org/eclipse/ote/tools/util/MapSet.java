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

package org.eclipse.ote.tools.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Interface for an object that maps keys to {@link Set} collections of values. The interface extends the interface for
 * {@link Map}<code>&lt;K,{@link Set}&lt;V&gt;&gt;</code> with additional methods for directly accessing or adding
 * values to the collections contained within the map.
 *
 * @author Loren K. Ashley
 * @param <K> the map key type.
 * @param <V> the type of value saved in the {@link Set} collections associated with the map keys.
 */

public interface MapSet<K, V> extends MapCollection<K, V, Set<V>> {

   /**
    * Creates an immutable map of immutable sets from the <code>entries</code>. The {@link Set} implementations in the
    * <code>entries</code> are copied to new immutable {@link Set}s. So the collections in the returned {@link MapSet}
    * are independent from the collections provided in the <code>entries</code>. Changes to the provided collections
    * will not be reflected in the returned {@link MapSet}. The {@link Set} values are not copies of the original
    * {@link Set} values. Changes to values will be reflected in the values of the {@link MapSet} and vice versa.
    *
    * @param <K> the map key type.
    * @param <V> the type of value saved in the {@link List} collections associated with the map keys.
    * @param entries {@link Map.Entry}s containing the keys and {@link List} collections the map is populated with.
    * @return an immutable {@link MapList} containing the specified mappings.
    */

   @SafeVarargs
   static <K, V> MapSet<K, V> ofEntries(Map.Entry<K, Set<V>>... entries) {

      if (entries == null) {
         throw new NullPointerException("MapSet::ofEntries, parameter \"entries\" is null");
      }

      Map<K, Set<V>> newMap = new HashMap<>(entries.length);

      for (Map.Entry<K, Set<V>> entry : entries) {
         K key = entry.getKey();
         Set<V> set = entry.getValue();
         Set<V> newSet = new HashSet<V>(set);
         Set<V> immutableSet = Collections.unmodifiableSet(newSet);
         newMap.put(key, immutableSet);
      }

      MapSet<K, V> mapSet = new AbstractImmutableMapSet<K, V>(newMap);

      return mapSet;
   }

}

/* EOF */
