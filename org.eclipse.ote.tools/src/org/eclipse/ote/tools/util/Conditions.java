/*********************************************************************
 * Copyright (c) 2025 Boeing
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.tools.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Methods to be combined with the org.eclipse.osee.framework.jdk.core.util.Conditions class when the next update
 * occurs.
 *
 * @author Loren K. Ashley
 */

public class Conditions {

   /**
    * When the <code>value</code> and <code>consumer</code> are non-<code>null</code> the <code>consumer</code> is
    * applied to the <code>value</code>.
    *
    * @param <V> the type of the function input.
    * @param value the value to be provided to the consumer when non-<code>null</code>.
    * @param consumer the {@link Consumer} to be applied to the <code>value</code> when the <code>value</code> and
    * <code>consumer</code> are non-<code>null</code>.
    */

   public static <V> void acceptWhenNonNull(@Nullable V value, @Nullable Consumer<@NonNull V> consumer) {

      if ((value != null) && (consumer != null)) {
         consumer.accept(value);
      }
   }

   /**
    * When the <code>value</code> and <code>function</code> are non-<code>null</code> the <code>function</code> is
    * applied to the <code>value</code> and the result of the <code>function</code> is returned. When <code>value</code>
    * or <code>function</code> are <code>null</code>, <code>null</code> is returned.
    *
    * @param <T> the return type of the function.
    * @param <V> the type of the function input.
    * @param value the value to be applied to the function when non-<code>null</code>.
    * @param function the {@link Function} to be applied to the <code>value</code> when the <code>value</code> is
    * non-<code>null</code>.
    * @return when <code>value</code> and <code>function</code> are non-<code>null</code> the result of applying the
    * <code>value</code> to the <code>function</code>; otherwise, <code>null</code>.
    */

   public static <T, V> @Nullable T applyWhenNonNull(@Nullable V value, @Nullable Function<@NonNull V, @Nullable T> function) {

      @Nullable
      T result;
      if ((value != null) && (function != null)) {
         result = function.apply(value);
      } else {
         result = null;
      }
      return result;
   }

   /**
    * When the <code>supplier</code> is non-<code>null</code> the <code>supplier</code> is invoked to obtain the return
    * value. When the <code>supplier</code> is <code>null</code>, <code>null</code> is returned.
    *
    * @param <V> the return type of the supplier.
    * @param supplier the {@link Supplier} to be used to obtain the return value.
    * @return when <code>supplier</code> is non-<code>null</code> the result of the <code>supplier</code>; otherwise,
    * <code>null</code>.
    */

   public static <V> @Nullable V getWhenNonNull(@Nullable Supplier<@NonNull V> supplier) {

      @Nullable
      V result;
      if (supplier != null) {
         result = supplier.get();
      } else {
         result = null;
      }
      return result;
   }

   /**
    * Returns the <code>value</code> when it is non-<code>null</code>.
    *
    * @param <T> the type of object to be tested.
    * @param value the value to be tested.
    * @return <code>value</code> when the <code>value</code> is non-<code>null</code>; otherwise, an exception is
    * thrown.
    * @throws {@link NullPointerException} when <code>value</code> is <code>null</code>.
    */

   public static <T> @NonNull T requireNonNull(@Nullable T value) {
      if (value == null) {
         throw new NullPointerException();
      }
      return value;
   }

   /**
    * Returns the {@code value} when it is non-{@code null}.
    *
    * @param <T> the type of object to be tested.
    * @param value the value to be tested.
    * @param message the message to be used for the {@link NullPointerException} when {@code value} is {@code null}.
    * @return {@code value} when the {@code value} is non-{@code null}; otherwise, an exception is thrown.
    * @throws {@link NullPointerException} when {@code value} is {@code null}.
    */

   public static <T> @NonNull T requireNonNull(@Nullable T value, @Nullable String message) {
      if (value == null) {
         throw new NullPointerException(message != null ? message : "(null)");
      }
      return value;
   }

   /**
    * Returns the {@code value} when it is non-{@code null}.
    *
    * @param <T> the type of object to be tested.
    * @param value the value to be tested.
    * @param messageSupplier a {@link Supplier} to be invoked to obtain the message to be used for a
    * {@link NullPointerException} when {@code value} is {@code null}.
    * @return {@code value} when the {@code value} is non-{@code null}; otherwise, an exception is thrown.
    * @throws {@link NullPointerException} when {@code value} is {@code null}.
    */

   public static <T> @NonNull T requireNonNull(@Nullable T value, @Nullable Supplier<@Nullable String> messageSupplier) {
      if (value == null) {
         String message = messageSupplier != null ? messageSupplier.get() : "(null)";
         message = message != null ? message : "(null)";
         throw new NullPointerException(message);
      }
      return value;
   }

   /**
    * Returns the {@code value} when it is non-{@code null} and the provided {@link Iterable} or Array does not contain
    * a {@code null} entry.
    *
    * @param <T> the type of object to be tested.
    * @param value the value to be tested.
    * @param valueMessageSupplier a {@link Supplier} to be invoked to obtain the message to be used for a
    * {@link NullPointerException} when {@code value} is {@code null}.
    * @param itemMessageSupplier a {@link Function} to be invoked to obtain the message to be used for a
    * {@link NullPointerException} when an entry in {@code value} is {@code null}. The {@link Function} will be passed
    * the index of the {@code null} entry.
    * @return {@code value} when the {@code value} is non-{@code null}; otherwise, an exception is thrown.
    * @throws IlleagalArgumentException when {@code value} is not an {@link Iterable} or Array.
    * @throws {@link NullPointerException} when {@code value} is {@code null} or contains a {@code null} entry.
    */

   public static <T> @NonNull T requireNonNullAndContentsNonNull(@Nullable T value, @Nullable Supplier<@Nullable String> valueMessageSupplier, @Nullable Function<@NonNull Integer, @Nullable String> itemMessageSupplier) {

      Conditions.requireNonNull(value, valueMessageSupplier);

      Iterable<?> toCheck = null;
      if (value instanceof Iterable<?>) {
         toCheck = (Iterable<?>) value;
      } else if (value instanceof Object[]) {
         toCheck = Arrays.asList((Object[]) value);
      }

      if (toCheck == null) {
         throw new IllegalArgumentException(
            "Conditions::requireNonNullAndContentNunNull: Parameter \"value\" is not an array or iterable.");
      }

      int index = 0;
      for (Object item : toCheck) {
         if (item == null) {
            String message = (itemMessageSupplier != null) ? itemMessageSupplier.apply(index) : "(null)";
            message = (message != null) ? message : "(null)";
            throw new NullPointerException(message);
         }
         index++;
      }

      return value;
   }

   /**
    * Performs the accept method of the {@code consumer} when the {@link Consumer} is non-{@code null} and the
    * {@code valueSupplier} is also non-{@code null}. When the {@link Consumer} or {@link Supplier} are {@code null} no
    * action is performed.
    *
    * @param <T> the {@link Supplier} provided value type and also the {@link Consumer} consumption type.
    * @param consumer the {@link Consumer} to be run when non-{@code null}.
    * @param valueSupplier the {@code Supplier} to provided the value for the {@link Consumer}.
    */

   public static <T> void runWhenValid(@Nullable Consumer<@Nullable T> consumer, @Nullable Supplier<@Nullable T> valueSupplier) {
      if ((consumer != null) && (valueSupplier != null)) {
         consumer.accept(valueSupplier.get());
      }
   }

   /**
    * Performs the accept method of the {@code consumer} when the {@link Consumer} is not {@code null}. When the
    * {@link Consumer} is {@code null} no action is performed.
    *
    * @param <T> the {@code consumer}'s value type.
    * @param consumer the {@link Consumer} to be run when non-{@code null}.
    * @param value the <code>value</code> to be provided to the {@link Consumer}.
    */

   public static <T> void runWhenValid(@Nullable Consumer<@Nullable T> consumer, @Nullable T value) {
      if (consumer != null) {
         consumer.accept(value);
      }
   }

   /**
    * Performs the apply method of the {@code function} when the {@link Function} is non-{@code null}. When the
    * {@link Function} is {@code null} no action is performed.
    *
    * @param <T> the {@link Function} input value type.
    * @param <R> the {@link FUnction} return value type.
    * @param function the {@link Function} to be run when non-{@code null}.
    * @param value the <code>value</code> to be provided to the {@link Function}.
    * @return an {@link Optional} containing the {@link Function} result; otherwise an empty {@link Optional} when the
    * {@code function} is {@code null}.
    */

   public static <T, R> Optional<R> runWhenValid(Function<T, R> function, T value) {
      if (function != null) {
         return Optional.ofNullable(function.apply(value));
      }
      return Optional.empty();
   }

   /**
    * Performs the run method of the {@code runnable} when the {@link Runnable} is non-{@code null}.
    *
    * @param runnable the {@link Runnable} to be run when non-{@code null}.
    */

   public static void runWhenValid(Runnable runnable) {
      if (runnable != null) {
         runnable.run();
      }
   }
}
