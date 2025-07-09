/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * A container object for one value.
 *
 * @param F the type of the member.
 * @author Loren K. Ashley
 */

public class Singlet<F> implements Serializable {

   /**
    * A static empty immutable {@link Singlet} literal.
    */

   private static @NonNull Singlet<?> EMPTY = Singlet.createNullableImmutable(null);

   /**
    * Serialization identifier.
    * <p>
    * See {@link Serializable}.
    */

   private static final long serialVersionUID = 1764353834209869140L;

   /**
    * Creates a new {@link Singlet} with non-<code>null</code> values.
    *
    * @param <F> the type of the object in the {@link Singlet}.
    * @param first the object to store in the new {@link Singlet}.
    * @return a new {@link Singlet} containing the object <code>first</code>.
    * @throws NullPointerException when the parameter <code>first</code> is <code>null</code>.
    */

   public static <F> org.eclipse.ote.tools.util.Singlet<F> createNonNull(@NonNull F first) {
      return new Singlet<>(Objects.requireNonNull(first));
   }

   /**
    * Creates a new immutable {@link Singlet} with a non-<code>null</code> value. The setter methods of the returned
    * {@link Singlet} will all throw an {@link UnsupportedOperationException}.
    *
    * @param <F> the type of the object in the {@link Singlet}.
    * @param first the object to store in the new {@link Singlet}.
    * @return a new immutable {@link Singlet} containing the object <code>first</code>.
    * @throws NullPointerException when either parameter is <code>null</code>.
    */

   public static <F> org.eclipse.ote.tools.util.Singlet<F> createNonNullImmutable(@NonNull F first) {
      return createNullableImmutable(Objects.requireNonNull(first));
   }

   /**
    * Creates a new {@link Singlet} with possibly <code>null</code> values.
    *
    * @param <F> the type of the object in the {@link Singlet}.
    * @param first the object to store in the new {@link Singlet}.
    * @return a new {@link Singlet} containing the object <code>first</code>.
    */

   public static <F> org.eclipse.ote.tools.util.Singlet<F> createNullable(@Nullable F first) {
      return new Singlet<>(first);
   }

   /**
    * Creates a new immutable {@link Singlet} with a possibly <code>null</code> value. The setter methods of the
    * returned {@link Singlet} will all throw an {@link UnsupportedOperationException}.
    *
    * @param <F> the type of the object in the {@link Singlet}.
    * @param first the object to store in the new {@link Singlet}.
    * @return a new {@link Singlet} containing the object <code>first</code>.
    */

   public static <F> org.eclipse.ote.tools.util.Singlet<F> createNullableImmutable(@Nullable F first) {
      //@formatter:off
      return
         new Singlet<F>( first ) {

            private static final long serialVersionUID = Singlet.serialVersionUID;

            @Override
            public Singlet<F> set(F first) {
               throw new UnsupportedOperationException();
            }

            @Override
            public void setFirst(F first) {
               throw new UnsupportedOperationException();
            }

         };
      //@formatter:on
   }

   @SuppressWarnings("unchecked")
   public static <F> org.eclipse.ote.tools.util.Singlet<F> empty() {
      return (Singlet<F>) Singlet.EMPTY;
   }

   /**
    * Predicate to determine if the member of the singlet is not set.
    *
    * @return <code>true</code> when the member of the singlet is not set; otherwise, <code>false</code>.
    */

   public boolean isEmpty() {
      return (this.first == null);
   }

   /**
    * Saves the first member of the pair.
    */

   protected @Nullable F first;

   /**
    * Creates a new {@link Singlet} with a <code>null</code> value.
    */

   public Singlet() {
      this.first = null;
   }

   /**
    * Creates a new {@link Singlet} with the possibly <code>null</code> value <code>first</code>.
    *
    * @param first the value for the first member of the {@link Singlet}.
    */

   public Singlet(@Nullable F first) {
      this.first = first;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean equals(Object obj) {

      if (!(obj instanceof Singlet<?>)) {
         return false;
      }

      Singlet<?> other = (Singlet<?>) obj;

      boolean firstEqual;

      if (this.first != null) {
         firstEqual = this.first.equals(other.first);
      } else {
         firstEqual = (other.first == null);
      }

      return firstEqual;
   }

   /**
    * Gets the first value of the {@link Singlet}.
    *
    * @return the first value.
    */

   public @Nullable F getFirst() {
      return this.first;
   }

   /**
    * Gets the first value of the {@link Singlet}.
    *
    * @return the first value.
    * @throws NullPointerException if the first value is <code>null</code>.
    */

   public @NonNull F getFirstNonNull() {
      return Conditions.requireNonNull(this.first);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public int hashCode() {

      return this.first.hashCode();

   }

   /**
    * Performs an action upon the {@link Singlet} members that are non-<code>null</code>.
    *
    * @param firstAction the action to perform on the member {@link #first} when non-<code>null</code>.
    * @throws NullPointerException when a member is non-<code>null</code> and the corresponding {@link Consumer}
    * parameter is <code>null</code>.
    */

   public void ifPresent(@Nullable Consumer<@NonNull F> firstAction) {

      Conditions.acceptWhenNonNull(this.first, firstAction);

   }

   /**
    * Sets the first and second values of the {@link Singlet}.
    *
    * @param first the value for the first member of the {@link Singlet}.
    */

   public @NonNull Singlet<F> set(@Nullable F first) {
      this.first = first;
      return this;
   }

   /**
    * Sets the first value of the {@link Singlet}.
    *
    * @param first the value to be set as the {@link #first} member.
    */

   public void setFirst(@Nullable F first) {
      this.first = first;
   }

   /**
    * Generates a string representation of the {@link Singlet} using the {@link Object#toString} method of each value.
    * The message is formatted as follows:
    * <p>
    * <code>
    *    "[" &lt;first-to-string&gt; ", " &lt;second-to-string&gt; "]"
    * </code>
    *
    * @return a {@link String} representation of the {@link Singlet}.
    */

   @Override
   public @NonNull String toString() {
      String firstAsString = String.valueOf(this.first);
      String result = String.format("[%s]", firstAsString);
      return Conditions.requireNonNull(result);
   }

   /**
    * Predicate to determine if the {@link Class} of the value is not as expected. A <code>null</code> value is
    * considered as matching.
    *
    * @param firstClass the expected {@link Class} of the first value.
    * @return <code>false</code> when the value is of the expected {@link Class}; otherwise, <code>true</code>.
    * @throws NullPointerException when <code>firstClass</code> or <code>secondClass</code> is <code>null</code>.
    */

   public boolean typesKo(@NonNull Class<?> firstClass) {
      return !this.typesOk(firstClass);
   }

   /**
    * Predicate to determine if the {@link Class} of the value is as expected. A <code>null</code> value is considered
    * as matching.
    *
    * @param firstClass the expected {@link Class} of the first value.
    * @return <code>true</code> when both values are of the expected {@link Class}; otherwise, <code>false</code>.
    * @throws NullPointerException when <code>firstClass</code> or <code>secondClass</code> is <code>null</code>.
    */

   public boolean typesOk(@NonNull Class<?> firstClass) {

      Class<?> classOfFirst = Conditions.applyWhenNonNull(this.first, (f) -> f.getClass());

      //@formatter:off
      return
            (    ( classOfFirst == null )
              || firstClass.isAssignableFrom( classOfFirst ) );
      //@formatter:on
   }

}

/* EOF */