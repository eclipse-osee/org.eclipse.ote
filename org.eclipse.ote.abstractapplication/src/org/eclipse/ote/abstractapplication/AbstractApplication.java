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

import java.util.Arrays;
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ote.tools.util.Message;

/**
 * An application super class that provides a command line parameter processor.
 * 
 * @author Loren K. Ashley
 */

public abstract class AbstractApplication implements Application {

   /**
    * Saves the application short name for exception messages.
    */

   private final @NonNull String shortName;

   /**
    * Saves the application full descriptive name.
    */

   private final @NonNull String name;

   /**
    * Saves the application description for the help message
    */

   private final @NonNull String description;

   /**
    * Flag set {@code true} by the command line processor when the help command line option is specified.
    */

   protected boolean helpFlag;

   /**
    * Saves the application's {@link CommandLineParser}.
    */

   protected final @NonNull CommandLineParser commandLineParser;

   /**
    * Initializes the {@link AbstractApplication} with descriptive names for the application and creates the command
    * line parser.
    * 
    * @param shortName a short name for the application used in exception messages.
    * @param name a full descriptive name for the application used in help and usage messages.
    * @param description the application description used in help and usage messages.
    * @param commandLineParameters zero or more {@link CommandLineParameterDefinition}s for the application.
    * @throws NullPointerException when either of <code>shortName</code>, <code>name</code>, or <code>description</code>
    * are <code>null</code>.
    */

   protected AbstractApplication(@NonNull String shortName, @NonNull String name, @NonNull String description, @Nullable CommandLineParameterDefinition... commandLineParameters) {

      this.shortName = Objects.requireNonNull(shortName);
      this.name = Objects.requireNonNull(name);
      this.description = Objects.requireNonNull(description);
      this.helpFlag = false;
      this.commandLineParser = new CommandLineParser(commandLineParameters);

      //@formatter:off
      this.commandLineParser
         .add
            (
               new CommandLineParameterFlag
                      (
                         "?",
                         "help",
                         "Prints command line option help and exits.",
                         ( application, flag ) ->
                            ((AbstractApplication) application).helpFlag = flag
                      )
            );
      //@formatter:on
   }

   /**
    * {@inheritDoc}
    * 
    * @throws ApplicationSetupException when an error occurred processing the command line arguments.
    * @throws NullPointerException when {@code args} is {@code null} or contains a {@code null} entry.
    */

   @Override
   public void go(@NonNull String[] args) {

      Objects.requireNonNull(args);
      Arrays.stream(args).forEach(Objects::requireNonNull);

      StringBuilder commandLineParseErrorMessage = new StringBuilder(1 * 1024);

      boolean commandLineParseOk = this.commandLineParser.parse(this, args, commandLineParseErrorMessage);

      if (this.helpFlag) {
         this.applicationHelp();
         return;
      }

      if (!commandLineParseOk) {
         throw new ApplicationSetupException(this, commandLineParseErrorMessage.toString());
      }

      this.run();
   }

   /**
    * Prints the application usage message to stderr.
    */

   void applicationHelp() {
      //@formatter:off
      Message outMessage = new Message();
      
      outMessage
         .title( this.name )
         .blank()
         .block( this.description )
         .blank()
         .block( this.commandLineParser.optionsHelp() )
         ;

      System.err.println( outMessage.toString() );
      //@formatter:on
   }

   /**
    * Extending classes must implement a {@link #run} method with the application logic. This method is called after
    * successful processing of the application's command line arguments.
    */

   public abstract void run();

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull String getName() {
      return this.name;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public @NonNull String getShortName() {
      return this.shortName;
   }
}

/* EOF */
