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

/**
 * Represents an operation that accepts three parameters and does not return a result.
 *
 * @author Loren K. Ashley
 * @param <T> the first argument type
 * @param <U> the second argument type
 * @param <V> the third argument type
 */

@FunctionalInterface
public interface TriConsumer<T, U, V> {

   /**
    * Performs the implementation's operations on the specified parameters.
    *
    * @param t the first parameter.
    * @param u the second parameter.
    * @param v the third parameter.
    */

   void accept(T t, U u, V v);

   /**
    * Creates a composed {@link TriConsumer} that performs this operation followed by the {@code after} operation. If
    * either {@link TriConsumer} throws an exception, the exception is relayed to the caller of the composed operation.
    * If this operation throws an exception, the {@code after} operation is not performed.
    *
    * @param after
    * @return a composed {@link TriConsumer} that performs this operation followed by the {@code after} operation.
    */

   default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {

      Objects.requireNonNull(after);

      //@formatter:off
      return
         ( t, u, v ) ->
         {
            accept( t , u, v );
            after.accept( t, u, v );
         };
      //@formatter:on
   }

}
