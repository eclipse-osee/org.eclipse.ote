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
 *     Boeing - Initial API and implementation
 **********************************************************************/

package org.eclipse.ote.tools.util;

import static org.eclipse.ote.tools.util.MapTestUtils.forEachMutableTestMap;
import static org.eclipse.ote.tools.util.MapTestUtils.forEachTestMap;
import static org.eclipse.ote.tools.util.MapTestUtils.id;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for implementations of the {@link DoubleMap} interface.
 *
 * @author Loren K. Ashley
 */

@RunWith(Parameterized.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DoubleMapTest {

   /**
    * An {@link List} of arrays with the test parameters for each iteration of the {@link DoubleMapTest} test suite.
    * Each array contains a single entry which is a {@link Supplier} that provides the implementation of the
    * {@link DoubleMap} interface for that iteration of the test suite. {@link Supplier}s are used in the parameter
    * array instead of a {@link DoubleMap} implementation so that each test gets a fresh map.
    *
    * @return {@link List} of test parameters for each iteration of the {@link DoubleMapTest} test suite.
    */

   @Parameters
   public static Collection<Object[]> data() {
      //@formatter:off
      List<Object[]> parameterList = new LinkedList<>();
      parameterList.add( new Supplier[] { () -> new DoubleHashMap<String,String,String>()                     } );
      parameterList.add( new Supplier[] { () -> new DoubleHashMap<String,String,String>( 32, 0.5f )           } );
      parameterList.add( new Supplier[] { () -> new DoubleHashMap<String,String,String>( 32, 0.5f, 32, 0.5f ) } );
      return parameterList;
      //@formatter:on
   }

   private final Supplier<DoubleMap<String, String, String>> doubleMapSupplier;

   private DoubleMap<String, String, String> doubleMapA;
   private DoubleMap<String, String, String> doubleMapB;
   private DoubleMap<String, String, String> doubleMapC;
   private DoubleMap<String, String, String> doubleMapD;
   private DoubleMap<String, String, String> doubleMapE;
   private DoubleMap<String, String, String> doubleMapF;

   private Map<String, DoubleMap<String, String, String>> maps;

   public DoubleMapTest(Supplier<DoubleMap<String, String, String>> doubleMapSupplier) {
      this.doubleMapSupplier = doubleMapSupplier;
   }

   @SuppressWarnings("unchecked")
   @Before
   public void testSetup() {

      //@formatter:off
      this.doubleMapA =
         DoubleMap.ofEntries
            (
               DoubleMap.entry( "A", "A", "VALUE (A,A)" ),
               DoubleMap.entry( "A", "B", "VALUE (A,B)" ),
               DoubleMap.entry( "A", "C", "VALUE (A,C)" ),

               DoubleMap.entry( "B", "A", "VALUE (B,A)" ),
               DoubleMap.entry( "B", "B", "VALUE (B,B)" ),
               DoubleMap.entry( "B", "C", "VALUE (B,C)" ),

               DoubleMap.entry( "C", "A", "VALUE (C,A)" ),
               DoubleMap.entry( "C", "B", "VALUE (C,B)" ),
               DoubleMap.entry( "C", "C", "VALUE (C,C)" )
            );
      //@formatter:on

      this.doubleMapB = this.doubleMapSupplier.get();

      this.doubleMapB.put("A", "A", "VALUE (A,A)");
      this.doubleMapB.put("A", "B", "VALUE (A,B)");
      this.doubleMapB.put("A", "C", "VALUE (A,C)");

      this.doubleMapB.put("B", "A", "VALUE (B,A)");
      this.doubleMapB.put("B", "B", "VALUE (B,B)");
      this.doubleMapB.put("B", "C", "VALUE (B,C)");

      this.doubleMapB.put("C", "A", "VALUE (C,A)");
      this.doubleMapB.put("C", "B", "VALUE (C,B)");
      this.doubleMapB.put("C", "C", "VALUE (C,C)");

      this.doubleMapC = this.doubleMapSupplier.get();

      this.doubleMapC.put(DoubleMap.entry("A", "A", "VALUE (A,A)"));
      this.doubleMapC.put(DoubleMap.entry("A", "B", "VALUE (A,B)"));
      this.doubleMapC.put(DoubleMap.entry("A", "C", "VALUE (A,C)"));
      this.doubleMapC.put(DoubleMap.entry("B", "A", "VALUE (B,A)"));
      this.doubleMapC.put(DoubleMap.entry("B", "B", "VALUE (B,B)"));
      this.doubleMapC.put(DoubleMap.entry("B", "C", "VALUE (B,C)"));
      this.doubleMapC.put(DoubleMap.entry("C", "A", "VALUE (C,A)"));
      this.doubleMapC.put(DoubleMap.entry("C", "B", "VALUE (C,B)"));
      this.doubleMapC.put(DoubleMap.entry("C", "C", "VALUE (C,C)"));

      this.doubleMapD = this.doubleMapSupplier.get();

      Map<String, String> mapDsecondaryMapA = new HashMap<>();

      mapDsecondaryMapA.put("A", "VALUE (A,A)");
      mapDsecondaryMapA.put("B", "VALUE (A,B)");
      mapDsecondaryMapA.put("C", "VALUE (A,C)");

      Map<String, String> mapDsecondaryMapB = new HashMap<>();

      mapDsecondaryMapB.put("A", "VALUE (B,A)");
      mapDsecondaryMapB.put("B", "VALUE (B,B)");
      mapDsecondaryMapB.put("C", "VALUE (B,C)");

      Map<String, String> mapDsecondaryMapC = new HashMap<>();

      mapDsecondaryMapC.put("A", "VALUE (C,A)");
      mapDsecondaryMapC.put("B", "VALUE (C,B)");
      mapDsecondaryMapC.put("C", "VALUE (C,C)");

      this.doubleMapD.put("A", mapDsecondaryMapA);
      this.doubleMapD.put("B", mapDsecondaryMapB);
      this.doubleMapD.put("C", mapDsecondaryMapC);

      this.doubleMapE = this.doubleMapSupplier.get();

      Map<String, String> mapEsecondaryMapA = new HashMap<>();

      mapEsecondaryMapA.put("A", "VALUE (A,A)");
      mapEsecondaryMapA.put("B", "VALUE (A,B)");
      mapEsecondaryMapA.put("C", "VALUE (A,C)");

      Map<String, String> mapEsecondaryMapB = new HashMap<>();

      mapEsecondaryMapB.put("A", "VALUE (B,A)");
      mapEsecondaryMapB.put("B", "VALUE (B,B)");
      mapEsecondaryMapB.put("C", "VALUE (B,C)");

      Map<String, String> mapEsecondaryMapC = new HashMap<>();

      mapEsecondaryMapC.put("A", "VALUE (C,A)");
      mapEsecondaryMapC.put("B", "VALUE (C,B)");
      mapEsecondaryMapC.put("C", "VALUE (C,C)");

      this.doubleMapE.putAll("A", mapEsecondaryMapA);
      this.doubleMapE.putAll("B", mapEsecondaryMapB);
      this.doubleMapE.putAll("C", mapEsecondaryMapC);

      this.doubleMapF = this.doubleMapSupplier.get();

      this.doubleMapF.putAll(this.doubleMapA);

      this.maps = new LinkedHashMap<String, DoubleMap<String, String, String>>();

      this.maps.put("A", this.doubleMapA);
      this.maps.put("B", this.doubleMapB);
      this.maps.put("C", this.doubleMapC);
      this.maps.put("D", this.doubleMapD);
      this.maps.put("E", this.doubleMapE);
      this.maps.put("F", this.doubleMapF);

      MapTestUtils.maps = (Map<String, Object>) (Object) this.maps;
   }

   private void assertMapOk(String mapLetter, DoubleMap<String, String, String> doubleMap) {
      String value;
      Assert.assertTrue(id(mapLetter), doubleMap.containsKey("A", "A"));
      value = doubleMap.get("A", "A");
      Assert.assertEquals(id(mapLetter), "VALUE (A,A)", value);
      Assert.assertTrue(id(mapLetter), doubleMap.containsKey("A", "B"));
      value = doubleMap.get("A", "B");
      Assert.assertEquals(id(mapLetter), "VALUE (A,B)", value);
      Assert.assertTrue(id(mapLetter), doubleMap.containsKey("A", "C"));
      value = doubleMap.get("A", "C");
      Assert.assertEquals(id(mapLetter), "VALUE (A,C)", value);
      Assert.assertFalse(id(mapLetter), doubleMap.containsKey("A", "D"));
      value = doubleMap.get("A", "D");
      Assert.assertNull(id(mapLetter), value);
      Assert.assertTrue(id(mapLetter), doubleMap.containsKey("B", "A"));
      value = doubleMap.get("B", "A");
      Assert.assertEquals(id(mapLetter), "VALUE (B,A)", value);
      Assert.assertTrue(id(mapLetter), doubleMap.containsKey("B", "B"));
      value = doubleMap.get("B", "B");
      Assert.assertEquals(id(mapLetter), "VALUE (B,B)", value);
      Assert.assertTrue(id(mapLetter), doubleMap.containsKey("B", "C"));
      value = doubleMap.get("B", "C");
      Assert.assertEquals(id(mapLetter), "VALUE (B,C)", value);
      Assert.assertFalse(id(mapLetter), doubleMap.containsKey("B", "D"));
      value = doubleMap.get("B", "D");
      Assert.assertNull(id(mapLetter), value);
      Assert.assertTrue(id(mapLetter), doubleMap.containsKey("C", "A"));
      value = doubleMap.get("C", "A");
      Assert.assertEquals(id(mapLetter), "VALUE (C,A)", value);
      Assert.assertTrue(id(mapLetter), doubleMap.containsKey("C", "B"));
      value = doubleMap.get("C", "B");
      Assert.assertEquals(id(mapLetter), "VALUE (C,B)", value);
      Assert.assertTrue(id(mapLetter), doubleMap.containsKey("C", "C"));
      value = doubleMap.get("C", "C");
      Assert.assertEquals(id(mapLetter), "VALUE (C,C)", value);
      Assert.assertFalse(id(mapLetter), doubleMap.containsKey("C", "D"));
      value = doubleMap.get("C", "D");
      Assert.assertNull(id(mapLetter), value);
      Assert.assertFalse(id(mapLetter), doubleMap.containsKey("D", "A"));
      value = doubleMap.get("D", "A");
      Assert.assertNull(id(mapLetter), value);
   }

   private static String[] testKeySet = {"A", "B", "C"};

   private static void forEachTestKey(Consumer<String> keyTest) {
      Arrays.stream(DoubleMapTest.testKeySet).forEach(keyTest::accept);
   }

   //   private void forEachMutableTestMap(BiConsumer<String, DoubleMap<String, String, String>> mapTest) {
   //      for (Map.Entry<String, DoubleMap<String, String, String>> mapEntry : this.maps.entrySet()) {
   //         String mapLetter = mapEntry.getKey();
   //         if ("A".equals(mapLetter)) {
   //            //Map A is immutable
   //            continue;
   //         }
   //         DoubleMap<String, String, String> doubleMap = mapEntry.getValue();
   //         mapTest.accept(mapLetter, doubleMap);
   //      }
   //   }
   //
   //   private void forEachTestMap(BiConsumer<String, DoubleMap<String, String, String>> mapTest) {
   //      for (Map.Entry<String, DoubleMap<String, String, String>> mapEntry : this.maps.entrySet()) {
   //         String mapLetter = mapEntry.getKey();
   //         DoubleMap<String, String, String> doubleMap = mapEntry.getValue();
   //         mapTest.accept(mapLetter, doubleMap);
   //      }
   //   }

   @Test
   public void testAA_clear_isEmpty() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Assert.assertFalse(id(mapLetter), doubleMap.isEmpty());
               doubleMap.clear();
               Assert.assertEquals(id(mapLetter), 0, doubleMap.size());
               Assert.assertNull(id(mapLetter), doubleMap.get("A"));
               Assert.assertNull(id(mapLetter), doubleMap.get("A", "A"));
               Assert.assertNull(id(mapLetter), doubleMap.get("A", "B"));
               Assert.assertNull(id(mapLetter), doubleMap.get("A", "C"));
               Assert.assertNull(id(mapLetter), doubleMap.get("A", "D"));
               Assert.assertNull(id(mapLetter), doubleMap.get("B"));
               Assert.assertNull(id(mapLetter), doubleMap.get("B", "A"));
               Assert.assertNull(id(mapLetter), doubleMap.get("B", "B"));
               Assert.assertNull(id(mapLetter), doubleMap.get("B", "C"));
               Assert.assertNull(id(mapLetter), doubleMap.get("B", "D"));
               Assert.assertNull(id(mapLetter), doubleMap.get("C"));
               Assert.assertNull(id(mapLetter), doubleMap.get("C", "A"));
               Assert.assertNull(id(mapLetter), doubleMap.get("C", "B"));
               Assert.assertNull(id(mapLetter), doubleMap.get("C", "C"));
               Assert.assertNull(id(mapLetter), doubleMap.get("C", "D"));
               Assert.assertNull(id(mapLetter), doubleMap.get("D"));
               Assert.assertTrue(id(mapLetter), doubleMap.isEmpty());
            }
         );
      //@formatter:on
   }

   @Test
   public void testAB_containsKey_primary() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("A"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("B"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("C"));
               Assert.assertFalse(id(mapLetter), doubleMap.containsKey("D"));
            }
         );
      //@formatter:on
   }

   @Test
   public void testAC_containsKey_primary_secondary() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("A", "A"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("A", "B"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("A", "C"));
               Assert.assertFalse(id(mapLetter), doubleMap.containsKey("A", "D"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("B", "A"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("B", "B"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("B", "C"));
               Assert.assertFalse(id(mapLetter), doubleMap.containsKey("B", "D"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("C", "A"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("C", "B"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsKey("C", "C"));
               Assert.assertFalse(id(mapLetter), doubleMap.containsKey("C", "D"));
               Assert.assertFalse(id(mapLetter), doubleMap.containsKey("D", "A"));
            }
         );
      //@formatter:on
   }

   @Test
   public void testAD_containsValue() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Assert.assertTrue(id(mapLetter), doubleMap.containsValue("VALUE (A,A)"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsValue("VALUE (A,B)"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsValue("VALUE (A,C)"));
               Assert.assertFalse(id(mapLetter), doubleMap.containsValue("VALUE (A,D)"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsValue("VALUE (B,A)"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsValue("VALUE (B,B)"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsValue("VALUE (B,C)"));
               Assert.assertFalse(id(mapLetter), doubleMap.containsValue("VALUE (B,D)"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsValue("VALUE (C,A)"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsValue("VALUE (C,B)"));
               Assert.assertTrue(id(mapLetter), doubleMap.containsValue("VALUE (C,C)"));
               Assert.assertFalse(id(mapLetter), doubleMap.containsValue("VALUE (C,D)"));
               Assert.assertFalse(id(mapLetter), doubleMap.containsValue("VALUE (D,A)"));
            }
         );
      //@formatter:on
   }

   @Test
   public void testAE_entrySet_contains() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<DoubleMap.Entry<String, String, String>> entrySet = doubleMap.entrySet();
               Set<DoubleMap.Entry<String, String, String>> entrySetX = doubleMap.entrySet();
               Assert.assertTrue(id(mapLetter), entrySet == entrySetX);

               Assert.assertTrue(id(mapLetter), entrySet.contains(DoubleMap.entry("A", "A", "VALUE (A,A)")));
               Assert.assertTrue(id(mapLetter), entrySet.contains(DoubleMap.entry("A", "B", "VALUE (A,B)")));
               Assert.assertTrue(id(mapLetter), entrySet.contains(DoubleMap.entry("A", "C", "VALUE (A,C)")));

               Assert.assertTrue(id(mapLetter), entrySet.contains(DoubleMap.entry("B", "A", "VALUE (B,A)")));
               Assert.assertTrue(id(mapLetter), entrySet.contains(DoubleMap.entry("B", "B", "VALUE (B,B)")));
               Assert.assertTrue(id(mapLetter), entrySet.contains(DoubleMap.entry("B", "C", "VALUE (B,C)")));

               Assert.assertTrue(id(mapLetter), entrySet.contains(DoubleMap.entry("C", "A", "VALUE (C,A)")));
               Assert.assertTrue(id(mapLetter), entrySet.contains(DoubleMap.entry("C", "B", "VALUE (C,B)")));
               Assert.assertTrue(id(mapLetter), entrySet.contains(DoubleMap.entry("C", "C", "VALUE (C,C)")));

               Assert.assertFalse(id(mapLetter), entrySet.contains(DoubleMap.entry("D", "A", "VALUE (D,A)")));
            }
         );
      //@formatter:on
   }

   @Test
   public void testAF_entrySet_set_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<DoubleMap.Entry<String, String, String>> entrySet = doubleMap.entrySet();
               DoubleMap.Entry<String, String, String> aaEntry = DoubleMap.entry("A", "A", "VALUE (A,A)");
               Assert.assertTrue(id(mapLetter), entrySet.contains(aaEntry));
               entrySet.remove(aaEntry);
               Assert.assertFalse(id(mapLetter), entrySet.contains(aaEntry));
            }
         );
      //@formatter:on
   }

   @Test
   public void testAG_entrySet_set_iterator_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<DoubleMap.Entry<String, String, String>> entrySet = doubleMap.entrySet();
               DoubleMap.Entry<String, String, String> aaEntry = DoubleMap.entry("A", "A", "VALUE (A,A)");
               Assert.assertTrue(id(mapLetter), entrySet.contains(aaEntry));
               Iterator<DoubleMap.Entry<String, String, String>> iterator = entrySet.iterator();
               boolean found = false;
               while (iterator.hasNext()) {
                  DoubleMap.Entry<String, String, String> loopEntry = iterator.next();
                  if (aaEntry.equals(loopEntry)) {
                     iterator.remove();
                     found = true;
                  }
               }
               Assert.assertTrue(id(mapLetter), found);
               Assert.assertFalse(id(mapLetter), entrySet.contains(aaEntry));
            }
         );
      //@formatter:on
   }

   @Test
   public void testAH_entrySet_add() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<DoubleMap.Entry<String, String, String>> entrySet = doubleMap.entrySet();
               DoubleMap.Entry<String, String, String> ddEntry = DoubleMap.entry("D", "D", "VALUE (D,D)");
               String value = null;
               value = doubleMap.get(ddEntry.getPrimaryKey(), ddEntry.getSecondaryKey());
               Assert.assertNull(id(mapLetter), value);
               Assert.assertFalse(id(mapLetter), entrySet.contains(ddEntry));
               entrySet.add(ddEntry);
               Assert.assertTrue(id(mapLetter), entrySet.contains(ddEntry));
               value = doubleMap.get(ddEntry.getPrimaryKey(), ddEntry.getSecondaryKey());
               Assert.assertTrue(id(mapLetter), ddEntry.getValue().equals(value));
            }
         );
      //@formatter:on
   }

   @Test
   public void testAI_entrySet_map_put() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<DoubleMap.Entry<String, String, String>> entrySet = doubleMap.entrySet();
               DoubleMap.Entry<String, String, String> ddEntry = DoubleMap.entry("D", "D", "VALUE (D,D)");
               Assert.assertFalse(id(mapLetter), entrySet.contains(ddEntry));
               doubleMap.put(ddEntry);
               Assert.assertTrue(id(mapLetter), entrySet.contains(ddEntry));
            }
         );
      //@formatter:on
   }

   @Test
   public void testAJ_entrySet_map_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<DoubleMap.Entry<String, String, String>> entrySet = doubleMap.entrySet();
               DoubleMap.Entry<String, String, String> aaEntry = DoubleMap.entry("A", "A", "VALUE (A,A)");
               Assert.assertTrue(id(mapLetter), entrySet.contains(aaEntry));
               doubleMap.remove(aaEntry.getPrimaryKey(), aaEntry.getSecondaryKey());
               Assert.assertFalse(id(mapLetter), entrySet.contains(aaEntry));
            }
         );
      //@formatter:on
   }

   @Test
   public void testAK_entrySet_secondary_contains() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<Map.Entry<String, String>> entrySet = doubleMap.entrySet(primaryKey);
                        Set<Map.Entry<String, String>> entrySetX = doubleMap.entrySet(primaryKey);
                        Assert.assertTrue(id( mapLetter, primaryKey ), entrySet == entrySetX);
                        Assert.assertTrue(id( mapLetter, primaryKey ), entrySet.contains(new AbstractMap.SimpleEntry<>("A", "VALUE (" + primaryKey + ",A)")));
                        Assert.assertTrue(id( mapLetter, primaryKey ), entrySet.contains(new AbstractMap.SimpleEntry<>("B", "VALUE (" + primaryKey + ",B)")));
                        Assert.assertTrue(id( mapLetter, primaryKey ), entrySet.contains(new AbstractMap.SimpleEntry<>("C", "VALUE (" + primaryKey + ",C)")));
                        Assert.assertFalse(id( mapLetter, primaryKey ), entrySet.contains(new AbstractMap.SimpleEntry<>("D", "VALUE (" + primaryKey + ",D)")));
                     }
                  );
               Set<Map.Entry<String, String>> entrySet = doubleMap.entrySet("D");
               Assert.assertNull(id( mapLetter ),entrySet);
            }
         );
      //@formatter:on
   }

   @Test
   public void testAL_entrySet_secondary_set_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<Map.Entry<String, String>> entrySet = doubleMap.entrySet(primaryKey);
                        Map.Entry<String,String> aEntry = new AbstractMap.SimpleEntry<>("A", "VALUE (" + primaryKey + ",A)");
                        String value;
                        Assert.assertTrue(id( mapLetter, primaryKey ), entrySet.contains( aEntry ));
                        value = doubleMap.get(primaryKey, aEntry.getKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), aEntry.getValue().equals( value ) );
                        entrySet.remove(aEntry);
                        Assert.assertFalse(id( mapLetter, primaryKey ), entrySet.contains( aEntry ));
                        value = doubleMap.get(primaryKey, aEntry.getKey());
                        Assert.assertNull(id( mapLetter, primaryKey ), value  );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testAM_entrySet_secondary_set_iterator_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<Map.Entry<String, String>> entrySet = doubleMap.entrySet(primaryKey);
                        Map.Entry<String,String> aEntry = new AbstractMap.SimpleEntry<>("A", "VALUE (" + primaryKey + ",A)");
                        String value;
                        Assert.assertTrue(id( mapLetter, primaryKey ), entrySet.contains( aEntry ));
                        value = doubleMap.get(primaryKey, "A");
                        Assert.assertTrue(id( mapLetter, primaryKey ), aEntry.getValue().equals( value ) );
                        Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
                        boolean found = false;
                        while (iterator.hasNext()) {
                           Map.Entry<String, String> loopEntry = iterator.next();
                           if (aEntry.equals(loopEntry)) {
                              iterator.remove();
                              found = true;
                           }
                        }
                        Assert.assertTrue(id( mapLetter, primaryKey ), found);
                        Assert.assertFalse(id( mapLetter, primaryKey ), entrySet.contains( aEntry ));
                        value = doubleMap.get(primaryKey, "A");
                        Assert.assertFalse(id( mapLetter, primaryKey ), aEntry.getValue().equals( value ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testAN_entrySet_secondary_add() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<Map.Entry<String, String>> entrySet = doubleMap.entrySet(primaryKey);
                        Map.Entry<String,String> dEntry = new AbstractMap.SimpleEntry<>("D", "VALUE (" + primaryKey + ",D)");
                        String value;
                        Assert.assertFalse(id( mapLetter, primaryKey ), entrySet.contains( dEntry ));
                        value = doubleMap.get(primaryKey, dEntry.getKey());
                        Assert.assertFalse(id( mapLetter, primaryKey ), dEntry.getValue().equals( value ) );
                        entrySet.add(dEntry);
                        Assert.assertTrue(id( mapLetter, primaryKey ), entrySet.contains( dEntry ));
                        value = doubleMap.get(primaryKey, dEntry.getKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), dEntry.getValue().equals( value ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testAO_entrySet_secondary_map_put() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<Map.Entry<String, String>> entrySet = doubleMap.entrySet(primaryKey);
                        Map.Entry<String,String> dEntry = new AbstractMap.SimpleEntry<>("D", "VALUE (" + primaryKey + ",D)");
                        String value;
                        Assert.assertFalse(id( mapLetter, primaryKey ), entrySet.contains( dEntry ));
                        value = doubleMap.get(primaryKey, dEntry.getKey());
                        Assert.assertNull(id( mapLetter, primaryKey ), value );
                        doubleMap.put(primaryKey,dEntry.getKey(),dEntry.getValue());
                        Assert.assertTrue(id( mapLetter, primaryKey ), entrySet.contains( dEntry ));
                        value = doubleMap.get(primaryKey, dEntry.getKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), dEntry.getValue().equals( value ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testAP_entrySet_secondary_map_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<Map.Entry<String, String>> entrySet = doubleMap.entrySet(primaryKey);
                        Map.Entry<String,String> aEntry = new AbstractMap.SimpleEntry<>("A", "VALUE (" + primaryKey + ",A)");
                        String value;
                        Assert.assertTrue(id( mapLetter, primaryKey ), entrySet.contains( aEntry ));
                        value = doubleMap.get(primaryKey, aEntry.getKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), aEntry.getValue().equals( value ) );
                        doubleMap.remove(primaryKey,aEntry.getKey());
                        Assert.assertFalse(id( mapLetter, primaryKey ), entrySet.contains( aEntry ));
                        value = doubleMap.get(primaryKey, aEntry.getKey());
                        Assert.assertFalse(id( mapLetter, primaryKey ), aEntry.getValue().equals( value ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testAQ_forEach_triConsumer() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               DoubleMap<String, String, String> captureMap = this.doubleMapSupplier.get();
               doubleMap.forEach((kp, ks, v) -> captureMap.put(kp, ks, v));
               this.assertMapOk(mapLetter, captureMap);
            }
         );
      //@formatter:on
   }

   @Test
   public void testAR_forEach_consumer_doubleMapEntry() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               DoubleMap<String, String, String> captureMap = this.doubleMapSupplier.get();
               doubleMap.forEach((entry) -> captureMap.put(entry));
               this.assertMapOk(mapLetter, captureMap);
            }
         );
      //@formatter:on
   }

   @Test
   public void testAS_get_primary() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Map<String, String> secondaryMappings = doubleMap.get( primaryKey );

                        Assert.assertNotNull ( id( mapLetter, primaryKey ), secondaryMappings );
                        Assert.assertEquals  ( id( mapLetter, primaryKey ), 3,                          secondaryMappings.size()       );
                        Assert.assertEquals  ( id( mapLetter, primaryKey ), secondaryMappings.get("A"), "VALUE (" + primaryKey + ",A)" );
                        Assert.assertEquals  ( id( mapLetter, primaryKey ), secondaryMappings.get("B"), "VALUE (" + primaryKey + ",B)" );
                        Assert.assertEquals  ( id( mapLetter, primaryKey ), secondaryMappings.get("C"), "VALUE (" + primaryKey + ",C)" );
                     }
                  );
               Map<String,String> secondaryMappings = doubleMap.get("D");
               Assert.assertNull(secondaryMappings);
            }
         );
      //@formatter:on
   }

   @Test
   public void testAT_get_secondary() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        forEachTestKey
                           (
                              ( secondaryKey ) ->
                              {
                                 String value = doubleMap.get( primaryKey, secondaryKey );
                                 Assert.assertEquals( id( mapLetter, primaryKey, secondaryKey ), "VALUE (" + primaryKey + "," + secondaryKey + ")",  value );
                              }
                           );
                        String value = doubleMap.get(primaryKey,"D");
                        Assert.assertNull(value);
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testAU_keySet_primary() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<String> keySet = doubleMap.keySet();
               Set<String> keySetX = doubleMap.keySet();
               Assert.assertTrue(id(mapLetter), keySet == keySetX);
               Assert.assertTrue(keySet.contains("A"));
               Assert.assertTrue(keySet.contains("B"));
               Assert.assertTrue(keySet.contains("C"));
               Assert.assertFalse(keySet.contains("D"));
            }
         );
      //@formatter:on
   }

   @Test
   public void testAV_keySet_primary_set_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<String> keySet = doubleMap.keySet();
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Map<String,String> secondaryMap;
                        Map<String,String> secondaryMapAbc = new HashMap<>();
                        secondaryMapAbc.put("A", "VALUE (" + primaryKey + ",A)" );
                        secondaryMapAbc.put("B", "VALUE (" + primaryKey + ",B)" );
                        secondaryMapAbc.put("C", "VALUE (" + primaryKey + ",C)" );
                        secondaryMap = doubleMap.get(primaryKey);
                        Assert.assertEquals(id( mapLetter, primaryKey ), secondaryMapAbc, secondaryMap );
                        Assert.assertTrue(id( mapLetter, primaryKey ), keySet.contains(primaryKey));
                        Assert.assertTrue(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey));
                        Assert.assertTrue(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "A"));
                        Assert.assertTrue(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "B"));
                        Assert.assertTrue(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "C"));
                        keySet.remove( primaryKey );
                        secondaryMap = doubleMap.get(primaryKey);
                        Assert.assertNull( id( mapLetter, primaryKey), secondaryMap );
                        Assert.assertFalse(keySet.contains(primaryKey));
                        Assert.assertFalse(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey));
                        Assert.assertFalse(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "A"));
                        Assert.assertFalse(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "B"));
                        Assert.assertFalse(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "C"));
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testAW_keySet_primary_iterator_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<String> keySet = doubleMap.keySet();
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Map<String,String> secondaryMap;
                        Map<String,String> secondaryMapAbc = new HashMap<>();
                        secondaryMapAbc.put("A", "VALUE (" + primaryKey + ",A)" );
                        secondaryMapAbc.put("B", "VALUE (" + primaryKey + ",B)" );
                        secondaryMapAbc.put("C", "VALUE (" + primaryKey + ",C)" );
                        secondaryMap = doubleMap.get(primaryKey);
                        Assert.assertEquals(id( mapLetter, primaryKey ), secondaryMapAbc, secondaryMap );
                        Assert.assertTrue(id( mapLetter, primaryKey ), keySet.contains(primaryKey));
                        Assert.assertTrue(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey));
                        Assert.assertTrue(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "A"));
                        Assert.assertTrue(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "B"));
                        Assert.assertTrue(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "C"));
                        Iterator<String> iterator = keySet.iterator();
                        boolean found = false;
                        while (iterator.hasNext()) {
                           String loopEntry = iterator.next();
                           if ( primaryKey.equals(loopEntry)) {
                              iterator.remove();
                              found = true;
                           }
                        }
                        Assert.assertTrue(id( mapLetter, primaryKey ),found );
                        secondaryMap = doubleMap.get(primaryKey);
                        Assert.assertNull( id( mapLetter, primaryKey), secondaryMap );
                        Assert.assertFalse(id( mapLetter, primaryKey ), keySet.contains(primaryKey));
                        Assert.assertFalse(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey));
                        Assert.assertFalse(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "A"));
                        Assert.assertFalse(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "B"));
                        Assert.assertFalse(id( mapLetter, primaryKey ), doubleMap.containsKey(primaryKey, "C"));
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testAX_keySet_primary_add() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<String> keySet = doubleMap.keySet();
               keySet.add( "D" );
            }
         );
      //@formatter:on
   }

   @Test
   public void testAY_keySet_primary_map_put() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<String> keySet = doubleMap.keySet();
               DoubleMap.Entry<String,String,String> ddEntry = DoubleMap.entry("D","C","VALUE (D,C)");
               String value = null;
               Assert.assertFalse(id( mapLetter ), keySet.contains(ddEntry.getPrimaryKey()) );
               Assert.assertFalse(id( mapLetter ), doubleMap.containsKey(ddEntry.getPrimaryKey()));
               doubleMap.put(ddEntry);
               Assert.assertTrue(id( mapLetter ), keySet.contains(ddEntry.getPrimaryKey()) );
               value = doubleMap.get(ddEntry.getPrimaryKey(), ddEntry.getSecondaryKey());
               Assert.assertEquals(id( mapLetter ), ddEntry.getValue(), value);
            }
         );
      //@formatter:on
   }

   @Test
   public void testAZ_keySet_primary_map_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Set<String> keySet = doubleMap.keySet();
               DoubleMap.Entry<String,String,String> aaEntry = DoubleMap.entry("A","A","VALUE (A,A)");
               String value = null;
               Assert.assertTrue(id( mapLetter ), keySet.contains(aaEntry.getPrimaryKey()) );
               value = doubleMap.get(aaEntry.getPrimaryKey(), aaEntry.getSecondaryKey());
               Assert.assertEquals(id( mapLetter ), aaEntry.getValue(), value);
               doubleMap.remove(aaEntry.getPrimaryKey());
               Assert.assertFalse(id( mapLetter ), keySet.contains(aaEntry.getPrimaryKey()) );
               value = doubleMap.get(aaEntry.getPrimaryKey(), aaEntry.getSecondaryKey());
               Assert.assertNull(id( mapLetter ), value);
            }
         );
      //@formatter:on
   }

   @Test
   public void testBA_keySet_secondary_contains() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<String> keySet = doubleMap.keySet(primaryKey);
                        Set<String> keySetX = doubleMap.keySet(primaryKey);
                        Assert.assertTrue(id( mapLetter, primaryKey ), keySet == keySetX);
                        Assert.assertTrue(id( mapLetter, primaryKey ), keySet.contains( "A" ) );
                        Assert.assertTrue(id( mapLetter, primaryKey ), keySet.contains( "B" ) );
                        Assert.assertTrue(id( mapLetter, primaryKey ), keySet.contains( "C" ) );
                        Assert.assertFalse(id( mapLetter, primaryKey ), keySet.contains( "D" ) );
                     }
                  );
               Set<String> keySet = doubleMap.keySet("D");
               Assert.assertNull(id( mapLetter ), keySet);
            }
         );
      //@formatter:on
   }

   @Test
   public void testBB_keySet_secondary_set_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<String> keySet = doubleMap.keySet(primaryKey);
                        Map.Entry<String,String> aEntry = new AbstractMap.SimpleEntry<>("A", "VALUE (" + primaryKey + ",A)");
                        String value;
                        Assert.assertTrue(id( mapLetter, primaryKey ), keySet.contains( aEntry.getKey() ) );
                        value = doubleMap.get(primaryKey, aEntry.getKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), aEntry.getValue().equals( value ) );
                        keySet.remove(aEntry.getKey());
                        Assert.assertFalse(id( mapLetter, primaryKey ), keySet.contains( aEntry.getKey() ));
                        value = doubleMap.get(primaryKey, aEntry.getKey());
                        Assert.assertNull(id( mapLetter, primaryKey ), value );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBC_keySet_secondary_set_iterator_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<String> keySet = doubleMap.keySet(primaryKey);
                        Map.Entry<String,String> aEntry = new AbstractMap.SimpleEntry<>("A", "VALUE (" + primaryKey + ",A)");
                        String value;
                        Assert.assertTrue(id( mapLetter, primaryKey ), keySet.contains( aEntry.getKey() ) );
                        value = doubleMap.get(primaryKey, aEntry.getKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), aEntry.getValue().equals( value ) );
                        Iterator<String> iterator = keySet.iterator();
                        boolean found = false;
                        while (iterator.hasNext()) {
                           String loopEntry = iterator.next();
                           if (aEntry.getKey().equals(loopEntry)) {
                              iterator.remove();
                              found = true;
                           }
                        }
                        Assert.assertTrue(id( mapLetter, primaryKey ), found);
                        Assert.assertFalse(id( mapLetter, primaryKey ), keySet.contains( aEntry.getKey() ));
                        value = doubleMap.get(primaryKey, aEntry.getKey());
                        Assert.assertNull(id( mapLetter, primaryKey ), value );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testBD_keySet_secondary_add() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<String> keySet = doubleMap.keySet( primaryKey );
                        Map.Entry<String,String> dEntry = new AbstractMap.SimpleEntry<>("D", "VALUE (" + primaryKey + ",D)");
                        String value;
                        Assert.assertFalse(id( mapLetter, primaryKey ), keySet.contains( dEntry.getKey() ));
                        value = doubleMap.get(primaryKey, dEntry.getKey());
                        Assert.assertFalse(id( mapLetter, primaryKey ), dEntry.getValue().equals( value ) );
                        keySet.add(dEntry.getKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), keySet.contains( dEntry.getKey() ));
                        value = doubleMap.get(primaryKey, dEntry.getKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), dEntry.getValue().equals( value ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBE_keySet_secondary_map_put() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<String> keySet = doubleMap.keySet(primaryKey);
                        Map.Entry<String,String> dEntry = new AbstractMap.SimpleEntry<>("D", "VALUE (" + primaryKey + ",D)");
                        String value;
                        Assert.assertFalse(id( mapLetter, primaryKey ), keySet.contains( dEntry.getKey() ));
                        value = doubleMap.get(primaryKey, "D");
                        Assert.assertNull(id( mapLetter, primaryKey ), value );
                        doubleMap.put(primaryKey,dEntry.getKey(),dEntry.getValue());
                        Assert.assertTrue(id( mapLetter, primaryKey ), keySet.contains( dEntry.getKey() ));
                        value = doubleMap.get(primaryKey, dEntry.getKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), dEntry.getValue().equals( value ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBF_keySet_secondary_map_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Set<String> keySet = doubleMap.keySet(primaryKey);
                        Map.Entry<String,String> aEntry = new AbstractMap.SimpleEntry<>("A", "VALUE (" + primaryKey + ",A)");
                        String value;
                        Assert.assertTrue(id( mapLetter, primaryKey ), keySet.contains( aEntry.getKey() ));
                        value = doubleMap.get(primaryKey, aEntry.getKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), aEntry.getValue().equals( value ) );
                        doubleMap.remove(primaryKey,aEntry.getKey());
                        Assert.assertFalse(id( mapLetter, primaryKey ), keySet.contains( aEntry.getKey() ));
                        value = doubleMap.get(primaryKey, aEntry.getKey());
                        Assert.assertNull(id( mapLetter, primaryKey ), value );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBG_putIfAbsent_kp_ks_v() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        DoubleMap.Entry<String,String,String> paEntry = DoubleMap.entry(primaryKey,"A", "VALUE (" + primaryKey + ",A)");
                        DoubleMap.Entry<String,String,String> pdEntry = DoubleMap.entry(primaryKey,"D", "VALUE (" + primaryKey + ",D)");
                        String value;
                        Assert.assertTrue  ( id( mapLetter, primaryKey ), doubleMap.containsKey( paEntry.getPrimaryKey(), paEntry.getSecondaryKey() ) );
                        Assert.assertFalse ( id( mapLetter, primaryKey ), doubleMap.containsKey( pdEntry.getPrimaryKey(), pdEntry.getSecondaryKey() ) );
                        value = doubleMap.putIfAbsent(paEntry.getPrimaryKey(),paEntry.getSecondaryKey(),"X");
                        Assert.assertTrue(id( mapLetter, primaryKey ), paEntry.getValue().equals( value ) );
                        value = doubleMap.get(paEntry.getPrimaryKey(),paEntry.getSecondaryKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), paEntry.getValue().equals( value ) );
                        value = doubleMap.putIfAbsent(pdEntry.getPrimaryKey(),pdEntry.getSecondaryKey(),pdEntry.getValue());
                        Assert.assertNull(id( mapLetter, primaryKey ), value );
                        value = doubleMap.get(pdEntry.getPrimaryKey(),pdEntry.getSecondaryKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), pdEntry.getValue().equals( value ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBH_putIfAbsent_entry() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        DoubleMap.Entry<String,String,String> paEntry  = DoubleMap.entry( primaryKey,"A", "VALUE (" + primaryKey + ",A)" );
                        DoubleMap.Entry<String,String,String> paEntryX = DoubleMap.entry( primaryKey,"A", "X" );
                        DoubleMap.Entry<String,String,String> pdEntry  = DoubleMap.entry( primaryKey,"D", "VALUE (" + primaryKey + ",D)" );
                        String value;
                        Assert.assertTrue  ( id( mapLetter, primaryKey ), doubleMap.containsKey( paEntry.getPrimaryKey(), paEntry.getSecondaryKey() ) );
                        Assert.assertFalse ( id( mapLetter, primaryKey ), doubleMap.containsKey( pdEntry.getPrimaryKey(), pdEntry.getSecondaryKey() ) );
                        value = doubleMap.putIfAbsent(paEntryX);
                        Assert.assertTrue(id( mapLetter, primaryKey ), paEntry.getValue().equals( value ) );
                        value = doubleMap.get(paEntry.getPrimaryKey(),paEntry.getSecondaryKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), paEntry.getValue().equals( value ) );
                        value = doubleMap.putIfAbsent(pdEntry);
                        Assert.assertNull(id( mapLetter, primaryKey ), value );
                        value = doubleMap.get(pdEntry.getPrimaryKey(),pdEntry.getSecondaryKey());
                        Assert.assertTrue(id( mapLetter, primaryKey ), pdEntry.getValue().equals( value ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBI_putIfAbsent_kp_secondary_map() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Map<String,String> secondaryMapAbc = new HashMap<>();
                        secondaryMapAbc.put("A", "VALUE (" + primaryKey + ",A)" );
                        secondaryMapAbc.put("B", "VALUE (" + primaryKey + ",B)" );
                        secondaryMapAbc.put("C", "VALUE (" + primaryKey + ",C)" );
                        Map<String,String> secondaryMapXyz = new HashMap<>();
                        secondaryMapXyz.put("X", "VALUE (" + primaryKey + ",X)" );
                        secondaryMapXyz.put("Y", "VALUE (" + primaryKey + ",Y)" );
                        secondaryMapXyz.put("Z", "VALUE (" + primaryKey + ",Z)" );
                        Map<String,String> value = null;
                        Assert.assertTrue  ( id( mapLetter, primaryKey ), doubleMap.containsKey( primaryKey, "A" ) );
                        Assert.assertTrue  ( id( mapLetter, primaryKey ), doubleMap.containsKey( primaryKey, "B" ) );
                        Assert.assertTrue  ( id( mapLetter, primaryKey ), doubleMap.containsKey( primaryKey, "C" ) );
                        Assert.assertFalse ( id( mapLetter, primaryKey ), doubleMap.containsKey( primaryKey, "X" ) );
                        Assert.assertFalse ( id( mapLetter, primaryKey ), doubleMap.containsKey( primaryKey, "Y" ) );
                        Assert.assertFalse ( id( mapLetter, primaryKey ), doubleMap.containsKey( primaryKey, "Z" ) );
                        value = doubleMap.putIfAbsent( primaryKey, secondaryMapAbc );
                        Assert.assertTrue(id( mapLetter, primaryKey ), secondaryMapAbc.equals( value ) );
                        value = doubleMap.get( primaryKey );
                        Assert.assertTrue(id( mapLetter, primaryKey ), secondaryMapAbc.equals( value ) );
                        value = doubleMap.putIfAbsent( primaryKey + "-XYZ", secondaryMapXyz );
                        Assert.assertNull(id( mapLetter, primaryKey ), value );
                        value = doubleMap.get( primaryKey + "-XYZ" );
                        Assert.assertTrue(id( mapLetter, primaryKey ), secondaryMapXyz.equals( value ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBJ_remove_kp() {
      //@formatter:off
      forEachMutableTestMap
      (
         ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
         {
            forEachTestKey
               (
                  ( primaryKey ) ->
                  {
                     Map<String,String> secondaryMapGet = doubleMap.get( primaryKey );
                     Assert.assertNotNull( id( mapLetter, primaryKey ), secondaryMapGet );
                     Assert.assertEquals( id( mapLetter, primaryKey ), 3, secondaryMapGet.size() );
                     Map<String,String> secondaryMapRemove = doubleMap.remove( primaryKey );
                     Assert.assertNotNull( id( mapLetter, primaryKey ), secondaryMapGet );
                     Assert.assertEquals( id( mapLetter, primaryKey ), secondaryMapGet, secondaryMapRemove );
                     Map<String,String> secondaryMapNull = doubleMap.remove( primaryKey );
                     Assert.assertNull( id( mapLetter, primaryKey ), secondaryMapNull );
                  }
               );
            Assert.assertEquals(0, doubleMap.size());
         }
      );
   }

   @Test
   public void testBK_remove_kp_ks() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        forEachTestKey
                           (
                              ( secondaryKey ) ->
                              {
                                 String expectedValue = "VALUE (" + primaryKey + "," + secondaryKey + ")";
                                 String valueGet = doubleMap.get( primaryKey, secondaryKey);
                                 Assert.assertEquals( id( mapLetter, primaryKey, secondaryKey ), expectedValue, valueGet );
                                 String valueRemove = doubleMap.remove( primaryKey,secondaryKey );
                                 Assert.assertEquals( id( mapLetter, primaryKey, secondaryKey ), expectedValue, valueRemove );
                                 String valueNull = doubleMap.get( primaryKey, secondaryKey);
                                 Assert.assertNull( id( mapLetter, primaryKey, secondaryKey ), valueNull );
                              }
                           );
                        Assert.assertFalse(doubleMap.containsKey(primaryKey));
                     }
                  );
               Assert.assertEquals(0, doubleMap.size());
            }
         );
      //@formatter:on
   }

   @Test
   public void testBL_size_total() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Assert.assertEquals( id( mapLetter ), 9, doubleMap.size() );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBM_size_secondary() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Assert.assertEquals( id( mapLetter, primaryKey ), 3, doubleMap.size( primaryKey ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBN_values_primary() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Collection<String> values = doubleMap.values();
               Collection<String> valuesX = doubleMap.values();
               Assert.assertTrue  ( id(mapLetter), values == valuesX);
               Assert.assertTrue  ( id(mapLetter), values.contains("VALUE (A,A)"));
               Assert.assertTrue  ( id(mapLetter), values.contains("VALUE (A,B)"));
               Assert.assertTrue  ( id(mapLetter), values.contains("VALUE (A,C)"));
               Assert.assertFalse ( id(mapLetter), values.contains("VALUE (A,D)"));
               Assert.assertTrue  ( id(mapLetter), values.contains("VALUE (B,A)"));
               Assert.assertTrue  ( id(mapLetter), values.contains("VALUE (B,B)"));
               Assert.assertTrue  ( id(mapLetter), values.contains("VALUE (B,C)"));
               Assert.assertFalse ( id(mapLetter), values.contains("VALUE (B,D)"));
               Assert.assertTrue  ( id(mapLetter), values.contains("VALUE (C,A)"));
               Assert.assertTrue  ( id(mapLetter), values.contains("VALUE (C,B)"));
               Assert.assertTrue  ( id(mapLetter), values.contains("VALUE (C,C)"));
               Assert.assertFalse ( id(mapLetter), values.contains("VALUE (C,D)"));
               Assert.assertFalse ( id(mapLetter), values.contains("VALUE (D,A)"));
            }
         );
      //@formatter:on
   }

   @Test
   public void testBO_values_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Collection<String> values = doubleMap.values();
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        forEachTestKey
                           (
                              ( secondaryKey ) ->
                              {
                                 String value = doubleMap.get(primaryKey, secondaryKey);
                                 Assert.assertTrue( id( mapLetter, primaryKey, secondaryKey ), values.contains( value ) );
                                 values.remove( value );
                                 Assert.assertFalse( id( mapLetter, primaryKey, secondaryKey ), values.contains( value ) );
                                 value = doubleMap.get(primaryKey, secondaryKey);
                                 Assert.assertNull( id( mapLetter, primaryKey, secondaryKey ), value );
                              }
                           );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBP_values_primary_iterator_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Collection<String> values = doubleMap.values();
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        forEachTestKey
                           (
                              ( secondaryKey ) ->
                              {
                                 String value = doubleMap.get(primaryKey, secondaryKey);
                                 Assert.assertTrue( id( mapLetter, primaryKey, secondaryKey ), values.contains( value ) );
                                 Iterator<String> iterator = values.iterator();
                                 boolean found = false;
                                 while (iterator.hasNext()) {
                                    String loopEntry = iterator.next();
                                    if ( value.equals(loopEntry)) {
                                       iterator.remove();
                                       found = true;
                                    }
                                 }
                                 Assert.assertTrue(id( mapLetter, primaryKey, secondaryKey ),found );
                                 Assert.assertFalse( id( mapLetter, primaryKey, secondaryKey ), values.contains( value ) );
                                 value = doubleMap.get(primaryKey, secondaryKey);
                                 Assert.assertNull( id( mapLetter, primaryKey, secondaryKey ), value );
                              }
                           );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testBQ_values_primary_add() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Collection<String> values = doubleMap.values();
               values.add( "D" );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBR_values_primary_map_put() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Collection<String> values = doubleMap.values();
               DoubleMap.Entry<String,String,String> ddEntry = DoubleMap.entry("D","C","VALUE (D,C)");
               String value = null;
               Assert.assertFalse(id( mapLetter ), values.contains( ddEntry.getValue() ) );
               Assert.assertFalse(id( mapLetter ), doubleMap.containsKey( ddEntry.getPrimaryKey() ) );
               doubleMap.put( ddEntry );
               Assert.assertTrue( id( mapLetter ), values.contains( ddEntry.getValue() ) );
               value = doubleMap.get( ddEntry.getPrimaryKey(), ddEntry.getSecondaryKey() );
               Assert.assertEquals( id( mapLetter ), ddEntry.getValue(), value );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBS_values_primary_map_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               Collection<String> values = doubleMap.values();
               DoubleMap.Entry<String,String,String> aaEntry = DoubleMap.entry("A","A","VALUE (A,A)");
               String value = null;
               Assert.assertTrue( id( mapLetter ), values.contains( aaEntry.getValue() ) );
               value = doubleMap.get( aaEntry.getPrimaryKey(), aaEntry.getSecondaryKey() );
               Assert.assertEquals( id( mapLetter ), aaEntry.getValue(), value );
               doubleMap.remove( aaEntry.getPrimaryKey() );
               Assert.assertFalse( id( mapLetter ), values.contains( "VALUE (A,A)" ) );
               Assert.assertFalse( id( mapLetter ), values.contains( "VALUE (A,B)" ) );
               Assert.assertFalse( id( mapLetter ), values.contains( "VALUE (A,C)" ) );
               value = doubleMap.get( aaEntry.getPrimaryKey(), aaEntry.getSecondaryKey() );
               Assert.assertNull( id( mapLetter ), value );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBT_values_secondary_contains() {
      //@formatter:off
      forEachTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Collection<String> values  = doubleMap.values( primaryKey );
                        Collection<String> valuesX = doubleMap.values( primaryKey );
                        Assert.assertTrue  ( id( mapLetter, primaryKey ), values == valuesX );
                        Assert.assertTrue  ( id( mapLetter, primaryKey ), values.contains( "VALUE (" + primaryKey + ",A)" ) );
                        Assert.assertTrue  ( id( mapLetter, primaryKey ), values.contains( "VALUE (" + primaryKey + ",B)" ) );
                        Assert.assertTrue  ( id( mapLetter, primaryKey ), values.contains( "VALUE (" + primaryKey + ",C)" ) );
                        Assert.assertFalse ( id( mapLetter, primaryKey ), values.contains( "VALUE (" + primaryKey + ",D)" ) );
                     }
                  );
               Collection<String> values = doubleMap.values("D");
               Assert.assertNull( id( mapLetter ), values );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBU_values_secondary_set_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Collection<String> values = doubleMap.values( primaryKey );
                        Map.Entry<String,String> aEntry = new AbstractMap.SimpleEntry<>("A", "VALUE (" + primaryKey + ",A)");
                        String value;
                        Assert.assertTrue( id( mapLetter, primaryKey ), values.contains( aEntry.getValue() ) );
                        value = doubleMap.get( primaryKey, aEntry.getKey());
                        Assert.assertTrue( id( mapLetter, primaryKey ), aEntry.getValue().equals( value ) );
                        values.remove( aEntry.getValue() );
                        Assert.assertFalse( id( mapLetter, primaryKey ), values.contains( aEntry.getValue() ) );
                        value = doubleMap.get( primaryKey, aEntry.getKey() );
                        Assert.assertNull( id( mapLetter, primaryKey ), value );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBV_values_secondary_set_iterator_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Collection<String> values = doubleMap.values( primaryKey );
                        Map.Entry<String,String> aEntry = new AbstractMap.SimpleEntry<>("A", "VALUE (" + primaryKey + ",A)");
                        String value;
                        Assert.assertTrue( id( mapLetter, primaryKey ), values.contains( aEntry.getValue() ) );
                        value = doubleMap.get( primaryKey, aEntry.getKey());
                        Assert.assertTrue( id( mapLetter, primaryKey ), aEntry.getValue().equals( value ) );
                        Iterator<String> iterator = values.iterator();
                        boolean found = false;
                        while (iterator.hasNext()) {
                           String loopEntry = iterator.next();
                           if (aEntry.getValue().equals(loopEntry)) {
                              iterator.remove();
                              found = true;
                           }
                        }
                        Assert.assertTrue( id( mapLetter, primaryKey ), found );
                        Assert.assertFalse( id( mapLetter, primaryKey ), values.contains( aEntry.getValue() ) );
                        value = doubleMap.get( primaryKey, aEntry.getKey() );
                        Assert.assertNull( id( mapLetter, primaryKey ), value );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testBW_values_secondary_add() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Collection<String> values = doubleMap.values(primaryKey);
                        Map.Entry<String,String> dEntry = new AbstractMap.SimpleEntry<>("D", "VALUE (" + primaryKey + ",D)");
                        String value;
                        Assert.assertFalse( id( mapLetter, primaryKey ), values.contains( dEntry.getValue() ) );
                        value = doubleMap.get( primaryKey, dEntry.getKey() );
                        Assert.assertFalse( id( mapLetter, primaryKey ), dEntry.getValue().equals( value ) );
                        values.add( dEntry.getValue() );
                        Assert.assertTrue(id( mapLetter, primaryKey ), values.contains( dEntry.getValue() ) );
                        value = doubleMap.get( primaryKey, dEntry.getKey() );
                        Assert.assertTrue( id( mapLetter, primaryKey ), dEntry.getValue().equals( value ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBX_values_secondary_map_put() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Collection<String> values = doubleMap.values( primaryKey );
                        Map.Entry<String,String> dEntry = new AbstractMap.SimpleEntry<>( "D", "VALUE (" + primaryKey + ",D)" );
                        String value;
                        Assert.assertFalse(id( mapLetter, primaryKey ), values.contains( dEntry.getValue() ) );
                        value = doubleMap.get( primaryKey, "D" );
                        Assert.assertNull( id( mapLetter, primaryKey ), value );
                        doubleMap.put( primaryKey, dEntry.getKey(), dEntry.getValue() );
                        Assert.assertTrue( id( mapLetter, primaryKey ), values.contains( dEntry.getValue() ) );
                        value = doubleMap.get( primaryKey, dEntry.getKey() );
                        Assert.assertTrue( id( mapLetter, primaryKey ), dEntry.getValue().equals( value ) );
                     }
                  );
            }
         );
      //@formatter:on
   }

   @Test
   public void testBY_values_secondary_map_remove() {
      //@formatter:off
      forEachMutableTestMap
         (
            ( String mapLetter, DoubleMap<String,String,String> doubleMap ) ->
            {
               forEachTestKey
                  (
                     ( primaryKey ) ->
                     {
                        Collection<String> values = doubleMap.values( primaryKey );
                        Map.Entry<String,String> aEntry = new AbstractMap.SimpleEntry<>( "A", "VALUE (" + primaryKey + ",A)" );
                        String value;
                        Assert.assertTrue( id( mapLetter, primaryKey ), values.contains( aEntry.getValue() ));
                        value = doubleMap.get( primaryKey, aEntry.getKey() );
                        Assert.assertTrue( id( mapLetter, primaryKey ), aEntry.getValue().equals( value ) );
                        doubleMap.remove( primaryKey, aEntry.getKey() );
                        Assert.assertFalse( id( mapLetter, primaryKey ), values.contains( aEntry.getValue() ) );
                        value = doubleMap.get( primaryKey, aEntry.getKey() );
                        Assert.assertNull( id( mapLetter, primaryKey ), value );
                     }
                  );
            }
         );
      //@formatter:on
   }

}

/* EOF */
