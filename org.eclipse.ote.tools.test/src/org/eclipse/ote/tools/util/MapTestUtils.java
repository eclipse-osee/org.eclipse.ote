/**********************************nt***********************************
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class MapTestUtils {

   static Map<String, Object> maps;

   private static String[] testKeySet = {"A", "B", "C"};

   static String id(String mapLetter) {
      return "Map" + mapLetter + ":";
   }

   static String id(String mapLetter, String primaryKey) {
      return "Map" + mapLetter + "(" + primaryKey + "):";
   }

   static String id(String mapLetter, String primaryKey, String secondaryKey) {
      return "Map" + mapLetter + "(" + primaryKey + "," + secondaryKey + "):";
   }

   static void forEachTestKey(Consumer<String> keyTest) {
      Arrays.stream(MapTestUtils.testKeySet).forEach(keyTest::accept);
   }

   static void forEachTestKey(BiConsumer<String, String> keyTest) {
      //@formatter:off
      Arrays
         .stream( MapTestUtils.testKeySet )
         .forEach
            (
               ( primaryKey ) -> Arrays
                                    .stream( MapTestUtils.testKeySet )
                                    .forEach
                                       (
                                          ( secondaryKey ) -> keyTest.accept( primaryKey, secondaryKey )
                                       )
            );
   }

   public interface CountBiConsumer {
      void accept(String key,Integer count);
   }

   static void forEachTestKey(CountBiConsumer keyTest) {
      //@formatter:off
      Arrays
         .stream( MapTestUtils.testKeySet )
         .forEach
            (
               ( primaryKey ) ->
               {
                  for(int i = 1; i <= 3; i++) {
                     keyTest.accept( primaryKey, i );
                  }
               }
            );
   }

   public interface CountTriConsumer {
      void accept(String primaryKey, String secondaryKey, Integer count);
   }

   static void forEachTestKey(CountTriConsumer keyTest) {
      //@formatter:off
      Arrays
         .stream( MapTestUtils.testKeySet )
         .forEach
            (
               ( primaryKey ) ->
               {
                  Arrays
                     .stream( MapTestUtils.testKeySet )
                     .forEach
                        (
                           ( secondaryKey ) ->
                           {
                              for(int i = 1; i <= 3; i++) {
                                 keyTest.accept( primaryKey, secondaryKey, i );
                              }
                           }
                        );
               }
            );
   }


   @SuppressWarnings("unchecked")
   static <M> void forEachMutableTestMap(BiConsumer<String, M> mapTest) {
      for (Map.Entry<String, M> mapEntry : ((Map<String, M>) MapTestUtils.maps).entrySet()) {
         String mapLetter = mapEntry.getKey();
         if ("A".equals(mapLetter)) {
            //Map A is immutable
            continue;
         }
         M map = mapEntry.getValue();
         mapTest.accept(mapLetter, map);
      }
   }

   @SuppressWarnings("unchecked")
   static <M> void forEachTestMap(BiConsumer<String, M> mapTest) {
      for (Map.Entry<String, M> mapEntry : ((Map<String, M>) MapTestUtils.maps).entrySet()) {
         String mapLetter = mapEntry.getKey();
         M map = mapEntry.getValue();
         mapTest.accept(mapLetter, map);
      }
   }

   @SafeVarargs
   static <T> Set<T> setOf(T... values) {
      HashSet<T> mutableHashSet = new HashSet<>(values.length * 2);
      for (T value : values) {
         mutableHashSet.add(value);
      }
      return mutableHashSet;
   }

}
