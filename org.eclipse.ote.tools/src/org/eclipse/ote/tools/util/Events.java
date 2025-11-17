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
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * A class of static methods to wait for events.
 *
 * @author Loren K. Ashley
 */

public class Events {

   /**
    * Waits for the occurrence of an event.
    *
    * @param maxCount the maximum number of checks made for the event.
    * @param delay the time units used by the {@link delayAction} to wait between event checks.
    * @param eventCheck a {@link Supplier} that returns {@link true} when the event has been detected.
    * @param delayAction when non-{@code null} this {@link Consumer} will be used to perform the delay between attempts.
    * @return {@code true} when the event was detected; otherwise, {@code false}.
    * @throws NullPointerExecption when {@link eventCheck} is {@code null} or returns {@code null}.
    */

   //@formatter:off
   public static boolean
      waitForEvent
         (
                      int                        maxCount,
                      int                        delay,
            @NonNull  Supplier<@NonNull Boolean> eventCheck,
            @Nullable Consumer<Integer>          delayAction
         ) {
      //@formatter:on

      Conditions.requireNonNull(eventCheck, "Events::waitForEvent: Parameter \"eventCheck\" cannot be null.");

      Boolean eventOk = eventCheck.get();

      Conditions.requireNonNull(eventOk, "Events::waitForEvent: Supplier \"eventCheck\" returned null.");

      for (int safety = 1; !eventOk && (safety < maxCount); safety++) {

         Conditions.runWhenValid(delayAction, delay);

         eventOk = eventCheck.get();

         Conditions.requireNonNull(eventOk, "Events: Supplier \"eventCheck\" returned null.");
      }

      return eventOk;
   }

   /**
    * Waits for the occurrence of an event.
    *
    * @param eventName when non-{@code null}, the {@code eventName} is displayed as a console prompt.
    * @param maxCount the maximum number of checks made for the event.
    * @param delay the time units used by the {@link delayAction} to wait between event checks.
    * @param eventCheck a {@link Supplier} that returns {@link true} when the event has been detected.
    * @param beforeAction when non-{@code null} this {@link Runnable} is run before each event check.
    * @param afterAction when non-{@code null} this {@link Runnable} is run after each event check. The
    * {@link afterAction} will be run even after a positive event check.
    * @param logAction when non-{@code null} this {@link Consumer} will be used to log the {@link eventName} at the
    * start of the method and a retry count message before each retry.
    * @param delayAction when non-{@code null} this {@link Consumer} will be used to perform the delay between attempts.
    * @return {@code true} when the event was detected; otherwise, {@code false}.
    * @throws NullPointerExecption when {@link eventCheck} is {@code null} or returns {@code null}.
    */

   //@formatter:off
   public static boolean
      waitForEvent
         (
            @Nullable String                     eventName,
                      int                        maxCount,
                      int                        delay,
            @NonNull  Supplier<@NonNull Boolean> eventCheck,
            @Nullable Runnable                   beforeAction,
            @Nullable Runnable                   afterAction,
            @Nullable Consumer<String>           logAction,
            @Nullable Consumer<Integer>          delayAction
         ) {
      //@formatter:on

      Conditions.requireNonNull(eventCheck, "Events::waitForEvent: Parameter \"eventCheck\" cannot be null.");

      Conditions.runWhenValid(logAction, eventName);

      Conditions.runWhenValid(beforeAction);

      Boolean eventOk = eventCheck.get();

      Conditions.requireNonNull(eventOk, "Events::waitForEvent: Supplier \"eventCheck\" returned null.");

      Conditions.runWhenValid(afterAction);

      for (int safety = 1; !eventOk && (safety < maxCount); safety++) {

         final int finalSafety = safety;

         Conditions.runWhenValid(logAction, () -> "Retry Count: " + finalSafety);

         Conditions.runWhenValid(delayAction, delay);

         Conditions.runWhenValid(beforeAction);

         eventOk = eventCheck.get();

         Conditions.requireNonNull(eventOk, "Events: Supplier \"eventCheck\" returned null.");

         Conditions.runWhenValid(afterAction);
      }

      return eventOk;
   }

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private Events() {
   }

}
