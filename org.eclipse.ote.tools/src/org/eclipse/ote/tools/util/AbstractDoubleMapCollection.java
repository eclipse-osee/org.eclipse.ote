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
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.eclipse.ote.tools.util.function.TriConsumer;

/**
 * This class provides a complete generic implementation of the {@link DoubleMapCollection} interface. The methods of
 * this class may be overridden by the implementation if a more efficient implementation for the {@link Collection} type
 * is possible.
 *
 * @author Loren K. Ashley
 * @param <Kp> the map primary key type.
 * @param <Ks> the map secondary key type.
 * @param <V> the type of value saved in the collections associated with the map keys.
 * @param <C> the type of collection associated with the map keys.
 */

public class AbstractDoubleMapCollection<Kp, Ks, V, C extends Collection<V>> implements DoubleMapCollection<Kp, Ks, V, C> {

   /**
    * A {@link Supplier} of new empty {@link Collection} objects of type <code>C</code>.
    */

   protected final Supplier<C> collectionSupplier;

   /**
    * Saves the map of collections.
    */

   protected final DoubleMap<Kp, Ks, C> doubleMapCollection;

   /**
    * Creates a new empty {@link DoubleMap} or {@link Collection} objects without a collection supplier for immutable
    * map collections.
    *
    * @param doubleMapCollection the {@link DoubleMap} of {@link Collection}s to be encapsulated.
    */

   public AbstractDoubleMapCollection(DoubleMap<Kp, Ks, C> doubleMapCollection) {
      this.collectionSupplier = null;
      this.doubleMapCollection = doubleMapCollection;
   }

   /**
    * Creates a new empty {@link DoubleMap} of {@link Collection} objects.
    *
    * @param doubleMapCollectionSupplier a {@link Supplier} that provides an implementation of the {@link DoubleMap}
    * interface to be used as the {@link DoubleMap} of {@link Collection} objects for this object.
    * @param collectionSupplier a {@link Supplier} that provides new empty implementation of the {@link Collection}
    * interface for the collections saved in this object.
    * @throws NullPointerException when <code>mapCollectionSupplier</code> is <code>null</code>,
    * <code>collectionSupper</code> is <code>null</code>, or <code>mapCollectionSupplier</code> returns
    * <code>null</code>.
    */

   public AbstractDoubleMapCollection(Supplier<DoubleMap<Kp, Ks, C>> doubleMapCollectionSupplier, Supplier<C> collectionSupplier) {
      this.collectionSupplier = Objects.requireNonNull(collectionSupplier);
      this.doubleMapCollection = Objects.requireNonNull(Objects.requireNonNull(doubleMapCollectionSupplier)).get();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void clear() {
      this.doubleMapCollection.clear();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsKey(Kp primaryKey) {
      return this.doubleMapCollection.containsKey(primaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsKey(Kp primaryKey, Ks secondaryKey) {
      return this.doubleMapCollection.containsKey(primaryKey, secondaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsValue(C value) {
      return this.doubleMapCollection.containsValue(value);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsValueInAnyCollection(V value) {
      for (Collection<V> collection : this.doubleMapCollection.values()) {
         if (collection.contains(value)) {
            return true;
         }
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean containsValueInCollection(Kp primaryKey, V value) {
      Map<Ks, C> secondaryMap = this.doubleMapCollection.get(primaryKey);
      if (Objects.isNull(secondaryMap)) {
         return false;
      }
      for (Collection<V> collection : secondaryMap.values()) {
         if (collection.contains(value)) {
            return true;
         }
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Set<Entry<Kp, Ks, C>> entrySet() {
      return this.doubleMapCollection.entrySet();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Set<Map.Entry<Ks, C>> entrySet(Kp primaryKey) {
      return this.doubleMapCollection.entrySet(primaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void forEach(Consumer<DoubleMap.Entry<? extends Kp, ? extends Ks, ? extends C>> action) {
      this.doubleMapCollection.forEach(action);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void forEach(TriConsumer<? super Kp, ? super Ks, ? super C> action) {
      this.doubleMapCollection.forEach(action);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void forEachEntry(Consumer<DoubleMap.Entry<Kp, Ks, V>> action) {
      //@formatter:off
      this.doubleMapCollection.forEach
         (
            ( primaryKey, secondaryKey, collection ) ->
               collection.forEach
                  (
                     ( value ) -> action.accept( DoubleMap.entry( primaryKey, secondaryKey, value ) )
                  )
         );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void forEachEntry(Kp primaryKey, Consumer<DoubleMap.Entry<Kp, Ks, V>> action) {
      Map<Ks, C> secondaryMap = this.doubleMapCollection.get(primaryKey);
      if (Objects.isNull(secondaryMap)) {
         return;
      }
      //@formatter:off
      secondaryMap.forEach
         (
            ( secondaryKey, collection ) ->
               collection.forEach
                  (
                     ( value ) -> action.accept( DoubleMap.entry( primaryKey, secondaryKey, value ) )
                  )
         );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void forEachEntry(Kp primaryKey, Ks secondaryKey, Consumer<DoubleMap.Entry<Kp, Ks, V>> action) {
      Collection<V> collection = this.doubleMapCollection.get(primaryKey, secondaryKey);
      if (Objects.isNull(collection)) {
         return;
      }
      //@formatter:off
      collection.forEach
         (
            ( value ) -> action.accept( DoubleMap.entry( primaryKey, secondaryKey, value ) )
         );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void forEachValue(Kp primaryKey, BiConsumer<Ks, V> action) {
      Map<Ks, C> secondaryMap = this.doubleMapCollection.get(primaryKey);
      if (Objects.isNull(secondaryMap)) {
         return;
      }
      //@formatter:off
      secondaryMap.forEach
         (
            ( secondaryKey, collection ) ->
               collection.forEach
                  (
                     ( value ) -> action.accept( secondaryKey, value )
                  )
         );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void forEachValue(Kp primaryKey, Ks secondaryKey, Consumer<V> action) {
      Collection<V> collection = this.doubleMapCollection.get(primaryKey, secondaryKey);
      if (Objects.isNull(collection)) {
         return;
      }
      collection.forEach(action::accept);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void forEachValue(TriConsumer<Kp, Ks, V> action) {
      //@formatter:off
      this.doubleMapCollection.forEach
         (
            ( primaryKey, secondaryKey, collection ) ->
               collection.forEach
                  (
                     ( value ) -> action.accept( primaryKey, secondaryKey, value )
                  )
         );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Map<Ks, C> get(Kp primaryKey) {
      return this.doubleMapCollection.get(primaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C get(Kp primaryKey, Ks secondaryKey) {
      return this.doubleMapCollection.get(primaryKey, secondaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Optional<C> getOptional(Kp primaryKey, Ks secondaryKey) {
      return Optional.ofNullable(this.doubleMapCollection.get(primaryKey, secondaryKey));
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isEmpty() {
      return this.doubleMapCollection.isEmpty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Set<Kp> keySet() {
      return this.doubleMapCollection.keySet();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Set<Ks> keySet(Kp primaryKey) {
      return this.doubleMapCollection.keySet(primaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C put(DoubleMap.Entry<? extends Kp, ? extends Ks, ? extends C> entry) {
      return this.doubleMapCollection.put(entry);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C put(Kp primaryKey, Ks secondaryKey, C value) {
      return this.doubleMapCollection.put(primaryKey, secondaryKey, value);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Map<Ks, C> put(Kp primaryKey, Map<Ks, C> secondaryMap) {
      return this.doubleMapCollection.put(primaryKey, secondaryMap);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void putAll(DoubleMap<? extends Kp, ? extends Ks, ? extends C> doubleMap) {
      this.doubleMapCollection.putAll(doubleMap);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C putAll(Kp primaryKey, Ks secondaryKey, C values) {
      C collection = this.doubleMapCollection.get(primaryKey, secondaryKey);
      if (Objects.isNull(collection)) {
         collection = this.collectionSupplier.get();
         this.doubleMapCollection.put(primaryKey, secondaryKey, collection);
      }
      collection.addAll(values);
      return collection;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void putAll(Kp primaryKey, Map<? extends Ks, ? extends C> secondaryMap) {
      this.doubleMapCollection.putAll(primaryKey, secondaryMap);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C putEntry(Entry<Kp, Ks, V> entry) {
      return this.putValue(entry.getPrimaryKey(), entry.getSecondaryKey(), entry.getValue());
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C putIfAbsent(Entry<? extends Kp, ? extends Ks, ? extends C> entry) {
      return this.doubleMapCollection.putIfAbsent(entry);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C putIfAbsent(Kp primaryKey, Ks secondaryKey, C value) {
      return this.doubleMapCollection.putIfAbsent(primaryKey, secondaryKey, value);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Map<Ks, C> putIfAbsent(Kp primaryKey, Map<Ks, C> secondaryMap) {
      return this.doubleMapCollection.putIfAbsent(primaryKey, secondaryMap);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C putValue(Kp primaryKey, Ks secondaryKey, V value) {
      C collection = this.doubleMapCollection.get(primaryKey, secondaryKey);
      if (Objects.isNull(collection)) {
         collection = this.collectionSupplier.get();
         this.doubleMapCollection.put(primaryKey, secondaryKey, collection);
      }
      collection.add(value);
      return collection;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Map<Ks, C> remove(Kp primaryKey) {
      return this.doubleMapCollection.remove(primaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public C remove(Kp primaryKey, Ks secondaryKey) {
      return this.doubleMapCollection.remove(primaryKey, secondaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean removeEntry(DoubleMap.Entry<Kp, Ks, V> entry) {
      //@formatter:off
      return
         Objects.nonNull( entry )
            ? this.removeValue( entry.getPrimaryKey(), entry.getSecondaryKey(), entry.getValue() )
            : false;
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean removeValue(Kp primaryKey, Ks secondaryKey, V value) {
      Collection<V> collection = this.doubleMapCollection.get(primaryKey, secondaryKey);
      if (Objects.nonNull(collection)) {
         if (collection.remove(value)) {
            if (collection.isEmpty()) {
               this.doubleMapCollection.remove(primaryKey, secondaryKey);
            }
            return true;
         }
      }
      return false;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size() {
      return this.doubleMapCollection.size();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int size(Kp primaryKey) {
      return this.doubleMapCollection.size(primaryKey);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int sizeValues() {
      int size = 0;
      for (Collection<V> collection : this.doubleMapCollection.values()) {
         size += collection.size();
      }
      return size;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int sizeValues(Kp primaryKey) {
      Map<Ks, C> secondaryMap = this.doubleMapCollection.get(primaryKey);
      if (Objects.isNull(secondaryMap)) {
         return 0;
      }
      int size = 0;
      for (Collection<V> collection : secondaryMap.values()) {
         size += collection.size();
      }
      return size;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int sizeValues(Kp primaryKey, Ks secondaryKey) {
      Collection<V> collection = this.doubleMapCollection.get(primaryKey, secondaryKey);
      //@formatter:off
      int size = Objects.nonNull( collection )
                    ? collection.size()
                    : 0;
      //@formatter:on
      return size;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<V> stream() {
      //@formatter:off
      return
         this.doubleMapCollection
            .values()
            .stream()
            .flatMap( C::stream );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<V> stream(Kp primaryKey) {
      //@formatter:off
      Map<Ks,C> secondaryMap = this.doubleMapCollection.get(primaryKey);
      return
         Objects.nonNull( secondaryMap )
            ? secondaryMap
                 .values()
                 .stream()
                 .flatMap( C::stream )
            : Stream.empty();
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<V> stream(Kp primaryKey, Ks secondaryKey) {
      Collection<V> collection = this.doubleMapCollection.get(primaryKey, secondaryKey);
      //@formatter:off
      return
         Objects.nonNull( collection )
            ? collection.stream()
            : Stream.empty();
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<DoubleMap.Entry<Kp, Ks, V>> streamAllCollectionValuesAsEntries() {
      //@formatter:off
      return
         this.doubleMapCollection
            .entrySet()
            .stream()
            .flatMap
               (
                  ( entry ) -> entry
                                .getValue()
                                .stream()
                                .map( ( value ) -> DoubleMap.entry( entry.getPrimaryKey(), entry.getSecondaryKey(), value ) )
               );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<C> streamCollections() {
      return this.doubleMapCollection.values().stream();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Stream<DoubleMap.Entry<Kp, Ks, C>> streamEntries() {
      return this.doubleMapCollection.entrySet().stream();
   }

   @Override
   public Stream<Kp> streamPrimaryKeys() {
      return this.doubleMapCollection.keySet().stream();
   }

   public Stream<Ks> streamSecondaryKeys(Kp primaryKey) {
      Map<Ks, C> secondaryMap = this.doubleMapCollection.get(primaryKey);
      return Objects.nonNull(secondaryMap) ? secondaryMap.keySet().stream() : Stream.empty();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Collection<C> values() {
      return this.doubleMapCollection.values();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Collection<C> values(Kp primaryKey) {
      return this.doubleMapCollection.values(primaryKey);
   }

}

/* EOF */
