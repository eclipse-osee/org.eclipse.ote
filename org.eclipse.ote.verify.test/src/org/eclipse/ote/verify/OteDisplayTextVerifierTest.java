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
package org.eclipse.ote.verify;

import org.eclipse.osee.framework.jdk.core.type.DoublePoint;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.ote.verify.display.OteDisplayTextVerifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Michael P. Masterson
 */
public class OteDisplayTextVerifierTest {
   
   private static final String TEST_COLOR = "Some Color";
   public static final double TEST_DOUBLE = 1.234;
   public static final String TEST_STRING = "Test1";

   @Rule
   public ExpectedException exceptionRule = ExpectedException.none();
   private MockOteApi api;
   private MockTestLogger logger;

   @Before
   public void setup() {
      api = new MockOteApi();
      logger = api.testLogger();
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Test
   public void testPassFailCases() {
      
      OteDisplayTextVerifier expected = new OteDisplayTextVerifier(api);
      OteDisplayTextVerifier actual = new OteDisplayTextVerifier(api);
      
      expected.setPosition(new DoublePoint(TEST_DOUBLE, TEST_DOUBLE));
      expected.setLabel(TEST_STRING);
      expected.setColor(TEST_COLOR);
      
      actual.setPosition(new DoublePoint(TEST_DOUBLE+1, TEST_DOUBLE));
      actual.setLabel(TEST_STRING + "_BAD");
      actual.setColor(TEST_COLOR + "_BAD");
      
      
      CheckGroup group = expected.verify(actual);
      Assert.assertFalse(group.isPass());
      expected.logResults(group);
      ITestPoint result = logger.pop();
      Assert.assertFalse(result.isPass());
      
      
      actual.setPosition(new DoublePoint(TEST_DOUBLE+1, TEST_DOUBLE));
      actual.setLabel(TEST_STRING);
      actual.setColor(TEST_COLOR);
      group = expected.verify(actual);
      Assert.assertFalse(group.isPass());
      expected.logResults(group);
      result = logger.pop();
      Assert.assertFalse(result.isPass());

      actual.setPosition(new DoublePoint(TEST_DOUBLE, TEST_DOUBLE));
      actual.setLabel(TEST_STRING);
      actual.setColor(TEST_COLOR);
      expected.logResults(expected.verify(actual));
      result = logger.pop();
      Assert.assertTrue(result.isPass());
   }
   
   @SuppressWarnings({"unchecked", "rawtypes"})
   @Test
   public void testExceptionWhenMissingActualPositionAttribute() {
      this.exceptionRule.expect(OseeCoreException.class);
      this.exceptionRule.expectMessage("Optional attribute 'Position' was set on expected but not on actual");
      OteDisplayTextVerifier expected = new OteDisplayTextVerifier(api);
      OteDisplayTextVerifier actual = new OteDisplayTextVerifier(api);

      expected.setPosition(new DoublePoint(TEST_DOUBLE, TEST_DOUBLE));
      expected.setLabel(TEST_STRING);
      expected.setColor(TEST_COLOR);

      actual = new OteDisplayTextVerifier(api);
      actual.setLabel(TEST_STRING);
      expected.verify(actual);
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Test
   public void testExceptionWhenMissingActualLabelAttribute() {
      this.exceptionRule.expect(OseeCoreException.class);
      this.exceptionRule.expectMessage("Required attribute 'Label' was never set on actual OTE verifier attribute");
      OteDisplayTextVerifier expected = new OteDisplayTextVerifier(api);
      OteDisplayTextVerifier actual = new OteDisplayTextVerifier(api);

      expected.setPosition(new DoublePoint(TEST_DOUBLE, TEST_DOUBLE));
      expected.setLabel(TEST_STRING);
      expected.setColor(TEST_COLOR);

      actual.setPosition(new DoublePoint(TEST_DOUBLE + 1, TEST_DOUBLE));
      expected.verify(actual);

   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Test
   public void testActualHavingMoreAttributesThanExpected() {
      OteDisplayTextVerifier expected = new OteDisplayTextVerifier(api);
      OteDisplayTextVerifier actual = new OteDisplayTextVerifier(api);

      expected.setLabel(TEST_STRING);
      expected.setColor(TEST_COLOR);

      actual.setPosition(new DoublePoint(TEST_DOUBLE + 1, TEST_DOUBLE));
      actual.setLabel(TEST_STRING);
      actual.setColor(TEST_COLOR);
      expected.logResults(expected.verify(actual));
      ITestPoint result = logger.pop();
      Assert.assertTrue(result.isPass());
      
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Test
   public void testExceptionWhenMissingActualOptionalAttribute() {
      this.exceptionRule.expect(OseeCoreException.class);
      this.exceptionRule.expectMessage("Optional attribute 'Color' was set on expected but not on actual");

      OteDisplayTextVerifier expected = new OteDisplayTextVerifier(api);
      OteDisplayTextVerifier actual = new OteDisplayTextVerifier(api);

      expected.setPosition(new DoublePoint(TEST_DOUBLE, TEST_DOUBLE));
      expected.setLabel(TEST_STRING);
      expected.setColor(TEST_COLOR);

      actual.setPosition(new DoublePoint(TEST_DOUBLE, TEST_DOUBLE));
      actual.setLabel(TEST_STRING);
      expected.verify(actual);

   }

}
