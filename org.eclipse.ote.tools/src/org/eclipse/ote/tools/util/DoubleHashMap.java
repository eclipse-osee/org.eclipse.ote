/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.ote.tools.util.function.TriConsumer;

/**
 * An implementation of the {@link DoubleMap} interface using primary and secondary {@link HashMap}s. This
 * implementation supports {@code null} keys and values.
 *
 * @author Loren K. Ashley
 * @param <Kp> the type of primary map keys.
 * @param <Ks> the type of the secondary map keys.
 * @param <V> the type of the secondary map values.
 */

public class DoubleHashMap<Kp, Ks, V> implements DoubleMap<Kp, Ks, V>, Serializable {

   /**
    * Default initial capacity for the primary map and secondary maps.
    */

   static final int defaultInitialCapacity = 16;

   /**
    * Default load factor for the primary map and secondary maps.
    */

   static final float defaultLoadFactor = 0.75f;

   /**
    * Serialization Identifier
    */

   private static final long serialVersionUID = 8914982043814177626L;

   /**
    * Non-serializable cache for the map's entry set view.
    */

   private transient Set<DoubleMap.Entry<Kp, Ks, V>> entrySet;

   /**
    * Saves the primary map.
    */

   private final HashMap<Kp, Map<Ks, V>> primaryMap;

   /**
    * The initial capacity secondary maps will be created with. This member is assigned from constructor parameters or
    * assigned to defaults.
    */

   private final int secondaryInitialCapacity;

   /**
    * The initial load factor secondary maps will be created with. This member is assigned from constructor parameters
    * or assigned to defaults.
    */

   private final float secondaryLoadFactor;

   /**
    * Non-serializable cache for the maps values view.
    */

   private transient Collection<V> values;

   /**
    * Creates a {@link DoubleHashMap} that uses the default {@link HashMap} initial capacity and load factor for the
    * primary and secondary maps.
    */

   public DoubleHashMap() {
      this.primaryMap = new HashMap<>(DoubleHashMap.defaultInitialCapacity, DoubleHashMap.defaultLoadFactor);
      this.secondaryInitialCapacity = DoubleHashMap.defaultInitialCapacity;
      this.secondaryLoadFactor = DoubleHashMap.defaultLoadFactor;
      this.entrySet = null;
      this.values = null;
   }

   /**
    * Creates a {@link DoubleHashMap} that uses the specified initial capacity and load factor for the primary and
    * secondary maps.
    *
    * @param initialCapacity the map or sub-map initial capacity.
    * @param loadFactor the map or sub-map load factor.
    */

   public DoubleHashMap(int initialCapacity, float loadFactor) {
      this.primaryMap = new HashMap<>(initialCapacity, loadFactor);
      this.secondaryInitialCapacity = initialCapacity;
      this.secondaryLoadFactor = loadFactor;
      this.entrySet = null;
      this.values = null;
   }

   /**
    * Creates a {@link DoubleHashMap} that uses the specified initial capacities and load factors for the primary and
    * secondary maps.
    *
    * @param primaryInitialCapacity the primary map initial capacity.
    * @param primaryLoadFactor the primary map load factor.
    * @param secondaryInitialCapacity the initial capacity for secondary maps.
    * @param secondaryLoadFactor the load factor for secondary maps.
    */

   public DoubleHashMap(int primaryInitialCapacity, float primaryLoadFactor, int secondaryInitialCapacity, float secondaryLoadFactor) {
      this.primaryMap = new HashMap<>(primaryInitialCapacity, primaryLoadFactor);
      this.secondaryInitialCapacity = secondaryInitialCapacity;
      this.secondaryLoadFactor = secondaryLoadFactor;
      this.entrySet = null;
      this.values = null;
   }

   /**
    * {@inheritDoc}
    *
    * @implNote Clears all entries from the primary map and secondary maps. Secondary maps obtained from or provided to
    * the map that are backed by the map will be cleared.
    */

   @Override
   public void clear() {
      this.primaryMap.values().forEach(Map::clear);
      this.primaryMap.clear();
   }

   /**
    * {@inheritDoc}
    *
    * @implNote This method will also return {@code true} when the {@code primaryKey} is explicitly associated with
    * {@code null}.
    */

   @Override
   public boolean containsKey(Kp primaryKey) {
      return this.primaryMap.containsKey(primaryKey);
   }

   /**
    * {@inheritDoc}
    *
    * @implNote This method will return {@code false} when the {@code primaryKey} is explicitly associated with
    * {@code null}. The method will return {@code true} when the {@code primaryKey} is associated with a secondary
    * mapping and the secondary key within the secondary mapping is explicitly associated with {@code null}.
    */

   @Override
   public boolean containsKey(Kp primaryKey, Ks secondaryKey) {

      Map<Ks, V> secondaryMap = this.primaryMap.get(primaryKey);

      return secondaryMap != null ? secondaryMap.containsKey(secondaryKey) : false;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsValue(V value) {

      for (Map<Ks, V> secondaryMap : this.primaryMap.values()) {
         if (secondaryMap.containsValue(value)) {
            return true;
         }
      }

      return false;
   }

   /**
    * {@inheritDoc}
    *
    * @implNote The {@link Entry} implementations in the {@link Set} view are immutable and not backed by the map. So an
    * entry obtained from the {@link Set} view will not reflect changes to the mapping represented by the entry when
    * that mapping is changed in the map.
    * @implNote The remove method of the set view iterator and the remove method of the {@link Set} are implemented.
    * Removal of entries via the remove methods will be reflected in the map. The add/put methods for the {@link Set}
    * will throw the {@link UnsupportedOperationException}.
    */

   @Override
   public Set<DoubleMap.Entry<Kp, Ks, V>> entrySet() {

      if (Objects.nonNull(this.entrySet)) {
         return this.entrySet;
      }

      return this.entrySet = new AbstractSet<DoubleMap.Entry<Kp, Ks, V>>() {

         @Override
         public boolean add(Entry<Kp, Ks, V> entry) {

            Kp primaryKey = entry.getPrimaryKey();
            Ks secondaryKey = entry.getSecondaryKey();
            V value = entry.getValue();

            if (!DoubleHashMap.this.containsKey(primaryKey, secondaryKey)) {
               DoubleHashMap.this.put(primaryKey, secondaryKey, value);
               return true;
            }

            V previousValue = DoubleHashMap.this.put(primaryKey, secondaryKey, value);

            //@formatter:off
            return
               ( previousValue == null )
                  ? ( value == null )
                  : previousValue.equals( value );
            //@formatter:on

         }

         @Override
         public Iterator<Entry<Kp, Ks, V>> iterator() {
            return new Iterator<Entry<Kp, Ks, V>>() {

               Kp lastPrimaryKey;

               Map<Ks, V> lastSecondaryMap;
               Iterator<Map.Entry<Kp, Map<Ks, V>>> primaryIterator =
                  DoubleHashMap.this.primaryMap.entrySet().iterator();

               Iterator<Map.Entry<Ks, V>> secondaryIterator = this.getSecondaryIterator();
               private Iterator<Map.Entry<Ks, V>> getSecondaryIterator() {

                  if (this.primaryIterator.hasNext()) {
                     Map.Entry<Kp, Map<Ks, V>> primaryEntry = this.primaryIterator.next();
                     this.lastPrimaryKey = primaryEntry.getKey();
                     this.lastSecondaryMap = primaryEntry.getValue();
                     return this.lastSecondaryMap.entrySet().iterator();
                  } else {
                     return null;
                  }
               }

               @Override
               public boolean hasNext() {
                  if (secondaryIterator == null) {
                     return false;
                  }

                  if (secondaryIterator.hasNext()) {
                     return true;
                  }

                  if (primaryIterator.hasNext()) {
                     secondaryIterator = getSecondaryIterator();

                     return this.hasNext();
                  }

                  secondaryIterator = null;

                  return false;
               }

               @Override
               public Entry<Kp, Ks, V> next() {

                  if (secondaryIterator == null) {
                     throw new NoSuchElementException();
                  }

                  Map.Entry<Ks, V> secondaryEntry = secondaryIterator.next();
                  return DoubleMap.entry(lastPrimaryKey, secondaryEntry.getKey(), secondaryEntry.getValue());
               }

               @Override
               public void remove() {
                  secondaryIterator.remove();
                  if (lastSecondaryMap.isEmpty()) {
                     primaryIterator.remove();
                  }
               }

            };
         }

         @Override
         public boolean remove(Object object) {

            if (!(object instanceof DoubleMap.Entry)) {
               return false;
            }

            @SuppressWarnings("unchecked")
            DoubleMap.Entry<Object, Object, Object> entry = (DoubleMap.Entry<Object, Object, Object>) object;

            Object primaryKey = entry.getPrimaryKey();
            Object secondaryKey = entry.getSecondaryKey();
            Object value = entry.getValue();

            if (!DoubleHashMap.this.primaryMap.containsKey(primaryKey)) {
               return false;
            }

            Map<Ks, V> secondaryMap = DoubleHashMap.this.primaryMap.get(primaryKey);

            if (Objects.isNull(secondaryMap)) {
               if (Objects.isNull(secondaryKey) && Objects.isNull(value)) {
                  DoubleHashMap.this.primaryMap.remove(primaryKey);
                  return true;
               }
               return false;
            }

            if (!secondaryMap.containsKey(secondaryKey)) {
               return false;
            }

            V mapValue = secondaryMap.get(secondaryKey);

            if (Objects.isNull(value) ? Objects.isNull(mapValue) : value.equals(mapValue)) {
               secondaryMap.remove(secondaryKey);
               if (secondaryMap.isEmpty()) {
                  DoubleHashMap.this.primaryMap.remove(primaryKey);
               }
               return true;
            }

            return false;
         }

         @Override
         public int size() {
            return DoubleHashMap.this.size();
         }

      };
   }

   /**
    * {@inheritDoc}
    *
    * @implNote The returned {@link Set} view for the secondary mapping is backed by the {@link HashMap} implementing
    * the secondary mapping. See {@link HashMap} for the implementation details.
    */

   @Override
   public Set<Map.Entry<Ks, V>> entrySet(Kp primaryKey) {
      Map<Ks, V> secondaryMap = this.primaryMap.get(primaryKey);
      if (Objects.isNull(secondaryMap)) {
         return null;
      }
      return secondaryMap.entrySet();
   }

   /**
    * {@inheritDoc}
    * <p>
    * The entries passed to the {@link Consumer} are not backed by the map.
    * <p>
    * Actions are performed in nested loops over the primary entry set for the outer loop and each secondary map entry
    * set for the inner loops. The outer and inner loops iterate in the entry set order of the primary and secondary
    * maps respectively.
    *
    * @throws NullPointerException when {@code action} is {@code null}.
    * @implNote The provided {@link Entry} implementations are
    */

   @Override
   public void forEach(Consumer<DoubleMap.Entry<? extends Kp, ? extends Ks, ? extends V>> action) {

      Objects.requireNonNull(action);

      for (Map.Entry<Kp, Map<Ks, V>> primaryEntry : this.primaryMap.entrySet()) {

         Kp primaryKey = primaryEntry.getKey();
         Map<Ks, V> secondaryMap = primaryEntry.getValue();

         for (Map.Entry<Ks, V> secondaryEntry : secondaryMap.entrySet()) {

            Ks secondaryKey = secondaryEntry.getKey();
            V value = secondaryEntry.getValue();

            action.accept(DoubleMap.entry(primaryKey, secondaryKey, value));
         }
      }
   }

   /**
    * {@inheritDoc}
    * <p>
    * Actions are performed in nested loops over the primary mappings for the outer loop and each secondary mapping for
    * the inner loops. The iteration order within a mapping is undefined.
    *
    * @throws NullPointerException when {@code action} is {@code null}.
    */

   @Override
   public void forEach(TriConsumer<? super Kp, ? super Ks, ? super V> action) {

      Objects.requireNonNull(action);

      for (Map.Entry<Kp, Map<Ks, V>> primaryEntry : this.primaryMap.entrySet()) {

         Kp primaryKey = primaryEntry.getKey();
         Map<Ks, V> secondaryMap = primaryEntry.getValue();

         for (Map.Entry<Ks, V> secondaryEntry : secondaryMap.entrySet()) {

            Ks secondaryKey = secondaryEntry.getKey();
            V value = secondaryEntry.getValue();

            action.accept(primaryKey, secondaryKey, value);
         }
      }
   }

   /**
    * {@inheritDoc}
    *
    * @implNote The returned {@link Map} of secondary mappings is implemented with a {@link HashMap} and is backed by
    * the {@link DoubleMap}. Changes made to the returned {@link Map} will be reflected in the {@link DoubleMap} and
    * vice-versa.
    */

   @Override
   public Map<Ks, V> get(Kp primaryKey) {

      return this.primaryMap.get(primaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public V get(Kp primaryKey, Ks secondaryKey) {

      Map<Ks, V> secondaryMap = this.primaryMap.get(primaryKey);

      return secondaryMap != null ? secondaryMap.get(secondaryKey) : null;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isEmpty() {
      return this.primaryMap.isEmpty();
   }

   /**
    * {@inheritDoc}
    *
    * @implNote The returned primary key {@link Set} is backed by the primary map which is implemented with a
    * {@link HashMap}. See {@link HashMap#keySet} for implementation details.
    */

   @Override
   public Set<Kp> keySet() {
      return primaryMap.keySet();
   }

   /**
    * {@inheritDoc}
    *
    * @implNote The returned secondary key {@link Set} is backed by the secondary map which is implemented with a
    * {@link HashMap}. See {@link HashMap#keySet} for implementation details.
    */

   @Override
   public Set<Ks> keySet(Kp primaryKey) {

      Map<Ks, V> secondaryMap = this.primaryMap.get(primaryKey);

      return secondaryMap != null ? secondaryMap.keySet() : null;
   }

   /**
    * {@inheritDoc}
    * <p>
    * When a secondary map is not associated with the primary key, a secondary map will be created and associated with
    * the primary key.
    *
    * @throws NullPointerException when {@code entry} is {@code null}.
    * @implNoe The provided {@code entry} is not incorporated into the map and changes to the {@code entry} will not be
    * reflected in the map and vice-versa.
    */

   @Override
   public V put(DoubleMap.Entry<? extends Kp, ? extends Ks, ? extends V> entry) {
      Objects.requireNonNull(entry);
      return this.put(entry.getPrimaryKey(), entry.getSecondaryKey(), entry.getValue());
   }

   /**
    * {@inheritDoc}
    * <p>
    * When a secondary map is not associated with the primary key, a secondary map will be created and associated with
    * the primary key.
    */

   @Override
   public V put(Kp primaryKey, Ks secondaryKey, V value) {

      Map<Ks, V> secondaryMap = this.primaryMap.get(primaryKey);

      if (secondaryMap == null) {

         secondaryMap = new HashMap<Ks, V>(this.secondaryInitialCapacity, this.secondaryLoadFactor);

         this.primaryMap.put(primaryKey, secondaryMap);

         secondaryMap.put(secondaryKey, value);

         return null;
      }

      V priorValue = secondaryMap.put(secondaryKey, value);

      return priorValue;
   }

   /**
    * {@inheritDoc}
    *
    * @implNote When the provided {@code secondaryMap} is implemented with a {@link HashMap}, the provided
    * {@code secondaryMap} is incorporated into the {@link DoubleHashMap}. Any changes to the {@code secondaryMap} will
    * be reflected in the {@link DoubleHashMap} and vice-versa. When the provided {@code secondaryMap} is not
    * implemented with a {@link HashMap}, the mappings in the provided {@code secondaryMap} are copied into a newly
    * created {@link HashMap} within the {@link DoubleHashMap}. In this case, changes to the {@code secondaryMap} will
    * not be reflected in the {@link DoubleHashMap} and vice-versa.
    * @implNote When a secondary mapping is replaced the {@link HashMap} that was used by the {@link DoubleHashMap} for
    * the secondary mappings is returned.
    * @implNote When the {@code secondaryMap} is implemented with a {@link HashMap} use
    * {@link DoubleHashMap#remove(Object)} and {@link DoubleHashMap#putAll(Object, Map)} to replace the secondary
    * mappings associated with a primary key by copying the mappings instead of by incorporating the provided
    * {@link HashMap} of secondary mappings.
    */

   @Override
   public Map<Ks, V> put(Kp primaryKey, Map<Ks, V> secondaryMap) {

      if (Objects.isNull(secondaryMap)) {
         Map<Ks, V> priorSecondaryMappings = this.get(primaryKey);
         this.put(primaryKey, null, null);
         return priorSecondaryMappings;
      }

      if (secondaryMap instanceof HashMap) {
         return this.primaryMap.put(primaryKey, secondaryMap);
      }

      Map<Ks, V> priorSecondaryMappings = this.remove(primaryKey);
      this.putAll(primaryKey, secondaryMap);

      return priorSecondaryMappings;
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointException when {@code doubleMap} is {@code null}.
    */

   @Override
   public void putAll(DoubleMap<? extends Kp, ? extends Ks, ? extends V> doubleMap) {
      Objects.requireNonNull(doubleMap);
      doubleMap.forEach((primaryKey, secondaryKey, value) -> this.put(primaryKey, secondaryKey, value));
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException when {@code secondaryMap} is {@code null}.
    */

   @Override
   public void putAll(Kp primaryKey, Map<? extends Ks, ? extends V> secondaryMap) {
      Objects.requireNonNull(secondaryMap);
      secondaryMap.forEach((secondaryKey, value) -> this.put(primaryKey, secondaryKey, value));
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException when {@code entry} is {@code null}.
    * @implNote The provided entry is not incorporated into the map. Changes to the entry will not be reflected in the
    * map and vice-versa.
    */

   @Override
   public V putIfAbsent(DoubleMap.Entry<? extends Kp, ? extends Ks, ? extends V> entry) {

      return this.putIfAbsent(entry.getPrimaryKey(), entry.getSecondaryKey(), entry.getValue());
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public V putIfAbsent(Kp primaryKey, Ks secondaryKey, V value) {

      Map<Ks, V> secondaryMap = this.primaryMap.get(primaryKey);

      if (secondaryMap == null) {

         secondaryMap = new HashMap<Ks, V>(this.secondaryInitialCapacity, this.secondaryLoadFactor);

         this.primaryMap.put(primaryKey, secondaryMap);

         secondaryMap.put(secondaryKey, value);

         return null;
      }

      V priorValue = secondaryMap.get(secondaryKey);

      if (priorValue != null) {

         return priorValue;

      }

      secondaryMap.put(secondaryKey, value);

      return null;
   }

   /**
    * {@inheritDoc}
    * <p>
    * When a new association is created with the {@code primaryKey} the {@code secondaryhMap} is incorporated into this
    * map. Changes to the {@code secondaryMap} will be reflected in this map and vice-versa.
    * <p>
    * When an association with the {@code primaryKey} already exists, the returned map is backed by this map. Changes to
    * the returned secondary mappings will be reflected in this map and vice-versa.
    */

   @Override
   public Map<Ks, V> putIfAbsent(Kp primaryKey, Map<Ks, V> secondaryMap) {

      Map<Ks, V> priorSecondaryMappings = this.primaryMap.get(primaryKey);

      if (priorSecondaryMappings != null) {
         return priorSecondaryMappings;
      }

      this.primaryMap.put(primaryKey, secondaryMap);

      return priorSecondaryMappings;
   }

   /**
    * {@inheritDoc}
    *
    * @implNote The returned map is the {@link HashMap} from the {@link DoubleMap} that contained the secondary mappings
    * that were removed.
    */

   @Override
   public Map<Ks, V> remove(Kp primaryKey) {

      return this.primaryMap.remove(primaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public V remove(Kp primaryKey, Ks secondaryKey) {

      if (!this.primaryMap.containsKey(primaryKey)) {
         return null;
      }

      Map<Ks, V> secondaryMap = this.primaryMap.get(primaryKey);

      if (secondaryMap == null) {
         this.primaryMap.remove(primaryKey);
         return null;
      }

      V priorValue = secondaryMap.remove(secondaryKey);

      if (secondaryMap.isEmpty()) {
         this.primaryMap.remove(primaryKey);
      }

      return priorValue;

   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {

      int size = 0;

      for (Map<Ks, V> secondaryMap : this.primaryMap.values()) {
         size += secondaryMap.size();
      }

      return size;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size(Kp primaryKey) {

      Map<Ks, V> secondaryMap = this.primaryMap.get(primaryKey);

      return secondaryMap != null ? secondaryMap.size() : 0;
   }

   /**
    * {@inheritDoc}
    *
    * @implNote The remove method of the collection view iterator and the remove method of the {@link Collection} are
    * implemented. Removal of values via the remove methods will be reflected in the map. The add/put methods for the
    * {@link Collection} will throw the {@link UnsupportedOperationException}.
    */

   @Override
   public Collection<V> values() {

      if (Objects.nonNull(this.values)) {
         return this.values;
      }

      return this.values = new AbstractCollection<V>() {

         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {

               Map<Ks, V> lastSecondaryMap;

               Iterator<Map.Entry<Kp, Map<Ks, V>>> primaryIterator =
                  DoubleHashMap.this.primaryMap.entrySet().iterator();

               Iterator<Map.Entry<Ks, V>> secondaryIterator = this.getSecondaryIterator();
               private Iterator<Map.Entry<Ks, V>> getSecondaryIterator() {

                  if (this.primaryIterator.hasNext()) {
                     Map.Entry<Kp, Map<Ks, V>> primaryEntry = this.primaryIterator.next();
                     this.lastSecondaryMap = primaryEntry.getValue();
                     return this.lastSecondaryMap.entrySet().iterator();
                  } else {
                     return null;
                  }
               }

               @Override
               public boolean hasNext() {
                  if (secondaryIterator == null) {
                     return false;
                  }

                  if (secondaryIterator.hasNext()) {
                     return true;
                  }

                  if (primaryIterator.hasNext()) {
                     secondaryIterator = getSecondaryIterator();

                     return this.hasNext();
                  }

                  secondaryIterator = null;

                  return false;
               }

               @Override
               public V next() {
                  if (secondaryIterator == null) {
                     throw new NoSuchElementException();
                  }

                  Map.Entry<Ks, V> secondaryEntry = secondaryIterator.next();
                  return secondaryEntry.getValue();
               }

               @Override
               public void remove() {
                  secondaryIterator.remove();
                  if (lastSecondaryMap.isEmpty()) {
                     primaryIterator.remove();
                  }
               }

            };
         }

         @Override
         public boolean remove(Object value) {

            boolean removed = false;

            for (Map<Ks, V> secondaryMap : DoubleHashMap.this.primaryMap.values()) {
               removed |= secondaryMap.values().remove(value);
            }

            return removed;
         }

         @Override
         public int size() {
            return DoubleHashMap.this.size();
         }
      };
   }

   /**
    * {@inheritDoc}
    *
    * @implNote The returned {@link Collection} view for the secondary mappings is backed by the {@link HashMap}
    * implementing the secondary mappings. See {@link HashMap} for the implementation details.
    */

   @Override
   public Collection<V> values(Kp primaryKey) {

      Map<Ks, V> secondaryMap = this.primaryMap.get(primaryKey);

      return secondaryMap != null ? secondaryMap.values() : null;
   }

}

/* EOF */
