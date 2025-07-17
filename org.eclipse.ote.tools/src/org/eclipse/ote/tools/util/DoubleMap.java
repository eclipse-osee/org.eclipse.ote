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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.ote.tools.util.function.TriConsumer;

/**
 * A two level map structure where the primary key selects a secondary mapping and the secondary key is used as the key
 * with the secondary mapping to select the associated value.
 *
 * @param <Kp> the type of primary map keys
 * @param <Ks> the type of the secondary map keys
 * @param <V> the type of the map values
 * @author Loren K. Ashley
 */

public interface DoubleMap<Kp, Ks, V> {

   /**
    * A double map entry (primary key, secondary key, value triplet). {@link Entry} instances may be obtained from
    * {@link DoubleMap} implementations or created with the method {@link DoubleMap#entry}.
    *
    * @param <Kp> the primary key type.
    * @param <Ks> the secondary key type.
    * @param <V> the value type.
    * @implSpec Whether the {@link Entry} is backed by the map or not is implementation and context dependent.
    */

   public interface Entry<Kp, Ks, V> {

      /**
       * Compares the specified object with this double map entry for equality.
       *
       * @param other the object to be compared for equality with this map entry.
       * @return {@code true} if the specified object is equal to this map entry.
       * @ImplSpec Implementations are expected to return {@code true} if the given {@code object} is also a
       * {@link DoubleMap.Entry} and the two entries represent the same mapping. More formally, two entries "this" and
       * "other" represent the same mapping when:
       *
       * <pre>
       * <code>
       *       ( other instanceof DoubleMap.Entry.class )
       *    && ( this.getPrimaryKey() == null
       *            ? other.getPrimaryKey() == null
       *            : this.getPrimaryKey().equals( other.getPrimaryKey() ) )
       *    && ( this.getSecondaryKey() == null
       *            ? other.getSecondaryKey() == null
       *            : this.getSecondaryKey().equals( other.getSecondaryKey() ) )
       *    && ( this.getValue() == null
       *            ? other.getValue() == null
       *            : this.getValue().equals( other.getValue() ) )
       * </code>
       * </pre>
       */

      @Override
      boolean equals(Object other);

      /**
       * Gets the primary key from this entry.
       *
       * @return the primary key.
       */

      Kp getPrimaryKey();

      /**
       * Gets the secondary key from this entry.
       *
       * @return the secondary key.
       */

      Ks getSecondaryKey();

      /**
       * Gets the value from this entry.
       *
       * @return the value.
       */

      V getValue();

      /**
       * Returns the hash code value for this double map entry.
       *
       * @return the hash code value for the double map entry.
       * @ImplSpec Implementation are expected to compute the hash code as follows:
       *
       * <pre>
       * <code>
       *      (e.getPrimaryKey()   == null ? 0 : e.getPrimaryKey().hashCode()   )
       *    ^ (e.getSecondaryKey() == null ? 0 : e.getSecondaryKey().hashCode() )
       *    ^ (e.getValue()        == null ? 0 : e.getValue().hashCode()        )
       * </code>
       * </pre>
       *
       * This ensures that e1.equals(e2) implies that e1.hashCode() == e2.hashCode() for any two double map entries e1
       * and e2.
       */

      @Override
      int hashCode();
   }

   /**
    * Returns an immutable {@link Entry} containing the given keys and value. The {@link Entry}s created by this method
    * are suitable for populating {@link DoubleMap} instances.
    *
    * @param <Kp> the primary key type.
    * @param <Ks> the secondary key type.
    * @param <V> the value type.
    * @param primaryKey the primary key.
    * @param secondaryKey the secondary key.
    * @param value the value.
    * @return an {@code Entry} containing the specified keys and value.
    * @ImplNote The {@link Entry} implementation created by this method allows {@link Null} keys and value.
    */

   static <Kp, Ks, V> Entry<Kp, Ks, V> entry(Kp primaryKey, Ks secondaryKey, V value) {
      //@formatter:off
      return
         new Entry<Kp,Ks,V>() {

            private final Kp entryPrimaryKey = primaryKey;
            private final Ks entrySecondaryKey = secondaryKey;
            private final V entryValue = value;

            @Override
            public boolean equals(Object other) {

               if( !(other instanceof DoubleMap.Entry ) ) {
                  return false;
               }

               @SuppressWarnings("unchecked")
               Entry<Object,Object,Object> otherEntry = (Entry<Object,Object,Object>) other;

               return
                     ( Objects.isNull( this.entryPrimaryKey )
                          ? Objects.isNull( otherEntry.getPrimaryKey() )
                          : this.entryPrimaryKey.equals( otherEntry.getPrimaryKey() ) )
                  && ( Objects.isNull( this.entrySecondaryKey )
                          ? Objects.isNull( otherEntry.getSecondaryKey() )
                          : this.entrySecondaryKey.equals( otherEntry.getSecondaryKey() ) )
                  && ( Objects.isNull( this.entryValue )
                          ? Objects.isNull( otherEntry.getValue() )
                          : this.entryValue.equals( otherEntry.getValue() ) );
            }

            @Override
            public Kp getPrimaryKey() {
               return this.entryPrimaryKey;
            }

            @Override
            public Ks getSecondaryKey() {
               return this.entrySecondaryKey;
            }

            @Override
            public V getValue() {
               return this.entryValue;
            }

            @Override
            public int hashCode() {
               return
                    ( Objects.isNull( entryPrimaryKey   ) ? 0 : entryPrimaryKey.hashCode()   )
                  ^ ( Objects.isNull( entrySecondaryKey ) ? 0 : entrySecondaryKey.hashCode() )
                  ^ ( Objects.isNull( value             ) ? 0 : value.hashCode()             );
            }
      };
      //@formatter:on
   }

   /**
    * Returns an immutable {@link Entry} containing the given non-{@code null} keys and value. The {@link Entry}s
    * created by this method are suitable for populating {@link DoubleMap} instances.
    *
    * @param <Kp> the primary key type.
    * @param <Ks> the secondary key type.
    * @param <V> the value type.
    * @param primaryKey the non-{@code null} primary key.
    * @param secondaryKey the non-{@code null} secondary key.
    * @param value the value.
    * @return an {@code Entry} containing the specified keys and value.
    * @throw NullPointerException when any of {@code primaryKey}, {@code secondaryKey} or {@code value} are
    * {@code null}.
    */

   static <Kp, Ks, V> Entry<Kp, Ks, V> entryNonNull(Kp primaryKey, Ks secondaryKey, V value) {
      //@formatter:off
      return
         DoubleMap.entry
            (
               Objects.requireNonNull(primaryKey),
               Objects.requireNonNull(secondaryKey),
               Objects.requireNonNull(value)
            );
      //@formatter:on
   }

   /**
    * Creates an immutable {@link DoubleMap} from the provided <code>entries</code>.
    *
    * @param <K> the map key type.
    * @param <V> the type of value saved in the {@link List} collections associated with the map keys.
    * @param entries {@link Map.Entry}s containing the keys and {@link List} collections the map is populated with.
    * @return an immutable {@link MapList} containing the specified mappings.
    * @ImplSpec Implementations may or may not incorporate the provided entries into the {@link DoubleMap}. Meaning
    * changes to the map or entries may or may not be reflected in the map or entries.
    */

   @SafeVarargs
   static <Kp, Ks, V> DoubleMap<Kp, Ks, V> ofEntries(DoubleMap.Entry<Kp, Ks, V>... entries) {

      if (entries == null) {
         throw new NullPointerException("DoubleMap::ofEntries, parameter \"entries\" is null");
      }

      DoubleMap<Kp, Ks, V> newMap = new DoubleHashMap<>(entries.length * 2, 0.75f, entries.length * 2, 0.75f);

      for (DoubleMap.Entry<Kp, Ks, V> entry : entries) {
         newMap.put(entry);
      }

      return newMap;
   }

   /**
    * Removes all entries from the map.
    *
    * @ImplSpec Implementations are expected to clear the contents of all secondary mappings when the implementation
    * allows {@link Map} views of the secondary mappings.
    */

   void clear();

   /**
    * Predicate to determine if the map contains a mapping associated with the primary key.
    *
    * @param primaryKey primary key whose presence in this map is to be tested.
    * @return <code>true</code> when the map contains a mapping associated with the provided primary key; otherwise,
    * <code>false</code>.
    */

   boolean containsKey(Kp primaryKey);

   /**
    * Predicate to determine if the map contains a mapping for the primary and secondary key pair.
    *
    * @param primaryKey the key used to select the secondary map.
    * @param secondaryKey the key used to select the value from the secondary map.
    * @return <code>true</code> when the map contains an association for the provided key pair; otherwise,
    * <code>false</code>.
    */

   boolean containsKey(Kp primaryKey, Ks secondaryKey);

   /**
    * Predicate to determine if the map contains the specified value according to the equality method defined by the
    * type &lt;T&gt;.
    *
    * @param value value whose presence in this map is to be tested.
    * @return {@code true} when the map contains {@code value}; otherwise, {@code false};
    */

   boolean containsValue(V value);

   /**
    * Returns a {@link Set} view of the mappings contained in this map.
    *
    * @return a {@link Set} with a {@link DoubleMap.Entry} object for each mapping contained in the map.
    * @implSpec The returned {@link Set} is expected to be backed by the {@link DoubleMap}. However, the {@link Entry}
    * implementations within the {@link Set} view may or may not be backed by the {@link DoubleMap}. Meaning changes to
    * the {@link DoubleMap} will be reflected in the {@link Set} view and vice-versa. However changes to the
    * {@link Entry} implementations in the {@link Set} view may or may not be reflected in the {@link DoubleMap} and
    * vice-versa.
    */

   Set<DoubleMap.Entry<Kp, Ks, V>> entrySet();

   /**
    * Returns a {@link Set} view of the secondary mappings contained in this map under the specified {@code primaryKey}.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @return a {@link Set} with a {@link DoubleMap.Entry} object for each secondary mapping under the specified
    * {@code primaryKey} contained in the map.
    * @implSpec The returned {@link Set} is expected to be backed by the {@link DoubleMap}. However, the {@link Entry}
    * implementations within the {@link Set} view may or may not be backed by the {@link DoubleMap}. Meaning changes to
    * the {@link DoubleMap} will be reflected in the {@link Set} view and vice-versa. However changes to the
    * {@link Entry} implementations in the {@link Set} view may or may not be reflected in the {@link DoubleMap} and
    * vice-versa.
    */

   Set<Map.Entry<Ks, V>> entrySet(Kp primaryKey);

   /**
    * Performs the given action for each entry in the map until all entries have been processed or the action throws an
    * exception. Iteration order is implementation dependent.
    *
    * @param action the action to be performed for each entry.
    * @throw NullPointerException when {@code action} is {@code null}.
    * @implSpec Whether the provided {@link Entry} implementations are backed by the map are implementation dependent.
    */

   void forEach(Consumer<DoubleMap.Entry<? extends Kp, ? extends Ks, ? extends V>> action);

   /**
    * Performs the given action for each entry in the map until all entries have been processed or the action throws an
    * exception. Iteration order is implementation dependent.
    *
    * @param action the action to be performed for each entry.
    * @throw NullPointerException when {@code action} is {@code null}.
    */

   void forEach(TriConsumer<? super Kp, ? super Ks, ? super V> action);

   /**
    * Returns the secondary mappings for the primary key, or {@code null} if the map does not contain secondary mappings
    * for the primary key. If the double map implementation allows {@code null} associations, the return value of
    * {@code null} does not necessarily indicate that the map does not contain an association for the primary key. The
    * {@link #containsKey} method may be used to distinguish these two cases.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @return when the primary key maps to a set of secondary mappings, a {@link Map} with the secondary mappings;
    * otherwise, {@code null}.
    * @NullPointerException when the {@code primaryKey} is {@code null} and the {@link DoubleMap} implementation does
    * not support {@code null} keys.
    * @implSpec Implementations may return a {@link Map} that is or is not backed by the double map.
    */

   Map<Ks, V> get(Kp primaryKey);

   /**
    * Returns the value which is mapped to the primary and secondary keys, or {@code null} if the map does not contain a
    * mapping for the keys. If the double map implementation allows {@code null} associations, the return value of
    * {@code null} does not necessarily indicate that the map does not contain an association for the keys. The
    * {@link #containsKey} method may be used to distinguish these two cases.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @param secondaryKey the key used to select the value from the secondary mappings.
    * @return when the key pair maps to a value, the value; otherwise, {@code null}.
    */

   V get(Kp primaryKey, Ks secondaryKey);

   /**
    * Predicate to determine if the double map contains any associations.
    *
    * @return {@code true} when the double map does not contain any associations; otherwise, {@code true}.
    */

   boolean isEmpty();

   /**
    * Returns a {@link Set} of the primary keys.
    *
    * @return a {@link Set} view of the map's primary keys.
    * @implSpec Implementations may or may not return a {@link Set} that is backed by the map.
    */

   Set<Kp> keySet();

   /**
    * Returns a {@link Set} view of the secondary keys associated with the primary key.
    *
    * @param primaryKey the primary key used to select the secondary mappings.
    * @return when the primary key maps to a set of secondary mappings, an {@link Optional} with a {@link Set} view of
    * the secondary mappings associated with the primary key; otherwise, an empty {@link Optional}.
    * @implSpec Implementations may or may not return a {@link Set} that is backed by the map.
    */

   Set<Ks> keySet(Kp primaryKey);

   /**
    * Associates the provided value with the primary and secondary keys from the {@code entry}.
    *
    * @param entry a {@link DoubleMap.Entry} containing the primary and secondary keys along with the value to be
    * associated with the keys.
    * @return when the key pair maps to a value, the previous value; otherwise, {@code null}.
    * @throws NullPointerException when {@code entry} is {@code null}.
    * @implSpec The provided {@link Entry} may or may not be incorporated into the map.
    */

   V put(DoubleMap.Entry<? extends Kp, ? extends Ks, ? extends V> entry);

   /**
    * Associates the provided <code>value</code> with the primary and secondary keys.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @param secondaryKey the key used to associate the value with in the secondary mappings.
    * @param value the value to be associated with the key pair.
    * @return when the key pair maps to a value, the previous value; otherwise, {@code null}.
    */

   V put(Kp primaryKey, Ks secondaryKey, V value);

   /**
    * Associates the secondary mappings in the {@code secondaryMap} with the primary key.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @param secondaryMap a {@link Map} containing the secondary mappings to be associated with the primary key.
    * @return when the primary key has associated secondary mapping, a {@link Map} containing the previous secondary
    * mappings; otherwise, {@code null}.
    * @implNote The provided {@code secondaryMap} may be copied into or incorporated into the map. The returned map may
    * be a copy of the previous secondary mappings or the actual {@link Map} implementation used by the
    * {@link DoubleMap} for the secondary mappings.
    */

   Map<Ks, V> put(Kp primaryKey, Map<Ks, V> secondaryMap);

   /**
    * Copies all of the mappings from the specified map to this map. The effect of this call is equivalent to that of
    * calling {@link #put(primaryKey,secondaryKey,value)} on this map once for each mapping of the primary key and
    * secondary key to value in the specified map. The result of this operation is undefined if the specified map is
    * modified while the operation is in progress.
    *
    * @param doubleMap mappings to be copied into this map.
    * @NullPointerException when:
    * <ul>
    * <li>{@code doubleMap} is {@code null}, or</li>
    * <li>if the double map implementation does not support {@code null} keys or values and the specified map contains
    * {@code null} keys or values.</li>
    * </ul>
    */

   void putAll(DoubleMap<? extends Kp, ? extends Ks, ? extends V> doubleMap);

   /**
    * Copies all of the mappings from the specified {@code secondaryMap} to this map under the specified
    * {@link primaryKey}.
    *
    * @param primaryKey the primary key to add the secondary mappings under.
    * @param secondaryMap the secondary mappings to be copied into this map under the {@code primaryKey}.
    * @NullPointerException when:
    * <ul>
    * <li>{@code secondaryMap} is {@code null}, or</li>
    * <li>when the double map implementation does not support {@code null} keys or values; and the {@code primaryKey} is
    * {@code null}; or the {@code secondaryMap} contains {@code null} keys or values.</li>
    * </ul>
    */

   void putAll(Kp primaryKey, Map<? extends Ks, ? extends V> secondaryMap);

   /**
    * If the specified keys are not already associated with a value or are associated with {@code null}, the value is
    * associated with the keys.
    *
    * @param entry a {@link DoubleMap#Entry} containing the primary and secondary keys along with value to be associated
    * with the keys when an association does not exist or the association is currently with {@code null}.
    * @return {@code null} when a new association is made; otherwise, the value currently associated with the keys.
    * @throws NullPointerException when {@code entry} is {@code null}.
    * @implSpec The provided {@link Entry} may or may not be incorporated into the map.
    */

   V putIfAbsent(DoubleMap.Entry<? extends Kp, ? extends Ks, ? extends V> entry);

   /**
    * If the specified keys are not already associated with a value or are associated with {@code null}, the value is
    * associated with the keys.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @param secondaryKey the key used to select the value from the secondary mappings.
    * @param value the value to be associated with the keys when an association does not exist or the association is
    * currently with {@code null}.
    * @return {@code null} when a new association is made; otherwise, the value currently associated with the keys.
    */

   V putIfAbsent(Kp primaryKey, Ks secondaryKey, V value);

   /**
    * If the specified primary key is not already associated with a secondary mapping or is associated with
    * {@code null}, the provided secondary mappings are are associated with the primary key. Implementations may or may
    * not incorporate the {@code secondaryMap}, so changes to the {@code secondaryMap} may or may not be reflected in
    * the map and vice-versa. When secondary mappings currently exist for the primary key the returned secondary
    * mappings returned by the implementation may or may not be backed by the map.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @param secondaryMap the secondary mappings to be associated with the primary key when an association does not
    * exist or the association is currently with {@code null}.
    * @return {@code null} when a new association is made; otherwise, the secondary mappings currently associated with
    * the primary key.
    */

   Map<Ks, V> putIfAbsent(Kp primaryKey, Map<Ks, V> secondaryMap);

   /**
    * Removes the mapping for the primary key.
    * <p>
    * Returns the secondary mappings to which the map previously associated with the primary key, or {@code null} if
    * there was not an association for the primary key.
    * <p>
    * If the implementation permits {@link null} keys, the a return value of {@code null} does not necessarily indicated
    * that the map contained no secondary mappings for the primary key.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @return the previous secondary mappings associated with the primary key, or {@code null} if there was not an
    * association for the primary key.
    * @implSpec The returned {@link Map} may be the actual {@link Map} that held the prior secondary mappings in the
    * {@link DoubleMap} or it may be a new {@link Map} containing the prior secondary mappings.
    */

   Map<Ks, V> remove(Kp primaryKey);

   /**
    * Removes the mapping for the key set.
    * <p>
    * Returns the value to which the map previously associated with the key set, or {@code null} if the map contained no
    * association for the key set.
    * <p>
    * If the implementation permits {@link null} keys or values, then a return value of {@code null} does not
    * necessarily indicate that the map contained no mapping for the key set.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @param secondaryKey the key used to select the value from the secondary mappings.
    * @return the previous value associated with the key set, or {@code null} if there was no association for the key
    * set.
    */

   V remove(Kp primaryKey, Ks secondaryKey);

   /**
    * Returns the number of mapped values within the map.
    *
    * @return the number of mapped values within the map.
    */

   int size();

   /**
    * Returns the number of mappings associated with the primary key in the map.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @return when the primary key maps to a set of secondary mappings, the number of secondary mappings associated with
    * the primary key; otherwise, 0.
    */

   int size(Kp primaryKey);

   /**
    * Returns a {@link Collection} view of the values contained in the map.
    *
    * @return a collection view of the values contained in the map.
    * @implSpec The returned {@link Collection} is expected to be backed by the {@link DoubleMap}. Changes to the
    * {@link DoubleMap} will be reflected in the {@link Collection} view and vice-versa.
    */

   Collection<V> values();

   /**
    * Returns a {@link Collection} view of the value contained in the secondary mappings associated with the primary
    * key. The implementation may return a {@link Collection} that is or is not backed by the map.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @return when the primary key maps to a set of secondary mappings, a {@link Collection} view of the values
    * contained in the secondary mappings associated with the primary key; otherwise {@code null}.
    */

   Collection<V> values(Kp primaryKey);
}

/* EOF */
