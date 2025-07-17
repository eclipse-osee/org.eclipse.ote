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
 * Tests for implementations of the {@link DoubleMapSet} interface.
 *
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DoubleMapSetTest {

   /**
    * Performs JUNIT assertion checks on the map contents. Some of the checks here are redundant with other tests.
    *
    * @param mapLetter the testing letter of the map being checked
    * @param mapSet the map to be tested
    */

   private static void assertMapOk(String mapLetter, DoubleMapSet<String, String, String> doubleMapSet) {
      Set<String> set;
      set = doubleMapSet.get("A", "A");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("A-A-1"));
      Assert.assertTrue(id(mapLetter), set.contains("A-A-2"));
      Assert.assertTrue(id(mapLetter), set.contains("A-A-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
      set = doubleMapSet.get("A", "B");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("A-B-1"));
      Assert.assertTrue(id(mapLetter), set.contains("A-B-2"));
      Assert.assertTrue(id(mapLetter), set.contains("A-B-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
      set = doubleMapSet.get("A", "C");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("A-C-1"));
      Assert.assertTrue(id(mapLetter), set.contains("A-C-2"));
      Assert.assertTrue(id(mapLetter), set.contains("A-C-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
      set = doubleMapSet.get("B", "A");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("B-A-1"));
      Assert.assertTrue(id(mapLetter), set.contains("B-A-2"));
      Assert.assertTrue(id(mapLetter), set.contains("B-A-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
      set = doubleMapSet.get("B", "B");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("B-B-1"));
      Assert.assertTrue(id(mapLetter), set.contains("B-B-2"));
      Assert.assertTrue(id(mapLetter), set.contains("B-B-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
      set = doubleMapSet.get("B", "C");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("B-C-1"));
      Assert.assertTrue(id(mapLetter), set.contains("B-C-2"));
      Assert.assertTrue(id(mapLetter), set.contains("B-C-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
      set = doubleMapSet.get("C", "A");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("C-A-1"));
      Assert.assertTrue(id(mapLetter), set.contains("C-A-2"));
      Assert.assertTrue(id(mapLetter), set.contains("C-A-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
      set = doubleMapSet.get("C", "B");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("C-B-1"));
      Assert.assertTrue(id(mapLetter), set.contains("C-B-2"));
      Assert.assertTrue(id(mapLetter), set.contains("C-B-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
      set = doubleMapSet.get("C", "C");
      Assert.assertNotNull(id(mapLetter), set);
      Assert.assertTrue(id(mapLetter), set.contains("C-C-1"));
      Assert.assertTrue(id(mapLetter), set.contains("C-C-2"));
      Assert.assertTrue(id(mapLetter), set.contains("C-C-3"));
      Assert.assertEquals(id(mapLetter), 3, set.size());
   }

   @Parameters
   public static Collection<Object[]> data() {
      //@formatter:off
      List<Object[]> parameterList = new LinkedList<>();
      parameterList.add( new Supplier[] { () -> new DoubleHashMapHashSet<String,String,String>() } );
      parameterList.add( new Supplier[] { () -> new DoubleHashMapHashSet<String,String,String>( 4, 4, 4 ) } );
      parameterList.add( new Supplier[] { () -> new DoubleHashMapHashSet<String,String,String>( 4, 0.5f, 4, 0.5f, 4, 0.5f ) } );
      return parameterList;
      //@formatter:on
   }

   private final Supplier<DoubleMapSet<String, String, String>> doubleMapSetSupplier;
   private DoubleMapSet<String, String, String> doubleMapSetA;
   private DoubleMapSet<String, String, String> doubleMapSetB;
   private DoubleMapSet<String, String, String> doubleMapSetC;
   private DoubleMapSet<String, String, String> doubleMapSetD;
   private DoubleMapSet<String, String, String> doubleMapSetE;
   private DoubleMapSet<String, String, String> doubleMapSetF;
   private Map<String, DoubleMapSet<String, String, String>> maps;

   public DoubleMapSetTest(Supplier<DoubleMapSet<String, String, String>> doubleMapSetSupplier) {
      this.doubleMapSetSupplier = doubleMapSetSupplier;
   }

   @SuppressWarnings("unchecked")
   @Before
   public void testSetup() {

      /*
       * Map setups to test constructors
       */

      //@formatter:off
      /*
       * DoubleMapSetSupplier provides maps from the constructors:
       *
       *   * DoubleHashMapHashSet()
       *   * DoubleHashMapHashSet(int primaryMapInitialCapacity, int secondaryMapInitialCapacity, int collectionInitialCapacity)
       *   * DoubleHashMapHashSet(int   primaryMapInitialCapacity, float primaryMapLoadFactor, int secondaryMapInitialCapacity, float secondaryMapLoadFactor, int collectionInitialCapacity, float collectionLoadFactor)
       *
       */
      //@formatter:on

      /*
       * DoubleMapSet::ofEntries
       */

      //@formatter:off
      this.doubleMapSetA =
         DoubleMapSet.ofEntries
            (
               DoubleMap.entry( "A", "A", setOf( "A-A-1", "A-A-2", "A-A-3" ) ),
               DoubleMap.entry( "A", "B", setOf( "A-B-1", "A-B-2", "A-B-3" ) ),
               DoubleMap.entry( "A", "C", setOf( "A-C-1", "A-C-2", "A-C-3" ) ),
               DoubleMap.entry( "B", "A", setOf( "B-A-1", "B-A-2", "B-A-3" ) ),
               DoubleMap.entry( "B", "B", setOf( "B-B-1", "B-B-2", "B-B-3" ) ),
               DoubleMap.entry( "B", "C", setOf( "B-C-1", "B-C-2", "B-C-3" ) ),
               DoubleMap.entry( "C", "A", setOf( "C-A-1", "C-A-2", "C-A-3" ) ),
               DoubleMap.entry( "C", "B", setOf( "C-B-1", "C-B-2", "C-B-3" ) ),
               DoubleMap.entry( "C", "C", setOf( "C-C-1", "C-C-2", "C-C-3" ) )
            );
      //@formatter:on

      /*
       * DoubleHashMapHashSet(DoubleMapCollection<Kp, Ks, V, Set<V>> doubleMapCollection)
       */

      this.doubleMapSetB = new DoubleHashMapHashSet<>(this.doubleMapSetA);

      /*
       * Map setups to test put methods
       */

      /*
       * V DoubleMap::put(DoubleMap.Entry<? extends Kp, ? extends Ks, ? extends V> entry);
       */

      this.doubleMapSetC = this.doubleMapSetSupplier.get();
      //@formatter:off
      forEachTestKey
         (
            ( primaryKey ) ->
            {
               forEachTestKey
                  (
                     ( secondaryKey ) ->
                     {
                        Set<String> set = new HashSet<>();
                        for( int i = 1; i <= 3; i++ ) {
                           set.add( primaryKey + "-" + secondaryKey + "-" + i );
                        }
                        this.doubleMapSetC.put( DoubleMap.entry( primaryKey, secondaryKey, set ) );
                     }
                  );
            }
         );
      //@formatter:on

      /*
       * C DoubleMapCollection::putAll(Kp primaryKey, Ks secondaryKey, C values)
       */

      this.doubleMapSetD = this.doubleMapSetSupplier.get();
      this.doubleMapSetD.putAll("A", "A", setOf("A-A-1", "A-A-2", "A-A-3"));
      this.doubleMapSetD.putAll("A", "B", setOf("A-B-1"));
      this.doubleMapSetD.putAll("A", "B", setOf("A-B-2", "A-B-3"));
      this.doubleMapSetD.putAll("A", "C", setOf("A-C-1", "A-C-2"));
      this.doubleMapSetD.putAll("A", "C", setOf("A-C-3"));
      this.doubleMapSetD.putAll("B", "A", setOf("B-A-1", "B-A-2", "B-A-3"));
      this.doubleMapSetD.putAll("B", "B", setOf("B-B-1", "B-B-2", "B-B-3"));
      this.doubleMapSetD.putAll("B", "C", setOf("B-C-1", "B-C-2", "B-C-3"));
      this.doubleMapSetD.putAll("C", "A", setOf("C-A-1", "C-A-2", "C-A-3"));
      this.doubleMapSetD.putAll("C", "B", setOf("C-B-1", "C-B-2", "C-B-3"));
      this.doubleMapSetD.putAll("C", "C", setOf("C-C-1", "C-C-2", "C-C-3"));

      /*
       * C DoubleMapCollection::putEntry(DoubleMap.Entry<Kp, Ks, V> entry)
       */

      this.doubleMapSetE = this.doubleMapSetSupplier.get();
      //@formatter:off
      forEachTestKey
         (
            ( primaryKey ) ->
            {
               forEachTestKey
                  (
                     ( secondaryKey ) ->
                     {
                        for( int i = 1; i <= 3; i++ ) {
                           this.doubleMapSetE.putEntry( DoubleMap.entry( primaryKey, secondaryKey, primaryKey + "-" + secondaryKey + "-" + i ) );
                        }
                     }
                  );
            }
         );
      //@formatter:on

      /*
       * C DoubleMapCollection:putValue(Kp primaryKey, Ks secondaryKey, V value)
       */

      this.doubleMapSetF = this.doubleMapSetSupplier.get();
      //@formatter:off
      forEachTestKey
         (
            ( primaryKey ) ->
            {
               forEachTestKey
                  (
                     ( secondaryKey ) ->
                     {
                        for( int i = 1; i <= 3; i++ ) {
                           this.doubleMapSetF.putValue( primaryKey, secondaryKey, primaryKey + "-" + secondaryKey + "-" + i );
                        }
                     }
                  );
            }
         );
      //@formatter:on

      /*
       * Create ordered map of test DoubleMapSets
       */

      this.maps = new LinkedHashMap<String, DoubleMapSet<String, String, String>>();
      this.maps.put("A", this.doubleMapSetA);
      this.maps.put("B", this.doubleMapSetB);
      this.maps.put("C", this.doubleMapSetC);
      this.maps.put("D", this.doubleMapSetD);
      this.maps.put("E", this.doubleMapSetE);
      this.maps.put("F", this.doubleMapSetF);

      MapTestUtils.maps = (Map<String, Object>) (Object) this.maps;
   }

   @Test
   public void testA_verifyMaps() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String, String, String> doubleMapSet ) ->
            {
               DoubleMapSetTest.assertMapOk(mapLetter, doubleMapSet);
            }
         );
      //@formatter:on
   }

   @Test
   public void testB_containsValueInAnyCollection() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String, String, String> doubleMapSet ) ->
            {
               forEachTestKey
                  (
                     ( String primaryKey, String secondaryKey, Integer i ) ->
                        Assert.assertTrue( id( mapLetter, primaryKey, secondaryKey ), doubleMapSet.containsValueInAnyCollection( primaryKey + "-" + secondaryKey + "-" + i ) )
                  );
               Assert.assertFalse( id( mapLetter ), doubleMapSet.containsValueInAnyCollection( "NOT-FOUND" ) );
            }
         );
      //@formatter:on
   }

   @Test
   public void testC_containsValueInAnyCollection_secondary() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String, String, String> doubleMapSet ) ->
            {
               forEachTestKey
                  (
                     ( String primaryKey, String secondaryKey, Integer i ) ->
                        Assert.assertTrue( id( mapLetter, primaryKey, secondaryKey ), doubleMapSet.containsValueInCollection( primaryKey, primaryKey + "-" + secondaryKey + "-" + i ) )
                  );
               forEachTestKey
                  (
                     ( String primaryKey ) ->
                        Assert.assertFalse( id( mapLetter ), doubleMapSet.containsValueInCollection(primaryKey, "NOT-FOUND" ) )
                  );
               Assert.assertFalse( id( mapLetter ), doubleMapSet.containsValueInCollection( "D", "NOT-FOUND" ) );
            }
         );
      //@formatter:on
   }

   @Test
   public void testD_forEach() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String,String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               doubleMapSet.forEach( ( DoubleMap.Entry<? extends String, ? extends String,? extends Set<String>> doubleMapEntry ) -> collectedDoubleMapSet.put( doubleMapEntry ) );
               DoubleMapSetTest.assertMapOk(mapLetter, collectedDoubleMapSet);
            }
         );
      //@formatter:on
   }

   @Test
   public void testE_forEachEntry_consumer() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String,String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               doubleMapSet.forEachEntry( ( DoubleMap.Entry<String,String,String> entry ) -> collectedDoubleMapSet.putEntry( entry ) );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testF_forEachEntry_primaryKey_consumer() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String, String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               forEachTestKey
                  (
                     ( String primaryKey ) ->
                        doubleMapSet.forEachEntry( primaryKey, ( DoubleMap.Entry<String,String,String> entry ) -> collectedDoubleMapSet.putEntry( entry ) )
                  );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testG_forEachEntry_primaryKey_secondaryKey_consumer() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String, String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               forEachTestKey
                  (
                     ( String primaryKey, String secondaryKey ) ->
                        doubleMapSet.forEachEntry( primaryKey, secondaryKey, ( DoubleMap.Entry<String,String,String> entry ) -> collectedDoubleMapSet.putEntry( entry ) )
                  );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testH_forEachValue_triconsumer() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String, String, String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               doubleMapSet.forEachValue( ( String kp, String ks, String v) -> collectedDoubleMapSet.putValue( kp, ks, v ) );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:off
   }

   @Test
   public void testI_forEachValue_primaryKey_biconsumer() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String, String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               forEachTestKey
                  (
                     ( String primaryKey ) ->
                        doubleMapSet.forEachValue( primaryKey, ( String secondaryKey, String value ) -> collectedDoubleMapSet.putValue( primaryKey, secondaryKey, value ) )
                  );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testJ_forEachValue_primaryKey_biconsumer() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String, String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               forEachTestKey
                  (
                     ( String primaryKey, String secondaryKey ) ->
                        doubleMapSet.forEachValue( primaryKey, secondaryKey, ( String value ) -> collectedDoubleMapSet.putValue( primaryKey, secondaryKey, value ) )
                  );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testK_getOptional() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String, String, String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               forEachTestKey
                  (
                     ( String primaryKey, String secondaryKey ) ->
                     {
                        Optional<Set<String>> optionalSet = doubleMapSet.getOptional( primaryKey, secondaryKey );
                        Assert.assertTrue( id( mapLetter, primaryKey, secondaryKey ), optionalSet.isPresent() );
                        Set<String> optionalValueSet = optionalSet.get();
                        collectedDoubleMapSet.put( primaryKey, secondaryKey, optionalValueSet );
                     }
                  );
               DoubleMapSetTest.assertMapOk( mapLetter, doubleMapSet );
            }
         );
      //@formatter:off
   }

   @Test
   public void testL_removeEntry() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               boolean result;
               result = doubleMapSet.removeEntry( DoubleMap.entry( "A", "A", "A-A-4" ) );
               Assert.assertFalse( id( mapLetter ), result );
               result = doubleMapSet.removeEntry( DoubleMap.entry( "A", "D", "A-A-1" ) );
               Assert.assertFalse( id( mapLetter ), result );
               result = doubleMapSet.removeEntry( DoubleMap.entry( "D", "A", "A-A-1" ) );
               Assert.assertFalse( id( mapLetter ), result );
               forEachTestKey
                  (
                     ( String primaryKey, String secondaryKey, Integer i ) ->
                     {
                        boolean result2 = doubleMapSet.removeEntry( DoubleMap.entry( primaryKey, secondaryKey, primaryKey + "-" + secondaryKey + "-" + i ) );
                        Assert.assertTrue  ( id( mapLetter, primaryKey, secondaryKey ), result2 );
                     }
                  );
               Assert.assertEquals( id( mapLetter ), 0, doubleMapSet.sizeValues() );
            }
         );
   }

   @Test
   public void testM_removeValue() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               boolean result;
               result = doubleMapSet.removeValue( "A", "A", "A-A-4" );
               Assert.assertFalse( id( mapLetter ), result );
               result = doubleMapSet.removeValue( "A", "D", "A-A-1" );
               Assert.assertFalse( id( mapLetter ), result );
               result = doubleMapSet.removeValue( "D", "A", "A-A-1" );
               Assert.assertFalse( id( mapLetter ), result );
               forEachTestKey
                  (
                     ( String primaryKey, String secondaryKey, Integer i ) ->
                     {
                        boolean result2 = doubleMapSet.removeValue( primaryKey, secondaryKey, primaryKey + "-" + secondaryKey + "-" + i );
                        Assert.assertTrue  ( id( mapLetter, primaryKey, secondaryKey ), result2 );
                     }
                  );
               Assert.assertEquals( id( mapLetter ), 0, doubleMapSet.sizeValues() );
            }
         );
   }

   @Test
   public void testN_sizeValues() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               Assert.assertEquals( id( mapLetter ), 27, doubleMapSet.sizeValues() );
            }
         );
      //@formatter:on
   }

   @Test
   public void testO_sizeValues_primaryKey() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               forEachTestKey
                  (
                     ( String primaryKey ) ->
                     {
                        Assert.assertEquals( id( mapLetter, primaryKey ), 9, doubleMapSet.sizeValues( primaryKey ) );
                     }
                  );
            }
         );
      //@formatter:off
   }

   @Test
   public void testP_sizeValues_primaryKey_secondaryKey() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               forEachTestKey
                  (
                     ( String primaryKey, String secondaryKey ) ->
                     {
                        Assert.assertEquals( id( mapLetter, primaryKey ), 3, doubleMapSet.sizeValues( primaryKey, secondaryKey ) );
                     }
                  );
            }
         );
      //@formatter:off
   }

   @Test
   public void testQ_stream() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String,String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               doubleMapSet
                  .stream()
                  .map( ( value ) -> DoubleMap.entry( value.substring(0,1), value.substring(2,3), value ) )
                  .forEach( collectedDoubleMapSet::putEntry );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testR_stream_primaryKey() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String,String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               forEachTestKey
                  (
                     ( String primaryKey ) ->
                        doubleMapSet
                           .stream( primaryKey )
                           .map( ( value ) -> DoubleMap.entry( value.substring(0,1), value.substring(2,3), value ) )
                           .forEach( collectedDoubleMapSet::putEntry )
                  );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testS_stream_primaryKey() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String,String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               forEachTestKey
                  (
                     ( String primaryKey, String secondaryKey ) ->
                        doubleMapSet
                           .stream( primaryKey, secondaryKey )
                           .map( ( value ) -> DoubleMap.entry( value.substring(0,1), value.substring(2,3), value ) )
                           .forEach( collectedDoubleMapSet::putEntry )
                  );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testT_streamAllCollectionValuesAsEntries() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String,String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               doubleMapSet
                  .streamAllCollectionValuesAsEntries()
                  .forEach( collectedDoubleMapSet::putEntry );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testU_streamCollections() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String,String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               doubleMapSet
                  .streamCollections()
                  .flatMap( Collection::stream )
                  .map( ( value ) -> DoubleMap.entry( value.substring(0,1), value.substring(2,3), value ) )
                  .forEach( collectedDoubleMapSet::putEntry );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testV_streamEntries() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               DoubleMapSet<String,String,String> collectedDoubleMapSet = new DoubleHashMapHashSet<>();
               doubleMapSet
                  .streamEntries()
                  .forEach
                     (
                        ( entry ) ->
                        {
                           String primaryKey = entry.getPrimaryKey();
                           String secondaryKey = entry.getSecondaryKey();
                           Set<String> set = entry.getValue();
                           set.stream().forEach( (value ) -> collectedDoubleMapSet.putValue( primaryKey, secondaryKey, value ) );
                        }
                     );
               DoubleMapSetTest.assertMapOk( mapLetter, collectedDoubleMapSet );
            }
         );
      //@formatter:on
   }

   @Test
   public void testW_streamPrimaryKeys() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               Set<String> primaryKeys = doubleMapSet.keySet();
               Set<String> collectedPrimaryKeys =
                  doubleMapSet
                     .streamPrimaryKeys()
                     .collect( Collectors.toSet() );
               Assert.assertEquals( id( mapLetter ), primaryKeys, collectedPrimaryKeys );
            }
         );
      //@formatter:on
   }

   @Test
   public void testX_streamSecondaryKeys() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMapSet<String,String,String> doubleMapSet ) ->
            {
               forEachTestKey
                  (
                     ( String primaryKey ) ->
                     {
                        Set<String> secondaryKeys = doubleMapSet.keySet( primaryKey );
                        Set<String> collectedSecondaryKeys =
                           doubleMapSet
                              .streamSecondaryKeys( primaryKey )
                              .collect( Collectors.toSet() );
                        Assert.assertEquals( id( mapLetter ), secondaryKeys, collectedSecondaryKeys );

                     }
                  );
            }
         );
      //@formatter:on
   }

}
