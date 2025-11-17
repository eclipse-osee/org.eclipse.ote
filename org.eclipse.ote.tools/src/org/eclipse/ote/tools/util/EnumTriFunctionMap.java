/*
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

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import org.eclipse.ote.tools.util.function.TriFunction;

/**
 * Implementation of the {@link EnumFunctionalInterfaceMap} interface for {@link TriFunction} functional interfaces.
 *
 * @author Loren K. Ashley
 * @param <K> the enumeration type whose members may be used as keys in this map.
 * @param <T> the type of the first parameter for the {@link TriFunction} functional interface.
 * @param <U> the type of the second parameter for the {@link TriFunction} functional interface.
 * @param <V> the type of the third parameter for the {@link TriFunction} functional interface.
 * @param <R> the type of results supplied by the {@link BiFunction} functional interface.
 */

public class EnumTriFunctionMap<K extends Enum<K>, T, U, V, R> extends AbstractEnumFunctionalInterfaceMap<K, TriFunction<T, U, V, R>> {

   /**
    * Creates an empty map with the specified key type.
    *
    * @param enumerationKeyClass the class object of the key type for this map.
    */

   public EnumTriFunctionMap(Class<K> enumerationKeyClass) {
      super(enumerationKeyClass);
   }

   /**
    * Looks up and performs the {@link TriFunction} associated with the provided key.
    *
    * @param key the key whose associated {@link TriFunction} is to be performed.
    * @param t the first function argument
    * @param u the second function argument
    * @param v the third function argument
    * @return the result provided by the {@link TriFunction} functional interface implementation.
    * @throws NullPointerException when the provided key is <code>null</code>.
    * @throws NoSuchElementException when there is no map association for the provided key.
    */

   public R apply(K key, T t, U u, V v) {
      TriFunction<T, U, V, R> triFunction = this.enumMap.get(Objects.requireNonNull(key));
      if (triFunction == null) {
         throw new NoSuchElementException();
      }
      return triFunction.apply(t, u, v);
   }

   /**
    * Creates an immutable {@link EnumTriFunctionMap} with the specified entries.
    *
    * @param <K> the enumeration type whose members may be used as keys in this map.
    * @param <T> the type of the first argument to the {@link TriFunction} functional interface.
    * @param <U> the type of the second argument to the {@link TriFunction} functional interface.
    * @param <V> the type of the third argument to the {@link TriFunction} functional interface.
    * @param <R> the type of the result from the {@link TriFunction} functional interface.
    * @param enumerationKeyClass the {@link Class} of the enumeration whose members may be used as map keys.
    * @param entries the entries to be contained in the map.
    * @return the created {@link EnumTriFunctionMap}.
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
   public static <K extends Enum<K>, T, U, V, R> EnumTriFunctionMap<K, T, U, V, R> ofEntries(Class<K> enumerationKeyClass, Map.Entry<K, TriFunction<T, U, V, R>>... entries) {

      return (EnumTriFunctionMap<K, T, U, V, R>) new EnumTriFunctionMap<K, T, U, V, R>(enumerationKeyClass) {
         @Override
         public void put(K key, TriFunction<T, U, V, R> triFunction) {
            throw new UnsupportedOperationException();
         }
      }.ofEntriesLoader(entries);
   }
}

/* EOF */
