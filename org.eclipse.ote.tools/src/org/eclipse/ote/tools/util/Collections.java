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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.ote.tools.util.function.TriConsumer;

/**
 * A class of static methods to support the collection classes in the package {@link org.eclipse.ote.tools.util}.
 *
 * @author Loren K. Ashley
 */

public class Collections {

   /**
    * Private static unmodifiable {@link DoubleMap} implementation used to wrap a modifiable {@link DoubleMap}
    * implementation.
    *
    * @param <Kp> the type of the primary key.
    * @param <Ks> the type of the secondary key.
    * @param <V> the type of the value.
    */

   private static class UnmodifiableDoubleMap<Kp, Ks, V> implements DoubleMap<Kp, Ks, V> {

      /**
       * Saves the wrapped mutable {@link DoubleMap}.
       */

      private final DoubleMap<Kp, Ks, V> doubleMap;

      /**
       * Creates an unmodifiable {@link DoubleMap} wrapper for the {@code mutableDoubleMap}.
       *
       * @param mutableDoubleMap the mutable {@link DoubleMap} implementation to be wrapped.
       */

      @SuppressWarnings("unchecked")
      UnmodifiableDoubleMap(DoubleMap<? extends Kp, ? extends Ks, ? extends V> mutableDoubleMap) {
         this.doubleMap = (DoubleMap<Kp, Ks, V>) Objects.requireNonNull(mutableDoubleMap);
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public boolean containsKey(Kp primaryKey) {
         return doubleMap.containsKey(primaryKey);
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public boolean containsKey(Kp primaryKey, Ks secondaryKey) {
         return doubleMap.containsKey(primaryKey, secondaryKey);
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public boolean containsValue(V val) {
         return doubleMap.containsValue(val);
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Set<DoubleMap.Entry<Kp, Ks, V>> entrySet() {
         return java.util.Collections.unmodifiableSet(this.doubleMap.entrySet());
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Set<Map.Entry<Ks, V>> entrySet(Kp primaryKey) {
         return java.util.Collections.unmodifiableSet(this.doubleMap.entrySet(primaryKey));
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public void forEach(Consumer<Entry<? extends Kp, ? extends Ks, ? extends V>> action) {
         this.doubleMap.forEach(action);
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public void forEach(TriConsumer<? super Kp, ? super Ks, ? super V> action) {
         this.doubleMap.forEach(action);
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Map<Ks, V> get(Kp primaryKey) {
         return this.doubleMap.get(primaryKey);
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public V get(Kp primaryKey, Ks secondaryKey) {
         return this.doubleMap.get(primaryKey, secondaryKey);
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public boolean isEmpty() {
         return this.doubleMap.isEmpty();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Set<Kp> keySet() {
         return java.util.Collections.unmodifiableSet(this.doubleMap.keySet());
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Set<Ks> keySet(Kp primaryKey) {
         return java.util.Collections.unmodifiableSet(this.doubleMap.keySet(primaryKey));
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public V put(Entry<? extends Kp, ? extends Ks, ? extends V> entry) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public V put(Kp primaryKey, Ks secondaryKey, V value) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Map<Ks, V> put(Kp primaryKey, Map<Ks, V> secondaryMap) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public void putAll(DoubleMap<? extends Kp, ? extends Ks, ? extends V> doubleMap) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public void putAll(Kp primaryKey, Map<? extends Ks, ? extends V> secondaryMap) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public V putIfAbsent(Entry<? extends Kp, ? extends Ks, ? extends V> entry) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public V putIfAbsent(Kp primaryKey, Ks secondaryKey, V value) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Map<Ks, V> putIfAbsent(Kp primaryKey, Map<Ks, V> secondaryMap) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Map<Ks, V> remove(Kp primaryKey) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public V remove(Kp primaryKey, Ks secondaryKey) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public int size() {
         return this.doubleMap.size();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public int size(Kp primaryKey) {
         return this.doubleMap.size(primaryKey);
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Collection<V> values() {
         return java.util.Collections.unmodifiableCollection(this.doubleMap.values());
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Collection<V> values(Kp primaryKey) {
         return java.util.Collections.unmodifiableCollection(this.doubleMap.values(primaryKey));
      }

   }

   /**
    * Private static unmodifiable {@link DoubleMapCollection} implementation used to wrap a modifiable
    * {@link DoubleMapCollection} implementation.
    *
    * @param <Kp> the type of the primary key.
    * @param <Ks> the type of the secondary key.
    * @param <V> the type of the values within the collections that are used as the map values.
    * @param <C> the type of the collections.
    */

   private static class UnmodifiableDoubleMapCollection<Kp, Ks, V, C extends Collection<V>> extends AbstractDoubleMapCollection<Kp, Ks, V, C> {

      /**
       * Creates an unmodifiable {@link DoubleMapCollection} wrapper for the {@code mutableDoubleMapCollection}.
       *
       * @param mutableDoubleMapCollection the mutable {@link DoubleMapCollection} implementation to be wrapped.
       */

      @SuppressWarnings("unchecked")
      UnmodifiableDoubleMapCollection(DoubleMapCollection<? extends Kp, ? extends Ks, ? extends V, C> mutableDoubleMapCollection) {
         super((DoubleMapCollection<Kp, Ks, V, C>) Objects.requireNonNull(mutableDoubleMapCollection));
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C put(Entry<? extends Kp, ? extends Ks, ? extends C> entry) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C put(Kp primaryKey, Ks secondaryKey, C value) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Map<Ks, C> put(Kp primaryKey, Map<Ks, C> secondaryMap) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public void putAll(DoubleMap<? extends Kp, ? extends Ks, ? extends C> m) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C putAll(Kp primaryKey, Ks secondaryKey, C values) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public void putAll(Kp primaryKey, Map<? extends Ks, ? extends C> secondaryMap) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C putEntry(Entry<Kp, Ks, V> entry) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C putIfAbsent(Entry<? extends Kp, ? extends Ks, ? extends C> entry) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C putIfAbsent(Kp primaryKey, Ks secondaryKey, C value) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Map<Ks, C> putIfAbsent(Kp primaryKey, Map<Ks, C> secondaryMap) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C putValue(Kp primaryKey, Ks secondaryKey, V value) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public Map<Ks, C> remove(Kp primaryKey) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C remove(Kp primaryKey, Ks secondaryKey) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public boolean removeEntry(DoubleMap.Entry<Kp, Ks, V> entry) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public boolean removeValue(Kp primaryKey, Ks secondaryKey, V value) {
         throw new UnsupportedOperationException();
      }

   }

   /**
    * Private static unmodifiable {@link DoubleMapSet} implementation used to wrap a modifiable {@link DoubleMapSet}
    * implementation.
    *
    * @param <Kp> the type of the primary key.
    * @param <Ks> the type of the secondary key.
    * @param <V> the type of the values within the sets that are used as the map values.
    */

   private static class UnmodifiableDoubleMapSet<Kp, Ks, V> extends UnmodifiableDoubleMapCollection<Kp, Ks, V, Set<V>> implements DoubleMapSet<Kp, Ks, V> {

      /**
       * Creates an unmodifiable {@link DoubleMapSet} wrapper for the {@code mutableDoubleMapSet}.
       *
       * @param mutableDoubleMapSet the mutable {@link DoubleMapSet} implementation to be wrapped.
       */

      @SuppressWarnings("unchecked")
      UnmodifiableDoubleMapSet(DoubleMapSet<? extends Kp, ? extends Ks, ? extends V> doubleMapSet) {
         super((DoubleMapSet<Kp, Ks, V>) Objects.requireNonNull(doubleMapSet));
      }

   }

   /**
    * Private static unmodifiable {@link MapCollection} implementation used to wrap a modifiable {@link MapCollection}
    * implementation.
    *
    * @param <K> the type of the primary key.
    * @param <V> the type of the values within the collections that are used as the map values.
    * @param <C> the type of the collections.
    */

   private static class UnmodifiableMapCollection<K, V, C extends Collection<V>> extends AbstractMapCollection<K, V, C> {

      @SuppressWarnings("unchecked")
      UnmodifiableMapCollection(MapCollection<? extends K, ? extends V, C> mutableMapCollection) {
         super((MapCollection<K, V, C>) Objects.requireNonNull(mutableMapCollection));
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public void clear() {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C put(K key, C value) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C putAll(K key, C values) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public void putAll(Map<? extends K, ? extends C> m) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C putEntry(Entry<K, V> entry) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C putValue(K key, V value) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public C remove(Object key) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public boolean removeEntry(Map.Entry<K, V> entry) {
         throw new UnsupportedOperationException();
      }

      /**
       * Throws {@link UnsupportedOperationException}.
       * <p>
       * {@inheritDoc}
       *
       * @throws UnsupportedOperationException
       */

      @Override
      public boolean removeValue(K key, V value) {
         throw new UnsupportedOperationException();
      }

   }

   /**
    * Private static unmodifiable {@link MapSet} implementation used to wrap a modifiable {@link MapSet} implementation.
    *
    * @param <K> the type of the key.
    * @param <V> the type of the values within the sets that are used as the map values.
    */

   private static class UnmodifiableMapSet<K, V> extends UnmodifiableMapCollection<K, V, Set<V>> implements MapSet<K, V> {

      /**
       * Creates a new unmodifiable {@link MapSet} wrapper for the {@code mutableMapSet}.
       *
       * @param mutableMapSet the mutable {@link MapSet} implementation to be wrapped.
       */

      @SuppressWarnings("unchecked")
      UnmodifiableMapSet(MapSet<? extends K, ? extends V> mapSet) {
         super((MapSet<K, V>) Objects.requireNonNull(mapSet));
      }

   }

   /**
    * Returns an unmodifiable view of a {@link DoubleMap} implementation.
    *
    * @param <Kp> the type of the primary map key.
    * @Param <Ks> the type of the secondary map key.
    * @param <V> the type of the map values.
    * @param mutableDoubleMap the {@link DoubleMap} implementation to be wrapped in an unmodifiable {@link DoubleMap}
    * wrapper.
    * @return an unmodifiable view of the {@code mutableDoubleMap}.
    */

   public static <Kp, Ks, V> DoubleMap<Kp, Ks, V> unmodifiableDoubleMap(DoubleMap<? extends Kp, ? extends Ks, ? extends V> mutableDoubleMap) {
      return new UnmodifiableDoubleMap<>(mutableDoubleMap);
   }

   /**
    * Returns an unmodifiable view of a {@link DoubleMapCollection} implementation.
    *
    * @param <Kp> the type of the primary map key.
    * @Param <Ks> the type of the secondary map key.
    * @param <V> the type of the values within the collections that are used as the map values.
    * @param <C> the type of the collections.
    * @param mutableDoubleMapCollection the {@link DoubleMapCollection} implementation to be wrapped in an unmodifiable
    * {@link DoubleMapCollection} wrapper.
    * @return an unmodifiable view of the {@code mutableDoubleMapCollection}.
    */

   public static <Kp, Ks, V, C extends Collection<V>> DoubleMapCollection<Kp, Ks, V, C> unmodifiableDoubleMapCollection(DoubleMapCollection<? extends Kp, ? extends Ks, ? extends V, C> mutableDoubleMapCollection) {
      return new UnmodifiableDoubleMapCollection<>(mutableDoubleMapCollection);
   }

   /**
    * Returns an unmodifiable view of a {@link DoubleMapSet} implementation.
    *
    * @param <Kp> the type of the primary map key.
    * @Param <Ks> the type of the secondary map key.
    * @param <V> the type of the values within the sets that are used as the map values.
    * @param mutableDoubleMapSet the {@link DoubleMapSet} implementation to be wrapped in an unmodifiable
    * {@link DoubleMapSet} wrapper.
    * @return an unmodifiable view of the {@code mutableDoubleMapSet}.
    */

   public static <Kp, Ks, V> DoubleMapSet<Kp, Ks, V> unmodifiableDoubleMapSet(DoubleMapSet<? extends Kp, ? extends Ks, ? extends V> mutableDoubleMapSet) {
      return new UnmodifiableDoubleMapSet<>(mutableDoubleMapSet);
   }

   /**
    * Returns an unmodifiable view of a {@link MapCollection} implementation.
    *
    * @param <K> the type of the map key.
    * @param <V> the type of the values within the collections that are used as the map values.
    * @param <C> the type of the collections.
    * @param mutableMapCollection the {@link MapCollection} implementation to be wrapped in an unmodifiable
    * {@link MapCollection} wrapper.
    * @return an unmodifiable view of the {@code mutableMapCollection}.
    */

   public static <K, V, C extends Collection<V>> MapCollection<K, V, C> unmodifiableMapCollection(MapCollection<? extends K, ? extends V, C> mutableMapCollection) {
      return new UnmodifiableMapCollection<>(mutableMapCollection);
   }

   /**
    * Returns an unmodifiable view of a {@link MapSet} implementation.
    *
    * @param <K> the type of the map key.
    * @param <V> the type of the values within the sets that are used as the map values.
    * @param mutableMapSet the {@link MapSet} implementation to be wrapped in an unmodifiable {@link MapSet} wrapper.
    * @return an unmodifiable view of the {@code mutableMapSet}.
    */

   public static <K, V> MapSet<K, V> unmodifiableMapSet(MapSet<? extends K, ? extends V> mutableMapSet) {
      return new UnmodifiableMapSet<>(mutableMapSet);
   }

}
