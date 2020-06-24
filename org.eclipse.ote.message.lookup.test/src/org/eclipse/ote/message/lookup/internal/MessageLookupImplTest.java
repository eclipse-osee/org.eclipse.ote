/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.ote.message.lookup.internal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.ote.message.lookup.MessageLookupResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Michael P. Masterson
 */
public class MessageLookupImplTest {

   private static final int UNIQUE_PROVIDER_ID = 1;

   private MessageLookupImpl impl;

   @Before
   public void setupLookup() throws IOException, ClassNotFoundException, SQLException {
      impl = new MessageLookupImpl();
      impl.initialize();
   }

   @After
   public void cleanup() {
      impl.stop();
   }

   @Test(timeout = 60000)
   public void testAdd() throws IOException, ClassNotFoundException, SQLException {
      createMessage(impl, "test.test1", "test1", "testType", "el1", "el2", "el3");
      createMessage(impl, "zzztest.test2", "test2", "testType", "el1", "el2", "el3");
      createMessage(impl, "test.test2", "test3", "otherType", "ell1", "ell2", "ell3");
   }

   @Test(timeout = 60000)
   public void testWildcardSearch() throws IOException, ClassNotFoundException, SQLException {
      createMessage(impl, "test.test1", "test1", "testType", "el1", "el2", "el3");
      createMessage(impl, "zzztest.test2", "test2", "testType", "el1", "el2", "el3");
      createMessage(impl, "test.test2", "test3", "otherType", "ell1", "ell2", "ell3");

      Collection<MessageLookupResult> results;
      results = impl.lookup(".*test.*");
      Assert.assertEquals(3, results.size());
      results = impl.lookup("*test*");
      Assert.assertEquals(3, results.size());
      results = impl.lookup("test");
      Assert.assertEquals(0, results.size());
   }

   @Test(timeout = 60000)
   public void testElementSearch() throws IOException, ClassNotFoundException, SQLException {
      createMessage(impl, "test.test1", "test1", "testType", "el1", "el12", "el3");
      createMessage(impl, "zzztest.test2", "test2", "testType", "el1", "el2", "el3");
      createMessage(impl, "test.test2", "test3", "otherType", "ell1", "ell2", "el3");

      List<MessageLookupResult> results;
      results = impl.lookup("ell*");
      Assert.assertEquals(1, results.size());
      MessageLookupResult resultEntry = results.get(0);
      Assert.assertEquals(2, resultEntry.getElements().size());

      results = impl.lookup("el1*");
      Assert.assertEquals(2, results.size());
      resultEntry = results.get(0);
      Assert.assertEquals(2, resultEntry.getElements().size());
      resultEntry = results.get(1);
      Assert.assertEquals(1, resultEntry.getElements().size());
   }

   @Test(timeout = 60000)
   public void loadTable() throws IOException, ClassNotFoundException, SQLException {
      MessageLookupTestUtil.loadSampleData(impl, UNIQUE_PROVIDER_ID);
   }

   @Test(timeout = 60000)
   public void loadTableSearches() throws IOException, ClassNotFoundException, SQLException {
      MessageLookupTestUtil.loadSampleData(impl, UNIQUE_PROVIDER_ID);

      List<MessageLookupResult> results;

      results = impl.lookup("*_CMD", "WIRE");
      int wire = results.size();
      Assert.assertEquals(4, wire);

      results = impl.lookup("*_CMD", "ETHERNET");
      int ethernet = results.size();
      Assert.assertEquals(3, ethernet);

      results = impl.lookup("*_CMD", "PUB_SUB");
      int pubsub = results.size();
      Assert.assertEquals(6, pubsub);

      results = impl.lookup("*_CMD", "MUX");
      int mux = results.size();
      Assert.assertEquals(5, mux);

      results = impl.lookup("*_CMD", "ETHERNET", "PUB_SUB");
      Assert.assertEquals(ethernet + pubsub, results.size());

      results = impl.lookup("*_CMD");
      Assert.assertEquals(wire + ethernet + pubsub + mux, results.size());

      results = impl.lookup("*_REQUEST", "PUB_SUB");
      Assert.assertEquals(3, results.size());

      results = impl.lookup("1471");
      Assert.assertEquals(0, results.size());

      results = impl.lookup("40");
      Assert.assertEquals(1, results.size());

      results = impl.lookup("*");
      int fastSize = results.size();
      Assert.assertTrue(fastSize > 0);
      results = impl.lookup("**");
      int slowSize = results.size();
      Assert.assertTrue(slowSize > 0);

      results = impl.lookup("*", "PUB_SUB");
      int pubsubAllMessageSize = results.size();
      Assert.assertEquals(9, pubsubAllMessageSize);

      results = impl.lookup("**", "PUB_SUB");
      int pubsubAllSize = results.size();
      Assert.assertEquals(9, pubsubAllSize);

      results = impl.lookup("*", "PUB_SUB", "MUX");
      int pubsubMuxAllSize = results.size();
      Assert.assertEquals(14, pubsubMuxAllSize);

   }

   @Test(timeout = 60000)
   public void testRemove() throws IOException, ClassNotFoundException, SQLException {
      MessageLookupTestUtil.loadSampleData(impl, UNIQUE_PROVIDER_ID);
      List<MessageLookupResult> results = impl.lookup("*_REQUEST", "PUB_SUB");
      Assert.assertNotSame(0, results.size());
      impl.removeFromLookup(UNIQUE_PROVIDER_ID);
      results = impl.lookup("*_REQUEST", "PUB_SUB");
      Assert.assertEquals(0, results.size());
   }

   @Test(timeout = 60000)
   public void testLookupClass() throws IOException, ClassNotFoundException, SQLException {
      MessageLookupTestUtil.loadSampleData(impl, UNIQUE_PROVIDER_ID);
      final String className = "lookup.test.ps.EXAMPLE_1_CMD";
      MessageLookupResult result = impl.lookupClass(className);
      Assert.assertNotNull(result);
      Assert.assertEquals(className, result.getClassName());
      Assert.assertEquals("EXAMPLE_1_CMD", result.getMessageName());
      Assert.assertEquals("PUB_SUB", result.getMessageType());
      Assert.assertEquals(20, result.getByteSize());
      Assert.assertEquals("0", result.getPhase());
      Assert.assertEquals("50.0", result.getRate());
      Assert.assertEquals("false", result.getScheduled());

      result = impl.lookupClass("EXAMPLE_1_CMD");
      Assert.assertEquals(result, null);
   }

   private void createMessage(MessageLookupImpl lookup, String clazz, String name, String type,
         String... element) throws SQLException {
      List<String> elements = Arrays.asList(element);
      lookup.addToLookup(UNIQUE_PROVIDER_ID, clazz, name, type, 0, 0, "", "", "", elements);
   }

}
