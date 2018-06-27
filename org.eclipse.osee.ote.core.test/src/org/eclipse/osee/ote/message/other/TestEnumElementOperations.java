/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.other;

import static org.eclipse.osee.ote.message.mock.TestEnum.VAL_0;
import static org.eclipse.osee.ote.message.mock.TestEnum.VAL_1;
import static org.eclipse.osee.ote.message.mock.TestEnum.VAL_10;
import static org.eclipse.osee.ote.message.mock.TestEnum.VAL_2;
import static org.eclipse.osee.ote.message.mock.TestEnum.VAL_3;
import static org.eclipse.osee.ote.message.mock.TestEnum.VAL_4;
import static org.eclipse.osee.ote.message.mock.TestEnum.VAL_5;
import static org.eclipse.osee.ote.message.mock.TestEnum.VAL_6;
import static org.eclipse.osee.ote.message.mock.TestEnum.VAL_7;
import static org.eclipse.osee.ote.message.mock.TestEnum.VAL_8;
import static org.eclipse.osee.ote.message.mock.TestEnum.VAL_9;
import java.util.EnumSet;
import org.eclipse.osee.ote.message.mock.TestEnum;
import org.eclipse.osee.ote.message.mock.TestMessage;
import org.eclipse.osee.ote.message.mock.UnitTestSupport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Roberto E. Escobar
 */
public class TestEnumElementOperations {

   private UnitTestSupport support;

   @BeforeClass
   public static void setUpBeforeClass() throws Exception {
   }

   @AfterClass
   public static void tearDownAfterClass() throws Exception {
   }

   @Before
   public void setUp() throws Exception {
      support = new UnitTestSupport();
   }

   @After
   public void tearDown() throws Exception {
      support.cleanup();
   }

   @Test
   public void testCheckWaitForValue() throws InterruptedException {
      TestMessage msg = new TestMessage();
      support.activateMsg(msg);
      support.genericTestCheckWaitForValue(msg.ENUM_ELEMENT_1, EnumSet.of(VAL_0, VAL_9).toArray(new TestEnum[] {}),
         VAL_10);

      support.genericTestCheckWaitForValue(msg.ENUM_ELEMENT_1, EnumSet.of(VAL_1, VAL_10).toArray(new TestEnum[] {}),
         VAL_0);
   }

   @Test
   public void testCheckNot() throws InterruptedException {
      TestMessage msg = new TestMessage();
      support.activateMsg(msg);
      support.genericTestCheckNot(msg.ENUM_ELEMENT_1, TestEnum.values());
   }

   @Test
   public void testCheckMaintain() throws InterruptedException {
      TestMessage msg = new TestMessage();
      support.activateMsg(msg);
      support.genericCheckMaintain(msg.ENUM_ELEMENT_1, TestEnum.values());
   }

   @Test
   public void testCheckList() throws InterruptedException {
      TestMessage msg = new TestMessage();
      support.activateMsg(msg);

      support.genericTestCheckList(msg.ENUM_ELEMENT_1, new TestEnum[] {VAL_0, VAL_2, VAL_4, VAL_6, VAL_8, VAL_10},
         new TestEnum[] {VAL_1, VAL_3, VAL_5, VAL_7, VAL_9});
   }
}
