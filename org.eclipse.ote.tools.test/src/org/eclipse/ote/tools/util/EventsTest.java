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

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link Events} class.
 *
 * @author Loren K. Ashley
 */

public class EventsTest {

   @Test
   public void eventWaitMded() {

      int maxAttempts = 4;
      int[] count = new int[1];
      int[] delayCount = new int[1];
      int[] delayValue = new int[1];
      int[] passOn = new int[1];

      for (int i = 1; i <= maxAttempts; i++) {

         for (int p = 1; p <= i; p++) {

            count[0] = 0;
            delayCount[0] = 0;
            delayValue[0] = -1;
            passOn[0] = p;
            final int maxCount = i;
            final int delay = i * p;
            Supplier<Boolean> eventCheck = () -> {
               count[0]++;
               return count[0] == passOn[0];
            };
            Consumer<Integer> delayAction = (delayLambda) -> {
               delayCount[0]++;
               delayValue[0] = delayLambda;
            };

            Events.waitForEvent(maxCount, delay, eventCheck, delayAction);

            Assert.assertEquals(String.format("EventCheck(i=%d,p=%d)", i, p), p, count[0]);
            Assert.assertEquals(String.format("DelayCount(i=%d,p=%d)", i, p), p - 1, delayCount[0]);
            Assert.assertEquals(String.format("DelayValue(i=%d,p=%d)", i, p), p > 1 ? i * p : -1, delayValue[0]);

         }
      }
   }

   @Test
   public void eventWaitEmdebald() {

      int maxAttempts = 4;
      int[] count = new int[1];
      int[] beforeCount = new int[1];
      int[] afterCount = new int[1];

      int[] delayCount = new int[1];
      int[] delayValue = new int[1];
      int[] passOn = new int[1];
      String[] messages = new String[maxAttempts];
      int[] messageCount = new int[1];

      for (int i = 1; i <= maxAttempts; i++) {

         for (int p = 1; p <= i; p++) {

            count[0] = 0;
            beforeCount[0] = 0;
            afterCount[0] = 0;
            delayCount[0] = 0;
            delayValue[0] = -1;
            passOn[0] = p;
            messageCount[0] = 0;
            final String titleMessage = "Event Name";
            final int maxCount = i;
            final int delay = i * p;
            Supplier<Boolean> eventCheck = () -> {
               count[0]++;
               return count[0] == passOn[0];
            };
            Runnable beforeAction = () -> beforeCount[0]++;
            Runnable afterAction = () -> afterCount[0]++;
            Consumer<Integer> delayAction = (delayLambda) -> {
               delayCount[0]++;
               delayValue[0] = delayLambda;
            };
            Consumer<String> logAction = (message) -> messages[messageCount[0]++] = message;

            //@formatter:off
            Events.waitForEvent
               (
                  "Event Name",
                  maxCount,
                  delay,
                  eventCheck,
                  beforeAction,
                  afterAction,
                  logAction,
                  delayAction
               );
            //@formatter:on

            Assert.assertEquals(String.format("EventCheck(i=%d,p=%d)", i, p), p, count[0]);
            Assert.assertEquals(String.format("BeforeAction(i=%d,p=%d)", i, p), p, beforeCount[0]);
            Assert.assertEquals(String.format("AfterAction(i=%d,p=%d)", i, p), p, afterCount[0]);
            Assert.assertEquals(String.format("DelayCount(i=%d,p=%d)", i, p), p - 1, delayCount[0]);
            Assert.assertEquals(String.format("DelayValue(i=%d,p=%d)", i, p), p > 1 ? i * p : -1, delayValue[0]);
            Assert.assertEquals(String.format("DelayValue(i=%d,p=%d)", i, p), titleMessage, messages[0]);
            for (int j = 1; j < p; j++) {
               Assert.assertEquals(String.format("RetryMessage(i=%d,p=%d)", i, p), "Retry Count: " + j, messages[j]);
            }

         }
      }
   }

}
