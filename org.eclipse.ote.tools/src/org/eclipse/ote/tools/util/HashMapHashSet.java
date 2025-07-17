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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of the {@link MapCollection} interface using a {@link HashMap} for the primary {@link Map} and
 * {@link HashSet} for the {@link Collection} implementation.
 *
 * @author Loren K. Ashley
 * @param <K> the type of keys used by the map.
 * @param <V> the type of value saved in the {@link HashSet} collections associated with the map keys.
 */

public class HashMapHashSet<K, V> extends AbstractMapCollection<K, V, Set<V>> implements MapSet<K, V> {

   /**
    * Creates the primary {@link HashMap} with a default capacity and load factor. New {@link HashSet} collections
    * created by this object are also created with the default capacity and load factor for {@link HashSet} objects.
    */

   public HashMapHashSet() {
      super(HashMap::new, HashSet::new);
   }

   /**
    * Creates the primary {@link HashMap} with an initial capacity of <code>mapInitialCapacity</code> and the default
    * load factor. New {@link HashSet} collections created by this object are created with an initial capacity of
    * <code>collectionInitialCapacity</code> and the default load factor for {@link HashSet} objects.
    *
    * @param mapInitialCapacity the initial capacity of the primary {@link HashMap}.
    * @param collectionInitialCapacity the initial capacity of the {@link HashSet} collections created by this object.
    */

   public HashMapHashSet(int mapInitialCapacity, int collectionInitialCapacity) {
      super(() -> new HashMap<>(mapInitialCapacity), () -> new HashSet<>(collectionInitialCapacity));
   }

   /**
    * Creates the primary {@link HashMap} with an initial capacity of <code>mapInitialCapacity</code> and a load factor
    * of <code>mapLoadFactor</code>. New {@link HashSet} collections created by this object are created with an initial
    * capacity of <code>collectionInitialCapacity</code> and a load factor of <code>collectionLoadFactor</code>.
    *
    * @param mapInitialCapacity the initial capacity of the primary {@link HashMap}.
    * @param mapLoadFactor the load factor for the primary {@link HashMap}.
    * @param collectionInitialCapacity the initial capacity of the {@link HashSet} collections created by this object.
    * @param collectionLoadFactor the load factor for the {@link HashSet} collections created by this object.
    */

   public HashMapHashSet(int mapInitialCapacity, float mapLoadFactor, int collectionInitialCapacity, float collectionLoadFactor) {
      super(() -> new HashMap<>(mapInitialCapacity, mapLoadFactor),
         () -> new HashSet<>(collectionInitialCapacity, collectionLoadFactor));
   }

   /**
    * Creates the primary {@link HashMap} with an initial capacity that matches the number of collections in the
    * provided <code>mapCollection</code> and the default load factor. New {@link HashSet} collections created by this
    * object are created with the default initial capacity and load factor for {@link HashSet} objects.
    * <p>
    * For each {@link Collection} in the provided {@code mapCollection}, a new {@link HashSet} is created and filled
    * with the values from that {@link Collection}. The new {@link HashSet}s are associated with the same key as their
    * corresponding {@link Collection}s were in the provided {@code mapCollection}.
    *
    * @param mapCollection the {@link MapCollection} to copy the key and value references from.
    */

   public HashMapHashSet(MapCollection<K, V, ? extends Collection<V>> mapCollection) {
      super(() -> new HashMap<>(mapCollection.size()), HashSet::new);
      //@formatter:off
      mapCollection
         .streamAllCollectionValuesAsEntries()
         .forEach( this::putEntry );
      //@formatter:on
   }

}

/* EOF */
