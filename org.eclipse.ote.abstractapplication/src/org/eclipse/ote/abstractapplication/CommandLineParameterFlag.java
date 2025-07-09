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

package org.eclipse.ote.abstractapplication;

import java.util.Objects;
import java.util.function.BiConsumer;
import org.eclipse.jdt.annotation.NonNull;

/**
 * An extension of the class {@link CommandLineParameterDefinition} for defining an optional command line parameter flag
 * without any parameters.
 *
 * @author Loren K. Ashley
 */

public class CommandLineParameterFlag extends CommandLineParameterDefinition {

   /**
    * Creates a new {@link CommandLineParameterDefinition} for an optional flag command line parameter.
    * <p>
    * The {@code biConsuer} will be called with the value of {@code true} when the command line parameter is present.
    * When the flag is not present the {@code biConsumer} will not be called.
    * 
    * @param shortOption the command line option short string.
    * @param longOption the command line option long string.
    * @param synopsis a description for the command line parameter.
    * @param biConsumer a {@link BiConsumer} call back to the {@link Application} to set the command line parameter
    * value.
    * @throws NullPointerException when any of {@code shortOption}, {@code longOption}, {@code synopsis}, or
    * {@code biConsumer} are {@code null}.
    */

   //@formatter:off
   public
      CommandLineParameterFlag
         (
            @NonNull String                          shortOption,
            @NonNull String                          longOption,
            @NonNull String                          synopsis,
            @NonNull BiConsumer<Application,Boolean> biConsumer
         )
   {
      super
         (
            shortOption,
            longOption,
            ParameterValueType.NONE,
            ParameterType.OPTIONAL,
            synopsis,
            null,
            null,
            1,
            ( application, commandLineParamterToken, message ) ->
            {
               biConsumer.accept( application, true );

               return true;
            }
         );
      
      Objects.requireNonNull(biConsumer);
   }
   //@formatter:on
}

/* EOF */
