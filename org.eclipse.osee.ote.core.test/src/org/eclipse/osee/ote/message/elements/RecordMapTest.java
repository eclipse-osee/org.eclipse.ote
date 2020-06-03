/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ote.message.elements;

import org.eclipse.osee.ote.message.mock.TestMessage;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class RecordMapTest {

   @Test
   public void testGetInt() {
      TestMessage msg = new TestMessage();

      // Testing below the boundary.
      try {
         msg.RECORD_MAP_1.get(1);
         Assert.assertTrue(true);
      } catch (IllegalArgumentException ex) {
         Assert.fail("We shouldn't get an exception for this get!");
      }

      // Testing on the boundary.
      try {
         msg.RECORD_MAP_1.get(2);
         Assert.fail("We should get an exception for this get on the boundary!");
      } catch (IllegalArgumentException ex) {
         Assert.assertTrue("We should get an exception for this index", true);
      }
      // Testing above the boundary.
      try {
         msg.RECORD_MAP_1.get(3);
         Assert.fail("We should get an exception for this get above the boundary!");
      } catch (IllegalArgumentException ex) {
         Assert.assertTrue("We should get an exception for this index", true);
      }

   }
}
