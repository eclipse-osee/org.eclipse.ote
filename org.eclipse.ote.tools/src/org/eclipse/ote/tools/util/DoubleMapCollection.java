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
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.eclipse.ote.tools.util.function.TriConsumer;

/**
 * Interface for an object that maps keys to collections of values. The interface extends the interface for
 * {@link Map}<code>&lt;K,C&gt;</code> with additional methods for directly accessing or adding values to the
 * collections contained within the map.
 *
 * @author Loren K. Ashley
 * @param <Kp> the primary map key type.
 * @param <Ks> the secondary map key type.
 * @param <V> the type of value saved in the collections associated with the map keys.
 * @param <C> the type of collection associated with the map keys.
 */

public interface DoubleMapCollection<Kp, Ks, V, C extends Collection<V>> extends DoubleMap<Kp, Ks, C> {

   /**
    * Determines if the <code>value</code> is contained in any of the {@link Collection}s in the
    * {@link DoubleMapCollection}.
    *
    * @param value the value to look for.
    * @return <code>true</code>, when the {@link DoubleMapCollection} contains <code>value</code>; otherwise,
    * <code>false</code>.
    */

   boolean containsValueInAnyCollection(V value);

   /**
    * Determines if the <code>value</code> is contained in any of the {@link Collection}s within the secondary mappings
    * associated with the {@code primaryKey} of the {@link DoubleMapCollection}.
    *
    * @param value the value to look for.
    * @return <code>true</code>, when the {@link DoubleMapCollection} contains <code>value</code>; otherwise,
    * <code>false</code>.
    */

   boolean containsValueInCollection(Kp primaryKey, V value);

   /**
    * A {@link DoubleMap.Entry} with the primary key, secondary key, and a value from the {@link Collection} associated
    * with the key pair are acted upon with the provided {@link action} for each key pair and all values from the
    * {@link Collection} associated with each key pair.
    *
    * @param action the action to be performed on each entry.
    */

   void forEachEntry(Consumer<DoubleMap.Entry<Kp, Ks, V>> action);

   /**
    * A {@link DoubleMap.Entry} with the primary key, secondary key, and a value from the {@link Collection} associated
    * with the key pair are acted upon with the provided {@link action} for each key pair with the provided
    * {@code primaryKey}, the secondary keys associated with the {@code primaryKey}, and all values from the
    * {@link Collection} associated with each key pair.
    *
    * @param primaryKey the primaryKey whose secondary mappings and values from each {@link Collection} that will be
    * processed.
    * @param action the action to be performed on each entry.
    */

   void forEachEntry(Kp primaryKey, Consumer<DoubleMap.Entry<Kp, Ks, V>> action);

   /**
    * A {@link DoubleMap.Entry} with the primary key, secondary key, and a value from the {@link Collection} associated
    * with the key pair are acted upon with the provided {@link action} for each values from the {@link Collection}
    * associated with the provided {@code primaryKey} and {@code secondaryKey}.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @param secondaryKey the key used to select the {@link Collection} whose values will be processed.
    * @param action the action to be performed on each entry.
    */

   void forEachEntry(Kp primaryKey, Ks secondaryKey, Consumer<DoubleMap.Entry<Kp, Ks, V>> action);

   /**
    * All the values from the {@link Collection} associated with each secondary mapping under the provided
    * {@code primaryKey} are acted upon with the provided {@link action}. The {@link BiConsumer} is passed each value
    * along with the secondary key associated with the {@link Collection} that the value is from.
    *
    * @param primaryKey the primaryKey whose secondary mappings and values from each {@link Collection} that will be
    * processed.
    * @param action the action to be performed on each secondary key and value pair.
    */

   void forEachValue(Kp primaryKey, BiConsumer<Ks, V> action);

   /**
    * All the values from the {@link Collection} associated with the provided {@code primaryKey} and
    * {@code secondaryKey} are acted upon with the provided {@link action}. The {@link Consumer} is passed each value
    * the {@link Collection} associated with the primary and secondary key pair.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @param secondaryKey the key used to select the {@link Collection} from the secondary mappings.
    * @param action the action to be performed on each value.
    */

   void forEachValue(Kp primaryKey, Ks secondaryKey, Consumer<V> action);

   /**
    * All the values from the {@link Collection} associated with each primary and secondary key pair are acted upon with
    * the provided {@link action}. The {@link TriConsumer} is passed each value along with the primary and secondary
    * keys associated with the {@link Collection} that the value is from.
    *
    * @param action the action to be performed on each primary, secondary, and value triplet.
    */

   void forEachValue(TriConsumer<Kp, Ks, V> action);

   /**
    * Gets the an {@link Optional} containing the {@link Collection} associated with the {@code primaryKey} and
    * {@code secondaryKey} pair. If the map implementation allows {@code null} values and the value associated with the
    * key pair is {@code null} and empty {@link Optional} is returned. The method {@link #containsKey} can be used to
    * distinguish between the {@code null} value case and the no-mapping case.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @param secondaryKey the key used to select the {@link Collection} from the secondary mappings.
    * @return when a {@link Collection} is associated with the key pair, an {@link Optional} containing the associated
    * {@link Collection}; otherwise, an empty {@link Optional}.
    */

   Optional<C> getOptional(Kp primaryKey, Ks secondaryKey);

   /**
    * Adds the values in the {@link Collection} <code>values</code> to a collection in this object associated with the
    * specified <code>key</code>.
    *
    * @implSpec Implements are expected to create a new {@link Collection} when an existing collection is not associated
    * with the specified <code>key</code> and associated it with the <code>key</code>. This decouples the collection in
    * this object with the <code>values</code> collection.
    * @param key the key whose associated {@link Collection} will be used to store all the values from the provided
    * <code>values</code> {@link Collection}.
    * @param values the collection to copy values from.
    * @return the collection containing the added values.
    */

   C putAll(Kp primaryKey, Ks secondaryKey, C values);

   /**
    * Adds the value in the {@link Map#Entry} to the {@link Collection} associated with the key in the
    * {@link Map#Entry}. When the key does not have an associated {@link Collection} an new {@link Collection} is
    * created and associated with the key. The value is added using the method {@link Collection#add(Object)}.
    *
    * @param entry the {@link Map.Entry} containing the key and value to be added.
    * @return the collection containing the added value.
    */

   C putEntry(DoubleMap.Entry<Kp, Ks, V> entry);

   /**
    * Adds the <code>value</code> to the {@link Collection} associated with the <code>key</code>. When the
    * <code>key</code> does not have an associated {@link Collection} an new {@link Collection} is created and
    * associated with the <code>key</code>. The <code>value</code> is added using the method
    * {@link Collection#add(Object)}.
    *
    * @param entry the {@link Map.Entry} containing the key and value to be added.
    * @return the collection containing the added value.
    */

   C putValue(Kp primaryKey, Ks secondaryKey, V value);

   /**
    * Removes the value in the {@link Map#Entry} from the {@link Collection} associated with the key in the
    * {@link Map#Entry}. If the value is also contained in any {@link Collection}s associated with other keys, the value
    * will not be removed from those {@link Collection}s.
    *
    * @param entry a {@link Map#Entry} contain the key of the {@link Collection} to remove the value contained in the
    * {@Link Map#Entry} from.
    * @return <code>true</code>, when the {@link MapCollection} was modified; otherwise, <code>false</code>.
    */

   boolean removeEntry(DoubleMap.Entry<Kp, Ks, V> entry);

   /**
    * Removes the <code>value</code> from the {@link Collection} associated with the <code>key</code>. If the
    * <code>value</code> is also contained in any {@link Collection}s associated with other keys, the <code>value</code>
    * will not be removed from those {@link Collection}s.
    *
    * @param key the key associated with the collection to remove the value from.
    * @param value the value to be removed.
    * @return <code>true</code>, when the {@link MapCollection} was modified; otherwise, <code>false</code>.
    */

   boolean removeValue(Kp primaryKey, Ks secondaryKey, V value);

   /**
    * Returns the number of values in all of the {@link Collection}s with in the {@link MapCollection}. If a value is
    * contained in more than one {@link Collection}, it will be counted once for each {@link Collection} containing the
    * value.
    *
    * @return the sum of the sizes of all the {@link Collection}s in the {@link MapCollection}.
    */

   int sizeValues();

   /**
    * Returns the number of values in all of the {@link Collection}s within the secondary mappings specified with the
    * specified {@code primaryKey} in the {@link MapCollection}. If a value is contained in more than one
    * {@link Collection}, it will be counted once for each {@link Collection} containing the value.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @return the sum of the sizes of all the {@link Collection}s in the {@link MapCollection}.
    */

   int sizeValues(Kp primaryKey);

   /**
    * Returns the number of values in the collection associated with the <code>key</code>.
    *
    * @param key the key whose associated {@link Collection} size is to be obtained.
    * @return when <code>key</code> is associated with a {@link Collection}, the number of values in the
    * {@link Collection}; otherwise, zero.
    */

   int sizeValues(Kp primaryKey, Ks secondaryKey);

   /**
    * Provides an unordered {@link Stream} of all the values in all of the {@link Collection}s held within the map.
    *
    * @return a {@link Stream} of the map's values.
    */

   Stream<V> stream();

   /**
    * Provides an unordered {@link Stream} of all the values in the {@link Collection}s from the secondary mappings
    * associated with the provided {@code primaryKey}.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @return when at least one {@link Collection} is contained with in the secondary mappings associated with the
    * {@code primaryKey}, a {@link Stream} of the values contained in all of the {@link Collection}s; otherwise an empty
    * {@link Stream}.
    */

   Stream<V> stream(Kp primaryKey);

   /**
    * Provides an unordered {@link Stream} of all the values in the {@link Collection} associated with the
    * {@code primaryKey} and {@code secondaryKey} pair.
    *
    * @param primaryKey the key used to select the secondary mappings.
    * @param secondaryKey the key used to select the {@link Collection} from the secondary mappings.
    * @return when a {@link Collection} is associated with the key pair, a {@link Stream} of the values contained in the
    * associated {@link Collection}; otherwise an empty {@link Stream}.
    */

   Stream<V> stream(Kp primaryKey, Ks secondaryKey);

   /**
    * Provides an unordered {@link Stream} of all the values within all the {@link Collection}s in the map. The values
    * are wrapped in an {@link Map.Entry} with the key the {@link Collection} the value came from is associated with.
    *
    * @return a {@link Stream} of {@link Map.Entry}<code>&lt;K,V&gt;</code> objects.
    */

   Stream<DoubleMap.Entry<Kp, Ks, V>> streamAllCollectionValuesAsEntries();

   /**
    * Provides an unordered {@link Stream} of the {@link Collection}s in the map.
    *
    * @return an unordered {@link Stream} of the {@link Collection}s in the map.
    */

   Stream<C> streamCollections();

   /**
    * Provides an unordered {@link Stream} of the {@link MapCollection} entries as {@link Map.Entry} objects.
    *
    * @return an unordered {@link Stream} of the {@link MapCollection} entries as {@link Map.Entry} objects.
    */

   Stream<DoubleMap.Entry<Kp, Ks, C>> streamEntries();

   /**
    * Provides an unordered {@link Stream} of the primary keys.
    *
    * @return an unordered {@link Stream} of the primary keys.
    */

   Stream<Kp> streamPrimaryKeys();

   /**
    * Provides an unordered {@link Stream} of the secondary keys in the secondary mappings selected by the provided
    * {@code primaryKey}.
    *
    * @return an unordered {@link Stream} of the primary keys.
    */

   Stream<Ks> streamSecondaryKeys(Kp primaryKey);

}
