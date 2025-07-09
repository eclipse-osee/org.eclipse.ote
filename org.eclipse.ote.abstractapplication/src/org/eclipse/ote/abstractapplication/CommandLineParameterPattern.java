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
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import org.eclipse.jdt.annotation.NonNull;

/**
 * An extension of the class {@link CommandLineParameterDefinition} for defining a command line parameter for a regular
 * expression.
 * 
 * @author Loren K. Ashley
 */

public class CommandLineParameterPattern extends CommandLineParameterDefinition {

   /**
    * Creates a new {@link CommandLineParameterDefinition} for a regular expression command line parameter.
    * <p>
    * The {@link CommandLineParameterProcessor} defined by this class will report an error when:
    * <ul>
    * <li>A value for the command line parameter was not obtained.</li>
    * <li>The parameter value fails to compile as a regular expression {@link Pattern}.</li>
    * </ul>
    * The {@code biConsuer} will be called with the {@link Pattern} segment.
    * 
    * @param shortOption the command line option short string.
    * @param longOption the command line option long string.
    * @param parameterType specifies whether the command line parameter is optional or required.
    * @param synopsis a description for the command line parameter.
    * @param biConsumer a {@link BiConsumer} call back to the {@link Application} to set the command line parameter
    * value.
    * @throws NullPointerException when any of {@code shortOption}, {@code longOption}, {@code parameterType},
    * {@code synopsis}, or {@code biConsumer} are {@code null}.
    */

   //@formatter:off
   public 
      CommandLineParameterPattern
         (
            @NonNull String                           shortOption,
            @NonNull String                           longOption, 
            @NonNull ParameterType                    parameterType, 
            @NonNull String                           synopsis,
            @NonNull BiConsumer<Application, Pattern> biConsumer
         ) {
      
      super
         (
            shortOption, 
            longOption,
            ParameterValueType.REQUIRED,
            parameterType,
            synopsis,
            null,
            null,
            0,
            (application, commandLineParameterToken, message) ->
            {
               CommandLineParameterDefinition commandLineParameter =
                  commandLineParameterToken.getCommandLineParameter().get();
               Optional<CommandLineParameterToken> linkedCommandLineParameterTokenOptional =
                  commandLineParameterToken.getLinkedCommandLineParameterToken();
               
               if (linkedCommandLineParameterTokenOptional.isPresent()) {
                  CommandLineParameterToken linkedCommandLineParameterToken =
                     linkedCommandLineParameterTokenOptional.get();
                  String parameterString = linkedCommandLineParameterToken.getToken();
               
                  try {
                     Pattern pattern = Pattern.compile(parameterString);
               
                     biConsumer.accept(application, pattern);
               
                     return true;
                  } catch (Exception e) {
                     message
                        .append( "ERROR: Pattern failed to compile." ).append("\n")
                        .append( "   Pattern:      " ).append( parameterString ).append("\n")
                        .append( "   Short Option: " ).append( commandLineParameter.getShortOption() ).append("\n")
                        .append( "   Long Option:  " ).append( commandLineParameter.getLongOption() ).append("\n");
               
                     return false;
                  }
               } else {
                  message.append("ERROR: Command line parameter value is missing.").append("\n").append(
                     "   Short Option:   ").append(commandLineParameter.getShortOption()).append("\n").append(
                        "   Long Option:    ").append(commandLineParameter.getLongOption()).append("\n");
               
                  return false;
               }
            }
         );
      
      Objects.requireNonNull( biConsumer );
   }
   //@formatter:on
}

/* EOF */
