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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.eclipse.jdt.annotation.NonNull;

/**
 * An extension of the class {@link CommandLineParameterDefinition} for defining a command line parameter for a file.
 * 
 * @author Loren K. Ashley
 */

public class CommandLineParameterFile extends CommandLineParameterDefinition {

   /**
    * Creates a new {@link CommandLineParameterDefinition} for a file command line parameter.
    * <p>
    * The {@link CommandLineParameterProcessor} defined by this class will report an error when:
    * <ul>
    * <li>A value for the command line parameter was not obtained.</li>
    * <li>When {@link mustExist} is {@code true} and the file specified for the command line parameter does not
    * exist.</li>
    * </ul>
    * The {@code biConsuer} will be called with the path to the file when all error checks pass.
    * 
    * @param shortOption the command line option short string.
    * @param longOption the command line option long string.
    * @param parameterType specifies whether the command line parameter is optional or required.
    * @param synopsis a description for the command line parameter.
    * @param mustExist when {@code true} the specified file must exist.
    * @param biConsumer a {@link BiConsumer} call back to the {@link Application} to set the command line parameter
    * value.
    * @throws NullPointerException when any of {@code shortOption}, {@code longOption}, {@code parameterType},
    * {@code synopsis}, or {@code biConsumer} are {@code null}.
    */

   //@formatter:off
   public
      CommandLineParameterFile
         (
           @NonNull String                       shortOption,
           @NonNull String                       longOption,
           @NonNull ParameterType                parameterType,
           @NonNull String                       synopsis,
                    boolean                      mustExist,
           @NonNull BiConsumer<Application,Path> biConsumer
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
              CommandLineParameterDefinition commandLineParameter                    = commandLineParameterToken.getCommandLineParameter().get();
              Optional<CommandLineParameterToken> linkedCommandLineParameterTokenOptional = commandLineParameterToken.getLinkedCommandLineParameterToken();

              if( linkedCommandLineParameterTokenOptional.isPresent() ) {

                 CommandLineParameterToken linkedCommandLineParameterToken = linkedCommandLineParameterTokenOptional.get();
                 String parameterString               = linkedCommandLineParameterToken.getToken();
                 Path filePath                        = Paths.get( parameterString );

                 if ( mustExist && !filePath.toFile().isFile()) {

                    message
                       .append( "ERROR: Path to file is invalid." ).append( "\n" )
                       .append( "   File Path:    " ).append( filePath ).append("\n")
                       .append( "   Short Option: ").append( commandLineParameter.getShortOption() ).append( "\n" )
                       .append( "   Long Option:   ").append( commandLineParameter.getLongOption() ).append( "\n" )
                       ;

                    return false;
                 }

                 biConsumer.accept(application, filePath);

                 return true;

              } else {

                 message
                    .append( "ERROR: Command line parameter value is missing." ).append("\n")
                    .append( "   Short Option:   ").append( commandLineParameter.getShortOption() ).append( "\n" )
                    .append( "   Long Option:    ").append( commandLineParameter.getLongOption() ).append( "\n" );

                  return false;
              }
           }
         );
      
      Objects.requireNonNull( biConsumer );
   }
   //@formatter:on
}

/* EOF */
