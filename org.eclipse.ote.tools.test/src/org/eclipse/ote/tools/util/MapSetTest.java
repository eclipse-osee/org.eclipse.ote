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
   Map<String, MapSet<String, String>> maps;

   public MapSetTest(Supplier<MapSet<String, String>> mapSetSupplier) {
      this.mapSetSupplier = mapSetSupplier;
   }

   @SafeVarargs
   private static <T> Set<T> setOf(T... values) {
      HashSet<T> mutableHashSet = new HashSet<>(values.length * 2);
      for (T value : values) {
         mutableHashSet.add(value);
      }
      return mutableHashSet;
   }

   @Before
   public void testSetup() {
      //@formatter:off
      this.mapSetA =
         MapSet.ofEntries
            (
               new AbstractMap.SimpleEntry<>( "A", setOf( "A-1", "A-2", "A-3" ) ),
               new AbstractMap.SimpleEntry<>( "B", setOf( "B-1", "B-2", "B-3" ) ),
               new AbstractMap.SimpleEntry<>( "C", setOf( "C-1", "C-2", "C-3" ) ),
               new AbstractMap.SimpleEntry<>( "D", setOf( "D-1", "D-2", "D-3" ) )
            );
      //@formatter:off

      this.mapSetB = new HashMapHashSet<>( this.mapSetA );

      this.mapSetC = new HashMapHashSet<>();
      this.mapSetC.putAll( "A", setOf( "A-1", "A-2", "A-3" ) );
      this.mapSetC.putAll( "B", setOf( "B-1" ) );
      this.mapSetC.putAll( "B", setOf( "B-2", "B-3" ) );
      this.mapSetC.putAll( "C", setOf( "C-1", "C-2" ) );
      this.mapSetC.putAll( "C", setOf( "C-3" ) );
      this.mapSetC.putAll( "D", setOf( "D-1", "D-2", "D-3" ) );

      this.mapSetD = new HashMapHashSet<>();
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "A", "A-1") );
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "A", "A-2") );
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "A", "A-3") );
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "B", "B-1") );
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "B", "B-2") );
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "B", "B-3") );
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "C", "C-1") );
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "C", "C-2") );
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "C", "C-3") );
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "D", "D-1") );
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "D", "D-2") );
      this.mapSetD.putEntry( new AbstractMap.SimpleEntry<>( "D", "D-3") );

      this.mapSetE = new HashMapHashSet<>();
      this.mapSetE.putValue( "A", "A-1" );
      this.mapSetE.putValue( "A", "A-2" );
      this.mapSetE.putValue( "A", "A-3" );
      this.mapSetE.putValue( "B", "B-1" );
      this.mapSetE.putValue( "B", "B-2" );
      this.mapSetE.putValue( "B", "B-3" );
      this.mapSetE.putValue( "C", "C-1" );
      this.mapSetE.putValue( "C", "C-2" );
      this.mapSetE.putValue( "C", "C-3" );
      this.mapSetE.putValue( "D", "D-1" );
      this.mapSetE.putValue( "D", "D-2" );
      this.mapSetE.putValue( "D", "D-3" );

      this.maps = new LinkedHashMap<String,MapSet<String,String>>();
      this.maps.put( "A", this.mapSetA );
      this.maps.put( "B", this.mapSetB );
      this.maps.put( "C", this.mapSetC );
      this.maps.put( "D", this.mapSetD );
      this.maps.put( "E", this.mapSetE );
   }

   @Test
   public void testA_containsValueInAnyCollection() {
      for( Map.Entry<String,MapSet<String,String>> mapSetEntry : this.maps.entrySet() ) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String,String> mapSet = mapSetEntry.getValue();
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "A-1" ));
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "A-2" ));
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "A-3" ));
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "B-1" ));
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "B-2" ));
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "B-3" ));
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "C-1" ));
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "C-2" ));
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "C-3" ));
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "D-1" ));
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "D-2" ));
         Assert.assertTrue( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "D-3" ));
         Assert.assertFalse( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "E-1" ));
         Assert.assertFalse( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "E-2" ));
         Assert.assertFalse( "mapSet" + mapLetter + " mapSet contains", mapSet.containsValueInAnyCollection( "E-3" ));
      }
   }

   @Test
   public void testB_forEachEntry_consumer() {
      for( Map.Entry<String,MapSet<String,String>> mapSetEntry : this.maps.entrySet() ) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String,String> mapSet = mapSetEntry.getValue();
         MapSet<String,String> collectedMapSet = new HashMapHashSet<>();
         mapSet.forEachEntry( ( entry ) -> collectedMapSet.putEntry(entry) );
         Set<String> keySet = mapSet.keySet();
         Set<String> collectedKeySet = collectedMapSet.keySet();
         Assert.assertTrue("mapSet" + mapLetter + " key sets", keySet.equals(collectedKeySet));
         for (String key : keySet) {
            Set<String> valueSet = mapSet.get(key);
            Set<String> collectedValueSet = collectedMapSet.get(key);
            Assert.assertTrue("mapSet" + mapLetter + " key " + key + " value sets", valueSet.equals(collectedValueSet));
         }
      }
   }

   @Test
   public void testC_forEachEntry_key_consumer() {
      for( Map.Entry<String,MapSet<String,String>> mapSetEntry : this.maps.entrySet() ) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String,String> mapSet = mapSetEntry.getValue();
         MapSet<String,String> collectedMapSet = new HashMapHashSet<>();
         Set<String> keySet = mapSet.keySet();
         for( String key : keySet ) {
            mapSet.forEachEntry( key, (entry) -> collectedMapSet.putEntry( entry ) );
         }
         Set<String> collectedKeySet = collectedMapSet.keySet();
         Assert.assertTrue("mapSet" + mapLetter + " key sets", keySet.equals(collectedKeySet));
         for (String key : keySet) {
            Set<String> valueSet = mapSet.get(key);
            Set<String> collectedValueSet = collectedMapSet.get(key);
            Assert.assertTrue("mapSet" + mapLetter + " key " + key + " value sets", valueSet.equals(collectedValueSet));
         }
      }
   }

   @Test
   public void testD_forEachValue_biconsumer() {
      for( Map.Entry<String,MapSet<String,String>> mapSetEntry : this.maps.entrySet() ) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String,String> mapSet = mapSetEntry.getValue();
         MapSet<String,String> collectedMapSet = new HashMapHashSet<>();
         mapSet.forEachValue( ( k, v ) -> collectedMapSet.putValue( k, v ) );
         Set<String> keySet = mapSet.keySet();
         Set<String> collectedKeySet = collectedMapSet.keySet();
         Assert.assertTrue("mapSet" + mapLetter + " key sets", keySet.equals(collectedKeySet));
         for (String key : keySet) {
            Set<String> valueSet = mapSet.get(key);
            Set<String> collectedValueSet = collectedMapSet.get(key);
            Assert.assertTrue("mapSet" + mapLetter + " key " + key + " value sets", valueSet.equals(collectedValueSet));
         }
      }
   }

   @Test
   public void testE_forEachValue_key_consumer() {
      for( Map.Entry<String,MapSet<String,String>> mapSetEntry : this.maps.entrySet() ) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String,String> mapSet = mapSetEntry.getValue();
         MapSet<String,String> collectedMapSet = new HashMapHashSet<>();
         Set<String> keySet = mapSet.keySet();
         for( String key : keySet ) {
            mapSet.forEachValue( key, (value) -> collectedMapSet.putValue( key,value ) );
         }
         Set<String> collectedKeySet = collectedMapSet.keySet();
         Assert.assertTrue("mapSet" + mapLetter + " key sets", keySet.equals(collectedKeySet));
         for (String key : keySet) {
            Set<String> valueSet = mapSet.get(key);
            Set<String> collectedValueSet = collectedMapSet.get(key);
            Assert.assertTrue("mapSet" + mapLetter + " key " + key + " value sets", valueSet.equals(collectedValueSet));
         }
      }
   }

   @Test
   public void testF_getOptional() {
      for( Map.Entry<String,MapSet<String,String>> mapSetEntry : this.maps.entrySet() ) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String,String> mapSet = mapSetEntry.getValue();
         Set<String> keySet = mapSet.keySet();
         for( String key : keySet ) {
            Optional<Set<String>> optionalSet = mapSet.getOptional( key );
            Assert.assertTrue( "mapSet" + mapLetter + " set optional value present", optionalSet.isPresent() );
            Set<String> valueSet = mapSet.get(key);
            Set<String> optionalValueSet = optionalSet.get();
            Assert.assertTrue("mapSet" + mapLetter + " key " + key + " value sets", valueSet.equals(optionalValueSet));
         }

         Assert.assertTrue( "mapSet" + mapLetter + " set optional value not present", !mapSet.getOptional( "Z" ).isPresent());
      }
   }

   @Test
   public void testG_removeEntry() {
      for (Map.Entry<String, MapSet<String, String>> mapSetEntry : this.maps.entrySet()) {
         String mapLetter = mapSetEntry.getKey();
         if( "A".equals( mapLetter) ) {
            //Map A is immutable
            continue;
         }
         MapSet<String, String> mapSet = mapSetEntry.getValue();
         mapSet.removeEntry( new AbstractMap.SimpleEntry<>( "A", "A-2" ) );
         Set<String> setA = mapSet.get("A");
         Assert.assertTrue( "mapSet" + mapLetter + " key A not contains A-1", setA.contains( "A-1") );
         Assert.assertFalse( "mapSet" + mapLetter + " key A not contains A-2", setA.contains( "A-2") );
         Assert.assertTrue( "mapSet" + mapLetter + " key A not contains A-3", setA.contains( "A-3") );
      }
   }

   @Test
   public void testH_removeValue() {
      for (Map.Entry<String, MapSet<String, String>> mapSetEntry : this.maps.entrySet()) {
         String mapLetter = mapSetEntry.getKey();
         if( "A".equals( mapLetter) ) {
            //Map A is immutable
            continue;
         }
         MapSet<String, String> mapSet = mapSetEntry.getValue();
         mapSet.removeValue( "A", "A-2" );
         Set<String> setA = mapSet.get("A");
         Assert.assertTrue( "mapSet" + mapLetter + " key A not contains A-1", setA.contains( "A-1") );
         Assert.assertFalse( "mapSet" + mapLetter + " key A not contains A-2", setA.contains( "A-2") );
         Assert.assertTrue( "mapSet" + mapLetter + " key A not contains A-3", setA.contains( "A-3") );
      }
   }

   @Test
   public void testI_size() {
      for (Map.Entry<String, MapSet<String, String>> mapSetEntry : this.maps.entrySet()) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String, String> mapSet = mapSetEntry.getValue();
         Set<String> keySet = mapSet.keySet();
         Assert.assertEquals("mapSet" + mapLetter + " keySet size", 4, keySet.size());
         Assert.assertTrue("mapSet" + mapLetter + " keySet contains A", keySet.contains("A"));
         Assert.assertTrue("mapSet" + mapLetter + " keySet contains B", keySet.contains("B"));
         Assert.assertTrue("mapSet" + mapLetter + " keySet contains C", keySet.contains("C"));
         Assert.assertTrue("mapSet" + mapLetter + " keySet contains D", keySet.contains("D"));
         for (String key : keySet) {
            Assert.assertEquals("mapSet" + mapLetter + " key " + key + " size", 3, mapSet.size(key));
         }
      }
   }

   @Test
   public void testJ_sizeValues() {
      for (Map.Entry<String, MapSet<String, String>> mapSetEntry : this.maps.entrySet()) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String, String> mapSet = mapSetEntry.getValue();
         Assert.assertEquals("mapSet" + mapLetter + " sizeValues", 12, mapSet.sizeValues());
      }
   }

   @Test
   public void testK_stream() {
      for (Map.Entry<String, MapSet<String, String>> mapSetEntry : this.maps.entrySet()) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String, String> mapSet = mapSetEntry.getValue();
         Set<String> keySet = mapSet.keySet();
         Assert.assertEquals("mapSet" + mapLetter + " keySet size", 4, keySet.size());
         for (String key : keySet) {
            Set<String> set = mapSet.get(key);
            Set<String> collectedSet = mapSet.stream(key).collect(Collectors.toSet());
            Assert.assertTrue("mapSet" + mapLetter + " key " + key + " stream", set.equals(collectedSet));
         }
      }
   }

   @Test
   public void testL_streamAllCollectionValuesAsEntries() {
      for (Map.Entry<String, MapSet<String, String>> mapSetEntry : this.maps.entrySet()) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String, String> mapSet = mapSetEntry.getValue();
         MapSet<String, String> collectedMapSet = new HashMapHashSet<>();
         //@formatter:off
         mapSet
            .streamAllCollectionValuesAsEntries()
            .forEach( collectedMapSet::putEntry );
         //@formatter:on
         Set<String> keySet = mapSet.keySet();
         Set<String> collectedKeySet = collectedMapSet.keySet();
         Assert.assertTrue("mapSet" + mapLetter + " key sets", keySet.equals(collectedKeySet));
         for (String key : keySet) {
            Set<String> valueSet = mapSet.get(key);
            Set<String> collectedValueSet = collectedMapSet.get(key);
            Assert.assertTrue("mapSet" + mapLetter + " key " + key + " value sets", valueSet.equals(collectedValueSet));
         }
      }
   }

   @Test
   public void testM_streamCollections() {
      for (Map.Entry<String, MapSet<String, String>> mapSetEntry : this.maps.entrySet()) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String, String> mapSet = mapSetEntry.getValue();
         //@formatter:off
         List<Set<String>> collectedSets =
         mapSet
            .streamCollections()
            .collect( Collectors.toList() );
         //@formatter:on
         Assert.assertEquals("mapSet" + mapLetter + " collected collections count", 4, collectedSets.size());
         for (Set<String> collectedSet : collectedSets) {
            Assert.assertTrue("mapSet" + mapLetter + " collected set contained in original",
               mapSet.containsValue(collectedSet));
         }
      }
   }

   @Test
   public void testN_streamEntries() {
      for (Map.Entry<String, MapSet<String, String>> mapSetEntry : this.maps.entrySet()) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String, String> mapSet = mapSetEntry.getValue();
         MapSet<String, String> collectedMapSet = new HashMapHashSet<>();
         //@formatter:off
         mapSet
            .streamEntries()
            .forEach(  ( entry ) -> collectedMapSet.putAll( entry.getKey(), entry.getValue() ) );
         //@formatter:on
         Set<String> keySet = mapSet.keySet();
         Set<String> collectedKeySet = collectedMapSet.keySet();
         Assert.assertTrue("mapSet" + mapLetter + " key sets", keySet.equals(collectedKeySet));
         for (String key : keySet) {
            Set<String> valueSet = mapSet.get(key);
            Set<String> collectedValueSet = collectedMapSet.get(key);
            Assert.assertTrue("mapSet" + mapLetter + " key " + key + " value sets", valueSet.equals(collectedValueSet));
         }
      }
   }

   @Test
   public void testO_streamKeys() {
      for (Map.Entry<String, MapSet<String, String>> mapSetEntry : this.maps.entrySet()) {
         String mapLetter = mapSetEntry.getKey();
         MapSet<String, String> mapSet = mapSetEntry.getValue();
         //@formatter:off
         Set<String> collectedKeySet =
         mapSet
            .streamKeys()
            .collect( Collectors.toSet() );
         //@formatter:on
         Assert.assertTrue("mapSet" + mapLetter + " key set", mapSet.keySet().equals(collectedKeySet));
      }
   }
}
