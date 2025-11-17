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

package org.eclipse.ote.tools.util.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents an operation that accepts three parameters and returns a result.
 *
 * @author Loren K. Ashley
 * @param <T> the first argument type
 * @param <U> the second argument type
 * @param <V> the third argument type
 * @param <R> the return type
 */

@FunctionalInterface
public interface TriFunction<T, U, V, R> {

   /**
    * Performs the implementation's operations on the specified parameters.
    *
    * @param t the first parameter.
    * @param u the second parameter.
    * @param v the third parameter.
    * @return the result of type &lt;R&gt;.
    */

   R apply(T t, U u, V v);

   /**
    * Creates a composed {@link TriFunction} that performs this operation followed by the {@code after} {@code Function}
    * applied to the {@link TriFunction}'s result. If either the {@link TriFunction} or the {@link Function} throws an
    * exception, the exception is relayed to the caller of the composed operation. If this {@link TriFunction} throws an
    * exception, the {@code after} {@link Function} operation is not performed.
    *
    * @param <RC> the return type of the composed {@link TriFunction}.
    * @param after the {@link Function} to be applied after this {@link TriFunction}.
    * @return a composed {@link TriConsumer} that performs this operation followed by the {@code after} operation.
    */

   default <RC> TriFunction<T, U, V, RC> andThen(Function<? super R, ? extends RC> after) {

      Objects.requireNonNull(after);

      //@formatter:off
      return
         ( t, u, v ) ->
         {
            R r = apply( t , u, v );
            RC rc = after.apply( r );
            return rc;
         };
      //@formatter:on
   }

}
