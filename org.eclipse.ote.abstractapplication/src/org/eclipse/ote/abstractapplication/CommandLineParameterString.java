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

package org.eclipse.ote.abstractapplication;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.eclipse.jdt.annotation.NonNull;

/**
 * An extension of the class {@link CommandLineParameterDefinition} for defining a command line parameter with a string
 * parameter value.
 *
 * @author Loren K. Ashley
 */

public class CommandLineParameterString extends CommandLineParameterDefinition {

   /**
    * Creates a new {@link CommandLineParameterDefinition} for a string command line parameter.
    * <p>
    * The {@link CommandLineParameterProcessor} defined by this class will report an error when:
    * <ul>
    * <li>A value for the command line parameter was not obtained.</li>
    * </ul>
    * The {@code BiConsumer} will be called with the {@link String}.
    *
    * @param shortOption the command line option short string.
    * @param longOption the command line option long string.
    * @param parameterType specifies whether the command line parameter is optional or required.
    * @param synopsis a description for the command line parameter.
    * @param mustBeNonEmpty when {@code true} the specified string must not be an empty string.
    * @param biConsumer a {@link BiConsumer} call back to the {@link Application} to set the command line parameter
    * value.
    * @throws NullPointerException when any of {@code shortOption}, {@code longOption}, {@code parameterType},
    * {@code synopsis}, or {@code biConsumer} are {@code null}.
    */

   //@formatter:off
   public CommandLineParameterString
      (
        @NonNull String                          shortOption,
        @NonNull String                          longOption,
        @NonNull ParameterType                   parameterType,
        @NonNull String                          synopsis,
                 boolean                         mustBeNonEmpty,
        @NonNull BiConsumer<Application,String>  biConsumer
      )
   {

      super
         (
           shortOption,
           longOption,
           ParameterValueType.REQUIRED,
           parameterType,
           synopsis,
           null,
           null,
           1,
           ( application, commandLineParameterToken, message ) ->
           {
              CommandLineParameterDefinition commandLineParameter = commandLineParameterToken.getCommandLineParameter().get();
              Optional<CommandLineParameterToken> linkedCommandLineParameterTokenOptional = commandLineParameterToken.getLinkedCommandLineParameterToken();

              if( !linkedCommandLineParameterTokenOptional.isPresent() )
              {
                 message
                 .append( "ERROR: Command line parameter value is missing." ).append( "\n" )
                 .append( "   Short Option:   " ).append( commandLineParameter.getShortOption() ).append( "\n" )
                 .append( "   Long Option:    " ).append( commandLineParameter.getLongOption()  ).append( "\n" )
                 ;

                 return false;
              }

              CommandLineParameterToken linkedCommandLineParameterToken = linkedCommandLineParameterTokenOptional.get();
              String parameterString = linkedCommandLineParameterToken.getToken();

              if (parameterString.isEmpty()) {
                 message
                    .append( "ERROR: Command line parameter value is specified but empty." ).append("\n")
                    .append( "   Short Option:   " ).append( commandLineParameter.getShortOption() ).append("\n")
                    .append( "   Long Option:    " ).append( commandLineParameter.getLongOption() ).append("\n");

                 return false;
              }

              String value = parameterString;

              biConsumer.accept( application, value );

              return true;
           }
      );

      Objects.requireNonNull( biConsumer );
   }
   //@formatter:on
}

/* EOF */
