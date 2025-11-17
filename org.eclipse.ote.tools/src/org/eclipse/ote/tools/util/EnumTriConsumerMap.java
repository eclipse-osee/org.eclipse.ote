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

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.eclipse.ote.tools.util.function.TriConsumer;

/**
 * Implementation of the {@link EnumFunctionalInterfaceMap} interface for {@link TriConsumer} functional interfaces.
 *
 * @author Loren K. Ashley
 * @param <K> the enumeration type whose members may be used as keys in this map.
 * @param <T> the type of the first argument to the {@link TriConsumer} functional interface.
 * @param <U> the type of the second argument to the {@link TriConsumer} functional interface.
 * @param <V> the type of the third argument to the {@link TriConsumer} functional interface.
 */

public class EnumTriConsumerMap<K extends Enum<K>, T, U, V> extends AbstractEnumFunctionalInterfaceMap<K, TriConsumer<T, U, V>> {

   /**
    * Creates an empty map with the specified key type.
    *
    * @param enumerationKeyClass the class object of the key type for this map.
    */

   public EnumTriConsumerMap(Class<K> enumerationKeyClass) {
      super(enumerationKeyClass);
   }

   /**
    * Looks up and performs the {@link TriConsumer} associated with the provided key.
    *
    * @param key the key whose associated {@link TriConsumer} is to be performed.
    * @param t the first input argument to the {@link TriConsumer}.
    * @param u the second input argument to the {@link TriConsumer}.
    * @param u the third input argument to the {@link TriConsumer}.
    * @throws NullPointerException when the provided key is <code>null</code>.
    * @throws NoSuchElementException when there is no map association for the provided key.
    */

   public void accept(K key, T t, U u, V v) {
      TriConsumer<T, U, V> triConsumer = this.enumMap.get(Objects.requireNonNull(key));
      if (triConsumer == null) {
         throw new NoSuchElementException();
      }
      triConsumer.accept(t, u, v);
   }

   /**
    * Creates an immutable {@link EnumTriConsumerMap} with the specified entries.
    *
    * @apiNote Map entries may be created using the {@link Map#entry Map.entry()} method.
    * @param <K> the enumeration type whose members may be used as keys in this map.
    * @param <T> the type of the first argument to the {@link TriConsumer} functional interface.
    * @param <U> the type of the second argument to the {@link TriConsumer} functional interface.
    * @param <V> the type of the third argument to the {@link TriConsumer} functional interface.
    * @param enumerationKeyClass the {@link Class} of the enumeration whose members may be used as map keys.
    * @param entries the entries to be contained in the map.
    * @return the created {@link EnumTriConsumerMap}.
    * @throws NullPointerException when:
    * <ul>
    * <li>the <code>entries</code> array reference is <code>null</code>, or</li>
    * <li>an entry in the <code>entries</code> array is <code>null</code>.
    * </ul>
    * @throws EnumMapDuplicateEntryException when an attempt is made to add an entry to the map when a mapping for the
    * provided key already exists.
    */

   @SafeVarargs
   @SuppressWarnings({"unchecked", "varargs"})
   public static <K extends Enum<K>, T, U, V> EnumTriConsumerMap<K, T, U, V> ofEntries(Class<K> enumerationKeyClass, Map.Entry<K, TriConsumer<T, U, V>>... entries) {

      return (EnumTriConsumerMap<K, T, U, V>) new EnumTriConsumerMap<K, T, U, V>(enumerationKeyClass) {
         @Override
         public void put(K key, TriConsumer<T, U, V> function) {
            throw new UnsupportedOperationException();
         }
      }.ofEntriesLoader(entries);
   }

}

/* EOF */
