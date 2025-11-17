/*
/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - Initial API and implementation
 **********************************************************************/

package org.eclipse.ote.tools.util.function;

import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the function Functional Interfaces.
 *
 * @author Loren K. Ashley
 */

public class FunctionsTest {

   /*
    * TriConsumer Test
    */

   @Test
   public void testTriConsumer() {

      StringBuilder stringBuilder = new StringBuilder(1024);

      TriConsumer<String, String, String> base = (a, b, c) -> stringBuilder.append(a).append(b).append(c);
      TriConsumer<String, String, String> after =
         (a, b, c) -> stringBuilder.append("-after-").append(a).append(b).append(c);

      TriConsumer<String, String, String> combined = base.andThen(after);

      combined.accept("a", "b", "c");

      String result = stringBuilder.toString();

      Assert.assertEquals("abc-after-abc", result);

   }

   /*
    * TriFunction Test
    */

   @Test
   public void testTriFunction() {

      TriFunction<String, String, String, String> base = (a, b, c) -> a + b + c;
      Function<String, String> after = (a) -> a + "-after-" + a;

      TriFunction<String, String, String, String> combined = base.andThen(after);

      String result = combined.apply("a", "b", "c");

      Assert.assertEquals("abc-after-abc", result);

   }

}

/* EOF */
