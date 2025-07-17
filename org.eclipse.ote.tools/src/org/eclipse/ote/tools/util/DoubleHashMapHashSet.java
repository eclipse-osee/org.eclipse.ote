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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of the {@link DoubleMapCollection} interface using a {@link DoubleHashMap} for the
 * {@link DoubleMap} and a {@link HashSet} for the {@link Collection} implementation.
 *
 * @author Loren K. Ashley
 * @param <Kp> the type of primary keys used by the map.
 * @param <Ks> the type of secondary keys used by the map.
 * @param <V> the type of values saved in the {@link HashSet} collections associated with the map keys.
 */

public class DoubleHashMapHashSet<Kp, Ks, V> extends AbstractDoubleMapCollection<Kp, Ks, V, Set<V>> implements DoubleMapSet<Kp, Ks, V> {

   /**
    * Creates the {@link DoubleHashMap} with a default capacity and load factor. New {@link HashSet} collections created
    * by this object are also created with the default capacity and load factor for {@link HashSet} objects.
    */

   public DoubleHashMapHashSet() {
      super(DoubleHashMap::new, HashSet::new);
   }

   /**
    * Creates the {@link DoubleHashMap} with an initial capacity of {@code primaryMapInitialCapacity} and
    * {@code secondaryMapInitialCapacity}. The {@link DoubleHashMap} primary and secondary maps will be created with the
    * default load factor for {@link HashMap} objects. New {@link HashSet} collections created by this object are
    * created with an initial capacity of {@code collectionInitialCapacity} and the default load factor for
    * {@link HashSet} objects.
    *
    * @param primaryMapInitialCapacity the initial capacity of the primary {@link HashMap}.
    * @param secondaryMapInitialCapacity the initial capacity for the secondary {@link HashMap}s.
    * @param collectionInitialCapacity the initial capacity of the {@link HashSet} collections created by this object.
    */

   public DoubleHashMapHashSet(int primaryMapInitialCapacity, int secondaryMapInitialCapacity, int collectionInitialCapacity) {
      //@formatter:off
      super
         (
            () -> new DoubleHashMap<>( primaryMapInitialCapacity, secondaryMapInitialCapacity ),
            () -> new HashSet<>( collectionInitialCapacity )
         );
      //@formatter:on
   }

   /**
    * Creates the {@link DoubleHashMap} primary map with an initial capacity of {@code primaryMapInitialCapacity} and a
    * load factor of {@link primaryMapLoadFactor}. The secondary maps will be created with an initial capacity of
    * {@code secondaryMapInitialCapacity} and a load factor of {@link secondaryMapLoadFactor}. New {@link HashSet}
    * collections created by this object are created with an initial capacity of {@code collectionInitialCapacity} and a
    * load factor of {@code collectionLoadFactor}.
    *
    * @param primaryMapInitialCapacity the initial capacity of the primary {@link HashMap}.
    * @param primaryMapLoadFactor the load factor for the primary {@link HashMap}.
    * @param secondaryMapInitialCapacity the initial capacity for the secondary {@link HashMap}s.
    * @param secondaryMapLoadFactor the load factor for the secondary {@link HashMap}s.
    * @param collectionInitialCapacity the initial capacity of the {@link HashSet} collections created by this object.
    * @param collectionLoadFactor the load factor for the {@link HashSet} collections created by this object.
    */

   //@formatter:off
   public
      DoubleHashMapHashSet
         (
            int   primaryMapInitialCapacity,
            float primaryMapLoadFactor,
            int   secondaryMapInitialCapacity,
            float secondaryMapLoadFactor,
            int   collectionInitialCapacity,
            float collectionLoadFactor
         ) {
      super
         (
            () -> new DoubleHashMap<>( primaryMapInitialCapacity, primaryMapLoadFactor, secondaryMapInitialCapacity, secondaryMapLoadFactor ),
            () -> new HashSet<>( collectionInitialCapacity, collectionLoadFactor )
         );
   }
   //@formatter:on

   /**
    * Creates the {@link DoubleHashMap} primary {@link HashMap} with an initial capacity that matches the number of
    * collections in the provided {@code doubleMapCollection} and the default load factor. The secondary
    * {@link HashMap}s will be created with an initial capacity that is the current average size of the secondary
    * mappings in the provided {@code doubleMapCollection}. The secondary {@link HashMap}s will be created with the
    * default load factor. New {@link HashSet} collections created by this object are created with the default initial
    * capacity and load factor for {@link HashSet} objects.
    * <p>
    * For each {@link Collection} in the provided {@code doubleMapCollection}, a new {@link HashSet} is created and
    * filled with the values from that {@link Collection}. The new {@link HashSet}s are associated with the same keys as
    * their corresponding {@link Collection}s were in the provided {@code doubleMapCollection}.
    *
    * @param doubleMapCollection the {@link DoubleMapCollection} to copy the key and value references from.
    */

   public DoubleHashMapHashSet(DoubleMapCollection<Kp, Ks, V, ? extends Collection<V>> doubleMapCollection) {
      //@formatter:off
      super
         (
            () -> new DoubleHashMap<>( doubleMapCollection.size(), doubleMapCollection.size() / doubleMapCollection.keySet().size() ),
            HashSet::new
         );
      doubleMapCollection
         .streamAllCollectionValuesAsEntries()
         .forEach( this::putEntry );
      //@formatter:on
   }

}
