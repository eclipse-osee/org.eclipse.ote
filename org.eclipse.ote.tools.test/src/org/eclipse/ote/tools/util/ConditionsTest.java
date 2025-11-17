/*
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the Enumeration Functional Interface Map classes.
 *
 * @author Loren K. Ashley
 */

public class ConditionsTest {

   @Test
   public void testAcceptWhenNonNull() {

      List<String> list = new ArrayList<String>();

      Consumer<String> consumer = (a) -> list.add(a);

      Conditions.acceptWhenNonNull("A", consumer);

      Assert.assertEquals(1, list.size());
      Assert.assertEquals("A", list.get(0));

   }

   @Test
   public void testAcceptWhenNonNullNullValue() {

      List<String> list = new ArrayList<String>();

      Consumer<String> consumer = (a) -> list.add(a);

      Conditions.acceptWhenNonNull(null, consumer);

      Assert.assertEquals(0, list.size());

   }

   @Test
   public void testAcceptWhenNonNullNullConsumer() {

      Conditions.acceptWhenNonNull("A", null);
   }

   @Test
   public void testApplyWhenNonNull() {

      Function<String, String> function = (a) -> a + "-" + a;

      String result = Conditions.applyWhenNonNull("A", function);

      Assert.assertEquals("A-A", result);

   }

   @Test
   public void testApplyWhenNonNullNullValue() {

      Function<String, String> function = (a) -> a + "-" + a;

      String result = Conditions.applyWhenNonNull(null, function);

      Assert.assertEquals(null, result);

   }

   @Test
   public void testApplyWhenNonNullNullFunction() {

      String result = Conditions.applyWhenNonNull("A", null);

      Assert.assertEquals(null, result);

   }

   @Test
   public void testGetWhenNonNull() {

      Supplier<String> supplier = () -> "A";

      String result = Conditions.getWhenNonNull(supplier);

      Assert.assertEquals("A", result);

   }

   @Test
   public void testGetWhenNonNullSupplierNull() {

      String result = Conditions.getWhenNonNull(null);

      Assert.assertEquals(null, result);

   }

   @Test
   public void testRequireNonNull() {

      String value = "A";

      String result = Conditions.requireNonNull(value);

      Assert.assertEquals("A", result);
   }

   @Test(expected = NullPointerException.class)
   public void testRequireNonNullNullValue() {

      String value = null;

      Conditions.requireNonNull(value);
   }

   @Test
   public void testRequireNonNullStringMessage() {

      String value = "A";

      String result = Conditions.requireNonNull(value, "Value cannot be null.");

      Assert.assertEquals("A", result);
   }

   @Test
   public void testRequireNonNullStringMessageNullValue() {

      String value = null;

      try {

         Conditions.requireNonNull(value, "Value cannot be null.");

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("Value cannot be null.", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullStringMessageNullMessage() {

      String value = "A";

      String result = Conditions.requireNonNull(value, (String) null);

      Assert.assertEquals("A", result);
   }

   @Test
   public void testRequireNonNullStringMessageNullValueNullMessage() {

      String value = null;

      try {

         Conditions.requireNonNull(value, (String) null);

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("(null)", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullSupplierMessage() {

      String value = "A";

      String result = Conditions.requireNonNull(value, () -> "Value cannot be null.");

      Assert.assertEquals("A", result);
   }

   @Test
   public void testRequireNonNullSupplierMessageNullValue() {

      String value = null;

      try {

         Conditions.requireNonNull(value, () -> "Value cannot be null.");

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("Value cannot be null.", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullSupplierMessageNullSupplier() {

      String value = "A";

      String result = Conditions.requireNonNull(value, (Supplier<String>) null);

      Assert.assertEquals("A", result);
   }

   @Test
   public void testRequireNonNullStringSupplierNullValueNullSupplier() {

      String value = null;

      try {

         Conditions.requireNonNull(value, (Supplier<String>) null);

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("(null)", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullStringSupplierNullValueNullMessage() {

      String value = null;

      try {

         Conditions.requireNonNull(value, () -> null);

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("(null)", e.getMessage());
      }
   }

   @Test(expected = IllegalArgumentException.class)
   public void testRequireNonNullAndContentsNonNullNotIterableNotArray() {

      String value = "A";

      Conditions.requireNonNullAndContentsNonNull(value, () -> "A", (i) -> "A");
   }

   @Test
   public void testRequireNonNullAndContentsNonNullIterable() {

      List<String> value = new LinkedList<String>();

      value.add("A");
      value.add("B");

      List<String> result = Conditions.requireNonNullAndContentsNonNull(value, () -> "A", (i) -> "A");

      Assert.assertEquals(2, result.size());
      Assert.assertEquals("A", result.get(0));
      Assert.assertEquals("B", result.get(1));
   }

   @Test
   public void testRequireNonNullAndContentsNonNullArray() {

      String[] value = new String[2];

      value[0] = "A";
      value[1] = "B";

      String[] result = Conditions.requireNonNullAndContentsNonNull(value, () -> "A", (i) -> "A");

      Assert.assertEquals(2, result.length);
      Assert.assertEquals("A", result[0]);
      Assert.assertEquals("B", result[1]);
   }

   @Test
   public void testRequireNonNullAndContentsNonNullNullValue() {

      try {

         Conditions.requireNonNullAndContentsNonNull(null, () -> "Value cannot be null.", (i) -> "A");

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("Value cannot be null.", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullAndContentsNonNullNullValueNullSupplier() {

      try {

         Conditions.requireNonNullAndContentsNonNull(null, null, (i) -> "A");

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("(null)", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullAndContentsNonNullNullValueNullSuppliedMessage() {

      try {

         Conditions.requireNonNullAndContentsNonNull(null, () -> null, (i) -> "A");

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("(null)", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullAndContentsNonNullIterableWithNullEntry() {

      List<String> value = new LinkedList<String>();

      value.add("A");
      value.add(null);
      value.add("B");

      try {

         Conditions.requireNonNullAndContentsNonNull(value, () -> "A", (i) -> "Entry " + i + " cannot be null.");

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("Entry 1 cannot be null.", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullAndContentsNonNullArrayWithNullEntry() {

      String[] value = new String[3];

      value[0] = "A";
      value[1] = null;
      value[2] = "B";

      try {

         Conditions.requireNonNullAndContentsNonNull(value, () -> "A", (i) -> "Entry " + i + " cannot be null.");

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("Entry 1 cannot be null.", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullAndContentsNonNullIterableWithNullEntryNullSupplier() {

      List<String> value = new LinkedList<String>();

      value.add("A");
      value.add(null);
      value.add("B");

      try {

         Conditions.requireNonNullAndContentsNonNull(value, () -> "A", null);

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("(null)", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullAndContentsNonNullArrayWithNullEntryNullSupplier() {

      String[] value = new String[3];

      value[0] = "A";
      value[1] = null;
      value[2] = "B";

      try {

         Conditions.requireNonNullAndContentsNonNull(value, () -> "A", null);

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("(null)", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullAndContentsNonNullIterableWithNullEntryNullSuppliedMessage() {

      List<String> value = new LinkedList<String>();

      value.add("A");
      value.add(null);
      value.add("B");

      try {

         Conditions.requireNonNullAndContentsNonNull(value, () -> "A", (i) -> null);

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("(null)", e.getMessage());
      }
   }

   @Test
   public void testRequireNonNullAndContentsNonNullArrayWithNullEntryNullSuppliedMessage() {

      String[] value = new String[3];

      value[0] = "A";
      value[1] = null;
      value[2] = "B";

      try {

         Conditions.requireNonNullAndContentsNonNull(value, () -> "A", (i) -> null);

         Assert.assertFalse(true);

      } catch (NullPointerException e) {

         Assert.assertEquals("(null)", e.getMessage());
      }
   }

   @Test
   public void testRunWhenValueConsumerSupplier() {
      List<String> value = new LinkedList<>();
      Consumer<String> consumer = (v) -> value.add(v);
      Supplier<String> supplier = () -> "A";

      Conditions.runWhenValid(consumer, supplier);

      Assert.assertEquals(1, value.size());
      Assert.assertEquals("A", value.get(0));
   }

   @Test
   public void testRunWhenValueConsumerSupplierNullConsumer() {
      List<String> value = new LinkedList<>();
      Consumer<String> consumer = null;
      Supplier<String> supplier = () -> "A";

      Conditions.runWhenValid(consumer, supplier);

      Assert.assertEquals(0, value.size());
   }

   @Test
   public void testRunWhenValueConsumerSupplierNullSupplier() {
      List<String> value = new LinkedList<>();
      Consumer<String> consumer = (v) -> value.add(v);
      Supplier<String> supplier = null;

      Conditions.runWhenValid(consumer, supplier);

      Assert.assertEquals(0, value.size());
   }

   @Test
   public void testRunWhenValueConsumerSupplierNullConsumerNullSupplier() {
      List<String> value = new LinkedList<>();
      Consumer<String> consumer = null;
      Supplier<String> supplier = null;

      Conditions.runWhenValid(consumer, supplier);

      Assert.assertEquals(0, value.size());
   }

   @Test
   public void testRunWhenValidConsumer() {
      List<String> value = new LinkedList<>();
      Consumer<String> consumer = (v) -> value.add(v);

      Conditions.runWhenValid(consumer, "A");

      Assert.assertEquals(1, value.size());
      Assert.assertEquals("A", value.get(0));
   }

   @Test
   public void testRunWhenValueConsumerNullConsumer() {
      List<String> value = new LinkedList<>();
      Consumer<String> consumer = null;

      Conditions.runWhenValid(consumer, "A");

      Assert.assertEquals(0, value.size());
   }

   @Test
   public void testRunWhenValidFunction() {
      Function<String, String> function = (v) -> "-" + v + "-";
      Optional<String> result = Conditions.runWhenValid(function, "A");

      Assert.assertTrue(result.isPresent());
      Assert.assertEquals("-A-", result.get());
   }

   @Test
   public void testRunWhenValidFunctionNullFunction() {
      Function<String, String> function = null;
      Optional<String> result = Conditions.runWhenValid(function, "A");

      Assert.assertFalse(result.isPresent());
   }

   @Test
   public void testRunWhenValidRunnable() {
      List<String> value = new LinkedList<>();
      Runnable runnable = () -> value.add("A");

      Conditions.runWhenValid(runnable);

      Assert.assertEquals(1, value.size());
      Assert.assertEquals("A", value.get(0));
   }

   @Test
   public void testRunWhenValidRunnableNullRunnable() {
      List<String> value = new LinkedList<>();
      Runnable runnable = null;

      Conditions.runWhenValid(runnable);

      Assert.assertEquals(0, value.size());
   }

}

/* EOF */
