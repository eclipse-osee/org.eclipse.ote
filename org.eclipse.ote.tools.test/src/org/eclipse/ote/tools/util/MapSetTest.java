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

import static org.eclipse.ote.tools.util.MapTestUtils.forEachMutableTestMap;
import static org.eclipse.ote.tools.util.MapTestUtils.forEachTestKey;
import static org.eclipse.ote.tools.util.MapTestUtils.forEachTestMap;
import static org.eclipse.ote.tools.util.MapTestUtils.id;
import static org.eclipse.ote.tools.util.MapTestUtils.setOf;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for implementations of the {@link MapSet} interface.
 *
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MapSetTest {

   /**
    * Performs JUNIT assertion checks on the map contents. Some of the checks here are redundant with other tests.
    *
    * @param mapLetter the testing letter of the map being checked
    * @param mapSet the map to be tested
    */

   private static void assertMapOk(String mapLetter, MapSet<String, String> mapSet) {
      Set<String> set;
      set = mapSet.get("A");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("A-1"));
      Assert.assertTrue(id(mapLetter), set.contains("A-2"));
      Assert.assertTrue(id(mapLetter), set.contains("A-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
      set = mapSet.get("B");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("B-1"));
      Assert.assertTrue(id(mapLetter), set.contains("B-2"));
      Assert.assertTrue(id(mapLetter), set.contains("B-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
      set = mapSet.get("C");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("C-1"));
      Assert.assertTrue(id(mapLetter), set.contains("C-2"));
      Assert.assertTrue(id(mapLetter), set.contains("C-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
      Assert.assertEquals(id(mapLetter), 3, mapSet.size());
   }

   @Parameters
   public static Collection<Object[]> data() {
      //@formatter:off
      List<Object[]> parameterList = new LinkedList<>();
      parameterList.add( new Supplier[] { () -> new HashMapHashSet<String,String>() } );
      parameterList.add( new Supplier[] { () -> new HashMapHashSet<String,String>( 4, 4 ) } );
      parameterList.add( new Supplier[] { () -> new HashMapHashSet<String,String>( 4, 0.5f, 4, 0.5f ) } );
      return parameterList;
      //@formatter:on
   }

   private final Supplier<MapSet<String, String>> mapSetSupplier;
   private MapSet<String, String> mapSetA;
   private MapSet<String, String> mapSetB;
   private MapSet<String, String> mapSetC;
   private MapSet<String, String> mapSetD;
   private MapSet<String, String> mapSetE;
   private MapSet<String, String> mapSetF;
   private Map<String, MapSet<String, String>> maps;

   public MapSetTest(Supplier<MapSet<String, String>> mapSetSupplier) {
      this.mapSetSupplier = mapSetSupplier;
   }

   @SuppressWarnings("unchecked")
   @Before
   public void testSetup() {

      /*
       * Map setups to test constructors
       */

      //@formatter:off
      /*
       * MapSetSupplier provides maps from the constructors:
       *
       *    * HashMapHashSet()
       *    * HashMapHashSet(int mapInitialCapacity, int collectionInitialCapacity)
       *    * HashMapHashSet(int mapInitialCapacity, float mapLoadFactor, int collectionInitialCapacity, float collectionLoadFactor)
       */
      //@formatter:on

      /*
       * MapSet::ofEntries
       */

      //@formatter:off
      this.mapSetA =
         MapSet.ofEntries
            (
               new AbstractMap.SimpleEntry<>( "A", setOf( "A-1", "A-2", "A-3" ) ),
               new AbstractMap.SimpleEntry<>( "B", setOf( "B-1", "B-2", "B-3" ) ),
               new AbstractMap.SimpleEntry<>( "C", setOf( "C-1", "C-2", "C-3" ) )
            );
      //@formatter:off

      /*
       * HashMapHashSet(MapCollection<K, V, Set<V>> mapCollection)
       */

      this.mapSetB = new HashMapHashSet<>( this.mapSetA );

      /*
       * Map setups to test put methods
       */

      /*
       * C MapCollection::put(Map.Entry<? extends K, ? extends C> entry)
       */

      this.mapSetC = this.mapSetSupplier.get();
      //@formatter:off
      forEachTestKey
         (
            ( key ) ->
            {
               Set<String> set = new HashSet<>();
               for( int i = 1; i <= 3; i++ ) {
                  set.add( key + "-" + i );
               }
               this.mapSetC.put( new AbstractMap.SimpleEntry<>( key, set ) );
            }
         );
      //@formatter:on

      /*
       * C MapCollection::putAll(K key, C values)
       */

      this.mapSetD = this.mapSetSupplier.get();
      this.mapSetD.putAll("A", setOf("A-1", "A-2", "A-3"));
      this.mapSetD.putAll("B", setOf("B-1"));
      this.mapSetD.putAll("B", setOf("B-2", "B-3"));
      this.mapSetD.putAll("C", setOf("C-1", "C-2"));
      this.mapSetD.putAll("C", setOf("C-3"));

      /*
       * C MapCollection::putEntry(Map.Entry<K, V> entry)
       */

      this.mapSetE = this.mapSetSupplier.get();
      //@formatter:off
      forEachTestKey
         (
            ( key ) ->
            {
               for( int i = 1; i <= 3; i++ ) {
                  this.mapSetE.putEntry( new AbstractMap.SimpleEntry<>( key, key + "-" + i ) );
               }
            }
         );
      //@formatter:on

      /*
       * C MapCollection::putValue(K key, V value)
       */

      this.mapSetF = this.mapSetSupplier.get();
      //@formatter:off
      forEachTestKey
         (
            ( key ) ->
            {
               for( int i = 1; i <= 3; i++ ) {
                  this.mapSetF.putValue( key, key + "-" + i );
               }
            }
         );
      //@formatter:on

      /*
       * Create ordered map of test MapSets
       */

      this.maps = new LinkedHashMap<String, MapSet<String, String>>();
      this.maps.put("A", this.mapSetA);
      this.maps.put("B", this.mapSetB);
      this.maps.put("C", this.mapSetC);
      this.maps.put("D", this.mapSetD);
      this.maps.put("E", this.mapSetE);
      this.maps.put("F", this.mapSetF);

      MapTestUtils.maps = (Map<String, Object>) (Object) this.maps;
   }

   @Test
   public void testA_verifyMaps() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String, String> mapSet ) ->
            {
               MapSetTest.assertMapOk(mapLetter, mapSet);
            }
         );
      //@formatter:on
   }

   @Test
   public void testB_containsValueInAnyCollection() {
      //@formatter:off
      forEachTestMap
         (
            (String mapLetter, MapSet<String, String> mapSet) ->
            {
               forEachTestKey
                  (
                     ( String key, Integer i ) ->
                        Assert.assertTrue( id( mapLetter, key ), mapSet.containsValueInAnyCollection( key + "-" + i ) )
                  );
            }
         );
   }

   @Test
   public void testC_forEach() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               MapSet<String,String> collectedMapSet = new HashMapHashSet<>();
               mapSet.forEach( ( Map.Entry<? extends String,? extends Set<String>> mapEntry ) -> collectedMapSet.put( mapEntry ) );
               MapSetTest.assertMapOk(mapLetter, collectedMapSet);
            }
         );
      //@formatter:on
   }

   @Test
   public void testD_forEachEntry_consumer() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               MapSet<String,String> collectedMapSet = new HashMapHashSet<>();
               mapSet.forEachEntry( ( Map.Entry<String,String> entry ) -> collectedMapSet.putEntry( entry ) );
               MapSetTest.assertMapOk( mapLetter, collectedMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testE_forEachEntry_key_consumer() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               MapSet<String, String> collectedMapSet = new HashMapHashSet<>();
               forEachTestKey
                  (
                     ( String key ) ->
                        mapSet.forEachEntry( key, ( Map.Entry<String,String> entry ) -> collectedMapSet.putEntry( entry ) )
                  );
               MapSetTest.assertMapOk( mapLetter, collectedMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testF_forEachValue_biconsumer() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               MapSet<String, String> collectedMapSet = new HashMapHashSet<>();
               mapSet.forEachValue( ( String k, String v) -> collectedMapSet.putValue( k, v ) );
               MapSetTest.assertMapOk( mapLetter, collectedMapSet );
            }
         );
      //@formatter:off
   }

   @Test
   public void testG_forEachValue_key_consumer() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               MapSet<String, String> collectedMapSet = new HashMapHashSet<>();
               forEachTestKey
                  (
                     ( String key ) -> mapSet.forEachValue( key, ( String value ) -> collectedMapSet.putValue( key, value ) )
                  );
               MapSetTest.assertMapOk( mapLetter, collectedMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testH_getOptional() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               MapSet<String, String> collectedMapSet = new HashMapHashSet<>();
               forEachTestKey
                  (
                     ( String key ) ->
                     {
                        Optional<Set<String>> optionalSet = mapSet.getOptional(key);
                        Assert.assertTrue( id( mapLetter, key ), optionalSet.isPresent() );
                        Set<String> optionalValueSet = optionalSet.get();
                        collectedMapSet.put( key, optionalValueSet );
                     }
                  );
               MapSetTest.assertMapOk( mapLetter, mapSet );
            }
         );
      //@formatter:off
   }

   @Test
   public void testI_removeEntry() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               boolean result;
               result = mapSet.removeEntry( new AbstractMap.SimpleEntry<>( "A", "A-4" ) );
               Assert.assertFalse( id( mapLetter ), result );
               result = mapSet.removeEntry( new AbstractMap.SimpleEntry<>( "D", "A-1" ) );
               Assert.assertFalse( id( mapLetter ), result );
               forEachTestKey
                  (
                     ( String key, Integer i ) ->
                     {
                        boolean result2 = mapSet.removeEntry(new AbstractMap.SimpleEntry<>( key, key + "-" + i ) );
                        Assert.assertTrue  ( id( mapLetter, key ), result2 );
                     }
                  );
               Assert.assertEquals( id( mapLetter ), 0, mapSet.sizeValues() );
            }
         );
      //@formatter:on
   }

   @Test
   public void testJ_removeValue() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               boolean result;
               result = mapSet.removeValue( "A", "A-4" );
               Assert.assertFalse( id( mapLetter ), result );
               result = mapSet.removeValue( "D", "A-1" );
               Assert.assertFalse( id( mapLetter ), result );
               forEachTestKey
                  (
                     ( String key, Integer i ) ->
                     {
                        boolean result2 = mapSet.removeValue( key, key + "-" + i );
                        Assert.assertTrue  ( id( mapLetter, key ), result2 );
                     }
                  );
               Assert.assertEquals( id( mapLetter ), 0, mapSet.sizeValues() );
            }
         );
      //@formatter:on
   }

   @Test
   public void testK_sizeValues() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               Assert.assertEquals( id( mapLetter ), 9, mapSet.sizeValues() );
            }
         );
      //@formatter:on
   }

   @Test
   public void testL_sizeValues_key() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               forEachTestKey
                  (
                     ( String key ) ->
                     {
                        Assert.assertEquals( id( mapLetter, key ), 3, mapSet.sizeValues( key ) );
                     }
                  );
            }
         );
      //@formatter:off
   }


   @Test
   public void testM_stream() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               MapSet<String,String> collectedMapSet = new HashMapHashSet<>();
               mapSet
                  .stream()
                  .map( ( value ) -> new AbstractMap.SimpleEntry<>( value.substring(0,1), value ) )
                  .forEach( collectedMapSet::putEntry );
               MapSetTest.assertMapOk( mapLetter, collectedMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testN_stream_key() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               MapSet<String,String> collectedMapSet = new HashMapHashSet<>();
               forEachTestKey
                  (
                     ( String key ) ->
                        mapSet
                           .stream( key )
                           .map( ( value ) -> new AbstractMap.SimpleEntry<>( value.substring(0,1), value ) )
                           .forEach( collectedMapSet::putEntry )
               );
               MapSetTest.assertMapOk( mapLetter, collectedMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testO_streamAllCollectionValuesAsEntries() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               MapSet<String,String> collectedMapSet = new HashMapHashSet<>();
               mapSet
                  .streamAllCollectionValuesAsEntries()
                  .forEach( collectedMapSet::putEntry );
               MapSetTest.assertMapOk( mapLetter, collectedMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testP_streamCollections() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               MapSet<String,String> collectedMapSet = new HashMapHashSet<>();
               mapSet
                  .streamCollections()
                  .flatMap( Collection::stream )
                  .map( ( value ) -> new AbstractMap.SimpleEntry<>( value.substring(0,1), value ) )
                  .forEach( collectedMapSet::putEntry );
               MapSetTest.assertMapOk( mapLetter, collectedMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testQ_streamEntries() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               MapSet<String,String> collectedMapSet = new HashMapHashSet<>();
               mapSet
                  .streamEntries()
                  .forEach
                     (
                        ( entry ) ->
                        {
                           String key = entry.getKey();
                           Set<String> set = entry.getValue();
                           set.stream().forEach( ( value ) -> collectedMapSet.putValue( key, value ) );
                        }
                     );
               MapSetTest.assertMapOk( mapLetter, collectedMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testO_streamPrimaryKeys() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, MapSet<String,String> mapSet ) ->
            {
               Set<String> primaryKeys = mapSet.keySet();
               Set<String> collectedPrimaryKeys =
                  mapSet
                     .streamPrimaryKeys()
                     .collect( Collectors.toSet() );
               Assert.assertEquals( id( mapLetter ), primaryKeys, collectedPrimaryKeys );
            }
         );
      //@formatter:on
   }
}
