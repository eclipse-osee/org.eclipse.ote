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
 *     Boeing - Initial API and implementation
 **********************************************************************/

package org.eclipse.ote.tools.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.eclipse.ote.tools.util.function.TriConsumer;
import org.eclipse.ote.tools.util.function.TriFunction;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the Enumeration Functional Interface Map classes.
 *
 * @author Loren K. Ashley
 */

public class EnumFunctionalInterafaceMapsTest {

   private static enum TestEnum {
      A,
      B,
      C,
      D,
      E,
      F;
   }

   /*
    * AbstractEnumFunctionInterfaceMap Tests
    */

   @Test
   public void testContainsKey() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);
      map.put(TestEnum.B, TestEnum.B::name);

      Assert.assertTrue(map.containsKey(TestEnum.A));
      Assert.assertTrue(map.containsKey(TestEnum.B));
      Assert.assertFalse(map.containsKey(TestEnum.C));
      Assert.assertFalse(map.containsKey(TestEnum.D));
      Assert.assertFalse(map.containsKey(TestEnum.E));
      Assert.assertFalse(map.containsKey(TestEnum.F));
   }

   @Test(expected = NullPointerException.class)
   public void testContainsKeyNull() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.containsKey((TestEnum) null);
   }

   @Test
   public void testGetFunction() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);

      Optional<Supplier<String>> functionalInterfaceOptional = map.getFunction(TestEnum.A);

      Assert.assertTrue(functionalInterfaceOptional.isPresent());

      Supplier<String> functionalInterface = functionalInterfaceOptional.get();

      Assert.assertEquals("A", functionalInterface.get());

      functionalInterfaceOptional = map.getFunction(TestEnum.B);

      Assert.assertFalse(functionalInterfaceOptional.isPresent());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testGetFunctionNull() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      Optional<Supplier<String>> functionalInterfaceOptional = map.getFunction((TestEnum) null);
   }

   @Test
   public void testIsEmpty() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      Assert.assertTrue(map.isEmpty());

      map.put(TestEnum.A, TestEnum.A::name);

      Assert.assertFalse(map.isEmpty());
   }

   @Test
   public void testKeySet() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);
      map.put(TestEnum.B, TestEnum.B::name);

      Set<TestEnum> keySet = map.keySet();

      Assert.assertEquals(2, keySet.size());

      Assert.assertTrue(keySet.contains(TestEnum.A));
      Assert.assertTrue(keySet.contains(TestEnum.B));
      Assert.assertFalse(keySet.contains(TestEnum.C));
      Assert.assertFalse(keySet.contains(TestEnum.D));
      Assert.assertFalse(keySet.contains(TestEnum.E));
      Assert.assertFalse(keySet.contains(TestEnum.F));

      Iterator<TestEnum> keySetIterator = keySet.iterator();

      Assert.assertTrue(keySetIterator.hasNext());

      TestEnum firstKey = keySetIterator.next();

      Assert.assertTrue(TestEnum.A.equals(firstKey) || TestEnum.B.equals(firstKey));

      Assert.assertTrue(keySetIterator.hasNext());

      TestEnum secondKey = keySetIterator.next();

      Assert.assertTrue(TestEnum.A.equals(secondKey) || TestEnum.B.equals(secondKey));

      Assert.assertNotEquals(firstKey, secondKey);

      Assert.assertFalse(keySetIterator.hasNext());
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetAdd() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      Set<TestEnum> keySet = map.keySet();

      keySet.add(TestEnum.A);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetAddAll() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      Set<TestEnum> keySet = map.keySet();

      Set<TestEnum> set = new HashSet<TestEnum>();
      set.add(TestEnum.A);
      set.add(TestEnum.B);

      keySet.addAll(set);
   }

   /*
    * Set must not be empty for the set's iterator to be called. Exception won't be thrown unless the set's iterator is
    * called.
    */

   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetRemove() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);

      Set<TestEnum> keySet = map.keySet();

      keySet.remove(TestEnum.A);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetRemoveAll() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);

      Set<TestEnum> keySet = map.keySet();

      Set<TestEnum> set = new HashSet<TestEnum>();
      set.add(TestEnum.A);
      set.add(TestEnum.B);

      keySet.removeAll(set);
   }

   /*
    * RetainAll won't call the set's iterator remove function unless the set contains an item to be removed. The call
    * won't throw an exception unless it attempts a remove.
    */
   @Test(expected = UnsupportedOperationException.class)
   public void testKeySetRetainAll() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);
      map.put(TestEnum.C, TestEnum.C::name);

      Set<TestEnum> keySet = map.keySet();

      Set<TestEnum> set = new HashSet<TestEnum>();
      set.add(TestEnum.A);
      set.add(TestEnum.B);

      keySet.retainAll(set);
   }

   @Test
   public void testOfEntriesLoader() {

      //@formatter:off
      class MapX extends AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> {

         MapX(Class<TestEnum> enumerationKeyClass) {
            super(enumerationKeyClass);
         }

        @SafeVarargs
         @SuppressWarnings( "varargs" )
         public final AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>
            ofEntriesLoaderX( Map.Entry<TestEnum, Supplier<String>>... entries )
         {
            return super.ofEntriesLoader(entries);
         }
      }

      MapX map = new MapX(TestEnum.class);

      map.ofEntriesLoaderX
         (
            new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.A, TestEnum.A::name),
            new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.B, TestEnum.B::name)
         );
      //@formatter:on

      Assert.assertTrue(map.containsKey(TestEnum.A));

      Optional<Supplier<String>> functionOptional = map.getFunction(TestEnum.A);

      Assert.assertTrue(functionOptional.isPresent());

      Supplier<String> function = functionOptional.get();

      Assert.assertEquals("A", function.get());

      functionOptional = map.getFunction(TestEnum.B);

      Assert.assertTrue(functionOptional.isPresent());

      function = functionOptional.get();

      Assert.assertEquals("B", function.get());

      Assert.assertFalse(map.containsKey(TestEnum.C));
      Assert.assertFalse(map.containsKey(TestEnum.D));
      Assert.assertFalse(map.containsKey(TestEnum.E));
      Assert.assertFalse(map.containsKey(TestEnum.F));

   }

   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testOfEntriesLoaderDuplicateEntry() {

      //@formatter:off
      class MapX extends AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> {

         MapX(Class<TestEnum> enumerationKeyClass) {
            super(enumerationKeyClass);
         }

        @SafeVarargs
         @SuppressWarnings( "varargs" )
         public final AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>
            ofEntriesLoaderX( Map.Entry<TestEnum, Supplier<String>>... entries )
         {
            return super.ofEntriesLoader(entries);
         }
      }

      MapX map = new MapX(TestEnum.class);

      map.ofEntriesLoaderX
         (
            new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.A, TestEnum.A::name),
            new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.A, TestEnum.A::name)
         );
      //@formatter:on
   }

   @Test(expected = NullPointerException.class)
   public void testOfEntriesLoaderNullArray() {

      //@formatter:off
      class MapX extends AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> {

         MapX(Class<TestEnum> enumerationKeyClass) {
            super(enumerationKeyClass);
         }

        @SafeVarargs
         @SuppressWarnings( "varargs" )
         public final AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>
            ofEntriesLoaderX( Map.Entry<TestEnum, Supplier<String>>... entries )
         {
            return super.ofEntriesLoader(entries);
         }
      }

      MapX map = new MapX(TestEnum.class);

      map.ofEntriesLoaderX
         (
            (Map.Entry<TestEnum,Supplier<String>>[]) null
         );
      //@formatter:on
   }

   @Test(expected = NullPointerException.class)
   public void testOfEntriesLoaderNullEntry() {

      //@formatter:off
      class MapX extends AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> {

         MapX(Class<TestEnum> enumerationKeyClass) {
            super(enumerationKeyClass);
         }

        @SafeVarargs
         @SuppressWarnings( "varargs" )
         public final AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>
            ofEntriesLoaderX( Map.Entry<TestEnum, Supplier<String>>... entries )
         {
            return super.ofEntriesLoader(entries);
         }
      }

      MapX map = new MapX(TestEnum.class);

      map.ofEntriesLoaderX
         (
            (Map.Entry<TestEnum,Supplier<String>>) null
         );
      //@formatter:on
   }

   @Test(expected = NullPointerException.class)
   public void testOfEntriesLoaderOneIsNull() {

      //@formatter:off
      class MapX extends AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> {

         MapX(Class<TestEnum> enumerationKeyClass) {
            super(enumerationKeyClass);
         }

        @SafeVarargs
         @SuppressWarnings( "varargs" )
         public final AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>
            ofEntriesLoaderX( Map.Entry<TestEnum, Supplier<String>>... entries )
         {
            return super.ofEntriesLoader(entries);
         }
      }

      MapX map = new MapX(TestEnum.class);

      map.ofEntriesLoaderX
         (
            new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.A, TestEnum.A::name),
            (Map.Entry<TestEnum,Supplier<String>>) null,
            new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.B, TestEnum.B::name)
         );
      //@formatter:on
   }

   @Test
   public void testPut() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);

      Assert.assertTrue(map.containsKey(TestEnum.A));

      Optional<Supplier<String>> functionOptional = map.getFunction(TestEnum.A);

      Assert.assertTrue(functionOptional.isPresent());

      Supplier<String> function = functionOptional.get();

      Assert.assertEquals("A", function.get());

      Assert.assertFalse(map.containsKey(TestEnum.B));
      Assert.assertFalse(map.containsKey(TestEnum.C));
      Assert.assertFalse(map.containsKey(TestEnum.D));
      Assert.assertFalse(map.containsKey(TestEnum.E));
      Assert.assertFalse(map.containsKey(TestEnum.F));
   }

   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testPutDuplicate() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      map.put(TestEnum.A, TestEnum.A::name);
      map.put(TestEnum.A, TestEnum.A::name);
   }

   @Test
   public void testSize() {

      AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>> map =
         new AbstractEnumFunctionalInterfaceMap<TestEnum, Supplier<String>>(TestEnum.class);

      Assert.assertEquals(0, map.size());

      map.put(TestEnum.A, TestEnum.A::name);

      Assert.assertEquals(1, map.size());

      map.put(TestEnum.B, TestEnum.B::name);

      Assert.assertEquals(2, map.size());

      map.put(TestEnum.C, TestEnum.C::name);

      Assert.assertEquals(3, map.size());

      map.put(TestEnum.D, TestEnum.D::name);

      Assert.assertEquals(4, map.size());

      map.put(TestEnum.E, TestEnum.E::name);

      Assert.assertEquals(5, map.size());

      map.put(TestEnum.F, TestEnum.F::name);

      Assert.assertEquals(6, map.size());
   }

   /*
    * EnumTriConsumerMap Tests
    */

   @Test
   public void testEnumTriConsumerMapAccept() {
      EnumTriConsumerMap<TestEnum, String, String, String> map =
         new EnumTriConsumerMap<TestEnum, String, String, String>(TestEnum.class);
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();
      ArrayList<String> listC = new ArrayList<String>();

      map.put(TestEnum.A, (a, b, c) -> {
         listA.add(a);
         listB.add(b);
         listC.add(c);
      });

      map.accept(TestEnum.A, "A", "B", "C");

      Assert.assertEquals(1, listA.size());
      Assert.assertEquals("A", listA.get(0));

      Assert.assertEquals(1, listB.size());
      Assert.assertEquals("B", listB.get(0));

      Assert.assertEquals(1, listC.size());
      Assert.assertEquals("C", listC.get(0));

   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumTriConsumerMapAcceptNoSuchElement() {

      EnumTriConsumerMap<TestEnum, String, String, String> map =
         new EnumTriConsumerMap<TestEnum, String, String, String>(TestEnum.class);

      map.accept(TestEnum.A, "A", "B", "C");
   }

   @Test(expected = NullPointerException.class)
   public void testEnumTriConsumerMapAcceptNullKey() {

      EnumTriConsumerMap<TestEnum, String, String, String> map =
         new EnumTriConsumerMap<TestEnum, String, String, String>(TestEnum.class);

      map.accept((TestEnum) null, "A", "B", "C");
   }

   @Test
   public void testEnumTriConsumerMapOfEntries() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();
      ArrayList<String> listC = new ArrayList<String>();

      //@formatter:off
      EnumTriConsumerMap<TestEnum,String,String,String> map =
         EnumTriConsumerMap.<TestEnum,String,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriConsumer<String,String,String>>(TestEnum.A, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); } ),
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriConsumer<String,String,String>>(TestEnum.B, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); } ),
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriConsumer<String,String,String>>(TestEnum.C, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); } )
            );
      //@formatter:on

      map.accept(TestEnum.A, "A0", "A1", "A2");
      map.accept(TestEnum.B, "B0", "B1", "B2");
      map.accept(TestEnum.C, "C0", "C1", "C2");

      Assert.assertEquals(3, listA.size());
      Assert.assertEquals(3, listB.size());
      Assert.assertEquals(3, listC.size());

      Assert.assertEquals("A0", listA.get(0));
      Assert.assertEquals("A1", listB.get(0));
      Assert.assertEquals("A2", listC.get(0));

      Assert.assertEquals("B0", listA.get(1));
      Assert.assertEquals("B1", listB.get(1));
      Assert.assertEquals("B2", listC.get(1));

      Assert.assertEquals("C0", listA.get(2));
      Assert.assertEquals("C1", listB.get(2));
      Assert.assertEquals("C2", listC.get(2));

   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumTriConsumerMapOfEntriesDuplicateEntry() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();
      ArrayList<String> listC = new ArrayList<String>();

      //@formatter:off
      EnumTriConsumerMap<TestEnum,String,String,String> map =
         EnumTriConsumerMap.<TestEnum,String,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriConsumer<String,String,String>>(TestEnum.A, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); }),
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriConsumer<String,String,String>>(TestEnum.A, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); }),
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriConsumer<String,String,String>>(TestEnum.C, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); })
            );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumTriConsumerMapOfEntriesPut() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();
      ArrayList<String> listC = new ArrayList<String>();

      //@formatter:off
      EnumTriConsumerMap<TestEnum,String,String,String> map =
         EnumTriConsumerMap.<TestEnum,String,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriConsumer<String,String,String>>(TestEnum.A, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); }),
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriConsumer<String,String,String>>(TestEnum.B, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); }),
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriConsumer<String,String,String>>(TestEnum.C, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); })
            );
      //@formatter:on

      map.put(TestEnum.D, (a, b, c) -> {
         listA.add(a);
         listB.add(b);
         listC.add(c);
      });
   }

   @Test
   public void testEnumTriConsumerMapOfEntriesEmpty() {

      //@formatter:off
      EnumTriConsumerMap<TestEnum,String,String,String> map = EnumTriConsumerMap.<TestEnum,String,String,String>ofEntries
      (
         TestEnum.class
      );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumTriConsumerMapOfEntriesNull() {

      //@formatter:off
      EnumTriConsumerMap<TestEnum,String,String,String> map = EnumTriConsumerMap.<TestEnum,String,String,String>ofEntries
      (
         TestEnum.class,
         (Map.Entry<TestEnum,TriConsumer<String,String,String>>) null
      );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumTriConsumerMapOfEntriesOneIsNull() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();
      ArrayList<String> listC = new ArrayList<String>();

      //@formatter:off
      EnumTriConsumerMap<TestEnum,String,String,String> map =
         EnumTriConsumerMap.<TestEnum,String,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriConsumer<String,String,String>>(TestEnum.A, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); }),
               (Map.Entry<TestEnum,TriConsumer<String,String,String>>) null,
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriConsumer<String,String,String>>(TestEnum.C, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); })
            );
      //@formatter:on
   }

   /*
    * EnumTriFunctionMap Tests
    */

   @Test
   public void testEnumTriFunctionMapApply() {
      EnumTriFunctionMap<TestEnum, String, String, String, String> map =
         new EnumTriFunctionMap<TestEnum, String, String, String, String>(TestEnum.class);
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();
      ArrayList<String> listC = new ArrayList<String>();

      map.put(TestEnum.A, (a, b, c) -> {
         listA.add(a);
         listB.add(b);
         listC.add(c);
         return a.concat(b).concat(c);
      });

      String result = map.apply(TestEnum.A, "A", "B", "C");

      Assert.assertEquals(1, listA.size());
      Assert.assertEquals("A", listA.get(0));

      Assert.assertEquals(1, listB.size());
      Assert.assertEquals("B", listB.get(0));

      Assert.assertEquals(1, listC.size());
      Assert.assertEquals("C", listC.get(0));

      Assert.assertEquals("ABC", result);
   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumTriFunctionMapAcceptNoSuchElement() {

      EnumTriFunctionMap<TestEnum, String, String, String, String> map =
         new EnumTriFunctionMap<TestEnum, String, String, String, String>(TestEnum.class);

      map.apply(TestEnum.A, "A", "B", "C");
   }

   @Test(expected = NullPointerException.class)
   public void testEnumTriFunctionMapAcceptNullKey() {

      EnumTriFunctionMap<TestEnum, String, String, String, String> map =
         new EnumTriFunctionMap<TestEnum, String, String, String, String>(TestEnum.class);

      map.apply((TestEnum) null, "A", "B", "C");
   }

   @Test
   public void testEnumTriFunctionMapOfEntries() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();
      ArrayList<String> listC = new ArrayList<String>();

      //@formatter:off
      EnumTriFunctionMap<TestEnum,String,String,String,String> map =
         EnumTriFunctionMap.<TestEnum,String,String,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriFunction<String,String,String,String>>( TestEnum.A, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); return a.concat(b).concat(c); } ),
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriFunction<String,String,String,String>>( TestEnum.B, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); return a.concat(b).concat(c); } ),
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriFunction<String,String,String,String>>( TestEnum.C, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); return a.concat(b).concat(c); } )
            );
      //@formatter:on

      String resultA = map.apply(TestEnum.A, "A", "A1", "A2");
      String resultB = map.apply(TestEnum.B, "B", "B1", "B2");
      String resultC = map.apply(TestEnum.C, "C", "C1", "C2");

      Assert.assertEquals(3, listA.size());
      Assert.assertEquals(3, listB.size());
      Assert.assertEquals(3, listC.size());

      Assert.assertEquals("A", listA.get(0));
      Assert.assertEquals("A1", listB.get(0));
      Assert.assertEquals("A2", listC.get(0));

      Assert.assertEquals("B", listA.get(1));
      Assert.assertEquals("B1", listB.get(1));
      Assert.assertEquals("B2", listC.get(1));

      Assert.assertEquals("AA1A2", resultA);
      Assert.assertEquals("BB1B2", resultB);
      Assert.assertEquals("CC1C2", resultC);
   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumTriFunctionMapOfEntriesDuplicateEntry() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();

      //@formatter:off
      EnumTriFunctionMap<TestEnum,String,String,String,String> map =
         EnumTriFunctionMap.<TestEnum,String,String,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriFunction<String,String,String,String>>(TestEnum.A, (a, b, c) -> { listA.add(a); listB.add(b); return a.concat(b); }),
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriFunction<String,String,String,String>>(TestEnum.A, (a, b, c) -> { listA.add(a); listB.add(b); return a.concat(b); })
            );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumTriFunctionMapOfEntriesPut() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();
      ArrayList<String> listC = new ArrayList<String>();

      //@formatter:off
      EnumTriFunctionMap<TestEnum,String,String,String,String> map =
         EnumTriFunctionMap.<TestEnum,String,String,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriFunction<String,String,String,String>>( TestEnum.A, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); return a.concat(b).concat(c); } ),
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriFunction<String,String,String,String>>( TestEnum.B, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); return a.concat(b).concat(c); } ),
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriFunction<String,String,String,String>>( TestEnum.C, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); return a.concat(b).concat(c); } )
            );
      //@formatter:on

      map.put(TestEnum.D, (a, b, c) -> {
         listA.add(a);
         listB.add(b);
         listC.add(c);
         return a.concat(b).concat(c);
      });
   }

   @Test
   public void testEnumTriFunctionMapOfEntriesEmpty() {

      //@formatter:off
      EnumTriFunctionMap<TestEnum,String,String,String,String> map =
         EnumTriFunctionMap.<TestEnum,String,String,String,String>ofEntries
            (
               TestEnum.class
            );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumTriFunctionMapOfEntriesNull() {

      //@formatter:off
      EnumTriFunctionMap<TestEnum,String,String,String,String> map =
         EnumTriFunctionMap.<TestEnum,String,String,String,String>ofEntries
            (
               TestEnum.class,
               (Map.Entry<TestEnum,TriFunction<String,String,String,String>>) null
            );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumTriFunctionMapOfEntriesOneIsNull() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();
      ArrayList<String> listC = new ArrayList<String>();

      //@formatter:off
      EnumTriFunctionMap<TestEnum,String,String,String,String> map =
         EnumTriFunctionMap.<TestEnum,String,String,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriFunction<String,String,String,String>>( TestEnum.A, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c) ; return a.concat(b).concat(c); } ),
               (Map.Entry<TestEnum,TriFunction<String,String,String,String>>) null,
               new AbstractMap.SimpleImmutableEntry<TestEnum,TriFunction<String,String,String,String>>( TestEnum.C, (a, b, c) -> { listA.add(a); listB.add(b); listC.add(c); return a.concat(b).concat(c); } )
            );
      //@formatter:on
   }

   /*
    * EnumBiConsumerMap Tests
    */

   @Test
   public void testEnumBiConsumerMapAccept() {
      EnumBiConsumerMap<TestEnum, String, String> map = new EnumBiConsumerMap<TestEnum, String, String>(TestEnum.class);
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();

      map.put(TestEnum.A, (a, b) -> {
         listA.add(a);
         listB.add(b);
      });

      map.accept(TestEnum.A, "A", "B");

      Assert.assertEquals(1, listA.size());
      Assert.assertEquals("A", listA.get(0));

      Assert.assertEquals(1, listB.size());
      Assert.assertEquals("B", listB.get(0));
   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumBiConsumerMapAcceptNoSuchElement() {

      EnumBiConsumerMap<TestEnum, String, String> map = new EnumBiConsumerMap<TestEnum, String, String>(TestEnum.class);

      map.accept(TestEnum.A, "A", "B");
   }

   @Test(expected = NullPointerException.class)
   public void testEnumBiConsumerMapAcceptNullKey() {

      EnumBiConsumerMap<TestEnum, String, String> map = new EnumBiConsumerMap<TestEnum, String, String>(TestEnum.class);

      map.accept((TestEnum) null, "A", "B");
   }

   @Test
   public void testEnumBiConsumerMapOfEntries() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();

      //@formatter:off
      EnumBiConsumerMap<TestEnum,String,String> map = EnumBiConsumerMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         new AbstractMap.SimpleImmutableEntry<TestEnum,BiConsumer<String,String>>(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); }),
         new AbstractMap.SimpleImmutableEntry<TestEnum,BiConsumer<String,String>>(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); })
      );
      //@formatter:on

      map.accept(TestEnum.A, "A", "A1");
      map.accept(TestEnum.B, "B", "B1");

      Assert.assertEquals(2, listA.size());
      Assert.assertEquals(2, listB.size());

      Assert.assertEquals("A", listA.get(0));
      Assert.assertEquals("A1", listB.get(0));

      Assert.assertEquals("B", listA.get(1));
      Assert.assertEquals("B1", listB.get(1));
   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumBiConsumerMapOfEntriesDuplicateEntry() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();

      //@formatter:off
      EnumBiConsumerMap<TestEnum,String,String> map =
         EnumBiConsumerMap.<TestEnum,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiConsumer<String,String>>(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); }),
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiConsumer<String,String>>(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); })
            );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumBiConsumerMapOfEntriesPut() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();

      //@formatter:off
      EnumBiConsumerMap<TestEnum,String,String> map =
         EnumBiConsumerMap.<TestEnum,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiConsumer<String,String>>(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); }),
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiConsumer<String,String>>(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); })
            );
      //@formatter:on

      map.put(TestEnum.C, (a, b) -> {
         listA.add(a);
         listB.add(b);
      });
   }

   @Test
   public void testEnumBiConsumerMapOfEntriesEmpty() {

      //@formatter:off
      EnumBiConsumerMap<TestEnum,String,String> map = EnumBiConsumerMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class
      );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumBiConsumerMapOfEntriesNull() {

      //@formatter:off
      EnumBiConsumerMap<TestEnum,String,String> map = EnumBiConsumerMap.<TestEnum,String,String>ofEntries
      (
         TestEnum.class,
         (Map.Entry<TestEnum,BiConsumer<String,String>>) null
      );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumBiConsumerMapOfEntriesOneIsNull() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();

      //@formatter:off
      EnumBiConsumerMap<TestEnum,String,String> map =
         EnumBiConsumerMap.<TestEnum,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiConsumer<String,String>>(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); }),
               (Map.Entry<TestEnum,BiConsumer<String,String>>) null,
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiConsumer<String,String>>(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); })
            );
      //@formatter:on
   }

   /*
    * EnumBiFunctionMap Tests
    */

   @Test
   public void testEnumBiFunctionMapApply() {
      EnumBiFunctionMap<TestEnum, String, String, String> map =
         new EnumBiFunctionMap<TestEnum, String, String, String>(TestEnum.class);
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();

      map.put(TestEnum.A, (a, b) -> {
         listA.add(a);
         listB.add(b);
         return a.concat(b);
      });

      String result = map.apply(TestEnum.A, "A", "B");

      Assert.assertEquals(1, listA.size());
      Assert.assertEquals("A", listA.get(0));

      Assert.assertEquals(1, listB.size());
      Assert.assertEquals("B", listB.get(0));

      Assert.assertEquals("AB", result);
   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumBiFunctionMapAcceptNoSuchElement() {

      EnumBiFunctionMap<TestEnum, String, String, String> map =
         new EnumBiFunctionMap<TestEnum, String, String, String>(TestEnum.class);

      map.apply(TestEnum.A, "A", "B");
   }

   @Test(expected = NullPointerException.class)
   public void testEnumBiFunctionMapAcceptNullKey() {

      EnumBiFunctionMap<TestEnum, String, String, String> map =
         new EnumBiFunctionMap<TestEnum, String, String, String>(TestEnum.class);

      map.apply((TestEnum) null, "A", "B");
   }

   @Test
   public void testEnumBiFunctionMapOfEntries() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();

      //@formatter:off
      EnumBiFunctionMap<TestEnum,String,String,String> map =
         EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiFunction<String,String,String>>(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); }),
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiFunction<String,String,String>>(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); })
            );
      //@formatter:on

      String resultA = map.apply(TestEnum.A, "A", "A1");
      String resultB = map.apply(TestEnum.B, "B", "B1");

      Assert.assertEquals(2, listA.size());
      Assert.assertEquals(2, listB.size());

      Assert.assertEquals("A", listA.get(0));
      Assert.assertEquals("A1", listB.get(0));

      Assert.assertEquals("B", listA.get(1));
      Assert.assertEquals("B1", listB.get(1));

      Assert.assertEquals("AA1", resultA);
      Assert.assertEquals("BB1", resultB);
   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumBiFunctionMapOfEntriesDuplicateEntry() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();

      //@formatter:off
      EnumBiFunctionMap<TestEnum,String,String,String> map =
         EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiFunction<String,String,String>>(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); }),
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiFunction<String,String,String>>(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); })
            );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumBiFunctionMapOfEntriesPut() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();

      //@formatter:off
      EnumBiFunctionMap<TestEnum,String,String,String> map = EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
      (
         TestEnum.class,
         new AbstractMap.SimpleImmutableEntry<TestEnum,BiFunction<String,String,String>>(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); }),
         new AbstractMap.SimpleImmutableEntry<TestEnum,BiFunction<String,String,String>>(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); })
      );
      //@formatter:on

      map.put(TestEnum.C, (a, b) -> {
         listA.add(a);
         listB.add(b);
         return a.concat(b);
      });
   }

   @Test
   public void testEnumBiFunctionMapOfEntriesEmpty() {

      //@formatter:off
      EnumBiFunctionMap<TestEnum,String,String,String> map =
         EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
            (
               TestEnum.class
            );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumBiFunctionMapOfEntriesNull() {

      //@formatter:off
      EnumBiFunctionMap<TestEnum,String,String,String> map =
         EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
            (
               TestEnum.class,
               (Map.Entry<TestEnum,BiFunction<String,String,String>>) null
            );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumBiFunctionMapOfEntriesOneIsNull() {
      ArrayList<String> listA = new ArrayList<String>();
      ArrayList<String> listB = new ArrayList<String>();

      //@formatter:off
      EnumBiFunctionMap<TestEnum,String,String,String> map =
         EnumBiFunctionMap.<TestEnum,String,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiFunction<String,String,String>>(TestEnum.A, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); }),
               (Map.Entry<TestEnum,BiFunction<String,String,String>>) null,
               new AbstractMap.SimpleImmutableEntry<TestEnum,BiFunction<String,String,String>>(TestEnum.B, (a, b) -> { listA.add(a); listB.add(b); return a.concat(b); })
            );
      //@formatter:on
   }

   /*
    * EnumConsumerMap Tests
    */

   @Test
   public void testEnumConsumerMapAccept() {
      EnumConsumerMap<TestEnum, String> map = new EnumConsumerMap<TestEnum, String>(TestEnum.class);
      ArrayList<String> listA = new ArrayList<String>();

      map.put(TestEnum.A, (a) -> listA.add(a));

      map.accept(TestEnum.A, "A");

      Assert.assertEquals(1, listA.size());
      Assert.assertEquals("A", listA.get(0));
   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumConsumerMapAcceptNoSuchElement() {

      EnumConsumerMap<TestEnum, String> map = new EnumConsumerMap<TestEnum, String>(TestEnum.class);

      map.accept(TestEnum.A, "A");
   }

   @Test(expected = NullPointerException.class)
   public void testEnumConsumerMapAcceptNullKey() {

      EnumConsumerMap<TestEnum, String> map = new EnumConsumerMap<TestEnum, String>(TestEnum.class);

      map.accept((TestEnum) null, "A");
   }

   @Test
   public void testEnumConsumerMapOfEntries() {
      ArrayList<String> listA = new ArrayList<String>();

      //@formatter:off
      EnumConsumerMap<TestEnum, String> map =
         EnumConsumerMap.<TestEnum,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Consumer<String>>(TestEnum.A, (a) -> listA.add(a) ),
               new AbstractMap.SimpleImmutableEntry<TestEnum,Consumer<String>>(TestEnum.B, (a) -> listA.add(a) )
            );
      //@formatter:on

      map.accept(TestEnum.A, "A");
      map.accept(TestEnum.B, "B");

      Assert.assertEquals(2, listA.size());

      Assert.assertEquals("A", listA.get(0));

      Assert.assertEquals("B", listA.get(1));
   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumConsumerMapOfEntriesDuplicateEntry() {
      ArrayList<String> listA = new ArrayList<String>();

      //@formatter:off
      EnumConsumerMap<TestEnum, String> map =
         EnumConsumerMap.<TestEnum,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Consumer<String>>(TestEnum.A, (a) -> listA.add(a)),
               new AbstractMap.SimpleImmutableEntry<TestEnum,Consumer<String>>(TestEnum.A, (a) -> listA.add(a))
            );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumConsumerMapOfEntriesPut() {
      ArrayList<String> listA = new ArrayList<String>();

      //@formatter:off
      EnumConsumerMap<TestEnum, String> map =
         EnumConsumerMap.<TestEnum,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Consumer<String>>(TestEnum.A, (a) -> listA.add(a)),
               new AbstractMap.SimpleImmutableEntry<TestEnum,Consumer<String>>(TestEnum.B, (a) -> listA.add(a))
            );
      //@formatter:on

      map.put(TestEnum.C, (a) -> listA.add(a));
   }

   @Test
   public void testEnumConsumerMapOfEntriesEmpty() {

      //@formatter:off
      EnumConsumerMap<TestEnum, String> map = EnumConsumerMap.<TestEnum,String>ofEntries
      (
         TestEnum.class
      );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumConsumerMapOfEntriesNull() {

      //@formatter:off
      EnumConsumerMap<TestEnum, String> map =
         EnumConsumerMap.<TestEnum,String>ofEntries
            (
               TestEnum.class,
               (Map.Entry<TestEnum,Consumer<String>>) null
            );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumConsumerMapOfEntriesOneIsNull() {
      ArrayList<String> listA = new ArrayList<String>();

      //@formatter:off
      EnumConsumerMap<TestEnum, String> map =
         EnumConsumerMap.<TestEnum,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Consumer<String>>(TestEnum.A, (a) -> listA.add(a)),
               (Map.Entry<TestEnum,Consumer<String>>) null,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Consumer<String>>(TestEnum.B, (a) -> listA.add(a))
            );
      //@formatter:on
   }

   /*
    * EnumFunctionMap Tests
    */

   @Test
   public void testEnumFunctionMapAccept() {
      EnumFunctionMap<TestEnum, String, String> map = new EnumFunctionMap<TestEnum, String, String>(TestEnum.class);

      map.put(TestEnum.A, String::toLowerCase);

      String result = map.apply(TestEnum.A, "A");

      Assert.assertEquals("a", result);
   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumFunctionMapAcceptNoSuchElement() {

      EnumFunctionMap<TestEnum, String, String> map = new EnumFunctionMap<TestEnum, String, String>(TestEnum.class);

      map.apply(TestEnum.A, "A");
   }

   @Test(expected = NullPointerException.class)
   public void testEnumFunctionMapAcceptNullKey() {

      EnumFunctionMap<TestEnum, String, String> map = new EnumFunctionMap<TestEnum, String, String>(TestEnum.class);

      map.apply((TestEnum) null, "A");
   }

   @Test
   public void testEnumFunctionMapOfEntries() {

      //@formatter:off
      EnumFunctionMap<TestEnum,String,String> map =
         EnumFunctionMap.<TestEnum,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Function<String,String>>(TestEnum.A, String::toLowerCase ),
               new AbstractMap.SimpleImmutableEntry<TestEnum,Function<String,String>>(TestEnum.B, String::toLowerCase )
            );
      //@formatter:on

      String resultA = map.apply(TestEnum.A, "A");
      String resultB = map.apply(TestEnum.B, "B");

      Assert.assertEquals("a", resultA);

      Assert.assertEquals("b", resultB);
   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumFunctionMapOfEntriesDuplicateEntry() {

      //@formatter:off
      EnumFunctionMap<TestEnum,String,String> map =
         EnumFunctionMap.<TestEnum,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Function<String,String>>(TestEnum.A, String::toLowerCase),
               new AbstractMap.SimpleImmutableEntry<TestEnum,Function<String,String>>(TestEnum.A, String::toLowerCase)
            );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumFunctionMapOfEntriesPut() {

      //@formatter:off
      EnumFunctionMap<TestEnum,String,String> map =
         EnumFunctionMap.<TestEnum,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Function<String,String>>(TestEnum.A, String::toLowerCase),
               new AbstractMap.SimpleImmutableEntry<TestEnum,Function<String,String>>(TestEnum.B, String::toLowerCase)
            );
      //@formatter:on

      map.put(TestEnum.C, String::toLowerCase);
   }

   @Test
   public void testEnumFunctionMapOfEntriesEmpty() {

      //@formatter:off
      EnumFunctionMap<TestEnum,String,String> map =
         EnumFunctionMap.<TestEnum,String,String>ofEntries
            (
               TestEnum.class
            );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumFunctionMapOfEntriesNull() {

      //@formatter:off
      EnumFunctionMap<TestEnum,String,String> map =
         EnumFunctionMap.<TestEnum,String,String>ofEntries
            (
               TestEnum.class,
               (Map.Entry<TestEnum,Function<String,String>>) null
            );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumFunctionMapOfEntriesOneIsNull() {

      //@formatter:off
      EnumFunctionMap<TestEnum,String,String> map =
         EnumFunctionMap.<TestEnum,String,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Function<String,String>>(TestEnum.A, String::toLowerCase),
               (Map.Entry<TestEnum,Function<String,String>>) null,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Function<String,String>>(TestEnum.B, String::toLowerCase)
            );
      //@formatter:on
   }

   /*
    * EnumSupplierMap Tests
    */

   @Test
   public void testEnumSupplierMapAccept() {
      EnumSupplierMap<TestEnum, String> map = new EnumSupplierMap<TestEnum, String>(TestEnum.class);

      map.put(TestEnum.A, () -> {
         return "A";
      });

      String result = map.get(TestEnum.A);

      Assert.assertEquals("A", result);
   }

   @Test(expected = NoSuchElementException.class)
   public void testEnumSupplierMapAcceptNoSuchElement() {

      EnumSupplierMap<TestEnum, String> map = new EnumSupplierMap<TestEnum, String>(TestEnum.class);

      map.get(TestEnum.A);
   }

   @Test(expected = NullPointerException.class)
   public void testEnumSupplierMapAcceptNullKey() {

      EnumSupplierMap<TestEnum, String> map = new EnumSupplierMap<TestEnum, String>(TestEnum.class);

      map.get((TestEnum) null);
   }

   @Test
   public void testEnumSupplierMapOfEntries() {

      //@formatter:off
      EnumSupplierMap<TestEnum, String> map =
         EnumSupplierMap.<TestEnum,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.A, () -> { return "A"; } ),
               new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.B, () -> { return "B"; } )
            );
      //@formatter:on

      String resultA = map.get(TestEnum.A);
      String resultB = map.get(TestEnum.B);

      Assert.assertEquals("A", resultA);

      Assert.assertEquals("B", resultB);
   }

   @SuppressWarnings("unused")
   @Test(expected = EnumMapDuplicateEntryException.class)
   public void testEnumSupplierMapOfEntriesDuplicateEntry() {

      //@formatter:off
      EnumSupplierMap<TestEnum, String> map =
         EnumSupplierMap.<TestEnum,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.A, () -> { return "A"; }),
               new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.A, () -> { return "A"; })
            );
      //@formatter:on
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testEnumSupplierMapOfEntriesPut() {

      //@formatter:off
      EnumSupplierMap<TestEnum, String> map =
         EnumSupplierMap.<TestEnum,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.A, () -> { return "A"; }),
               new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.B, () -> { return "B"; })
            );

      map.put(TestEnum.C, () -> { return "C"; });
      //@formatter:on
   }

   @Test
   public void testEnumSupplierMapOfEntriesEmpty() {

      //@formatter:off
      EnumSupplierMap<TestEnum, String> map =
         EnumSupplierMap.<TestEnum,String>ofEntries
            (
               TestEnum.class
            );
      //@formatter:on

      Assert.assertEquals(0, map.size());
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumSupplierMapOfEntriesNull() {

      //@formatter:off
      EnumSupplierMap<TestEnum, String> map =
         EnumSupplierMap.<TestEnum,String>ofEntries
            (
               TestEnum.class,
               (Map.Entry<TestEnum,Supplier<String>>) null
            );
      //@formatter:on
   }

   @SuppressWarnings("unused")
   @Test(expected = NullPointerException.class)
   public void testEnumSupplierMapOfEntriesOneIsNull() {

      //@formatter:off
      EnumSupplierMap<TestEnum, String> map =
         EnumSupplierMap.<TestEnum,String>ofEntries
            (
               TestEnum.class,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.A, () -> { return "A"; }),
               (Map.Entry<TestEnum,Supplier<String>>) null,
               new AbstractMap.SimpleImmutableEntry<TestEnum,Supplier<String>>(TestEnum.B, () -> { return "B"; })
            );
      //@formatter:on
   }

}

/* EOF */
