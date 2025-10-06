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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ote.tools.util.Message;
import org.eclipse.ote.tools.util.ToMessage;

/**
 * Parses command line parameters, environment variables, and properties according to the command line parameter
 * definitions provided to the constructor.
 *
 * @author Loren K. Ashley
 */

public class CommandLineParser implements ToMessage {

   /**
    * Saves a map of {@link CommandLineParameterDefinition} objects by the command line short option string.
    */

   private final Map<String, CommandLineParameterDefinition> shortMap;

   /**
    * Saves a map of {@link CommandLineParameterDefinition} objects by the command line long option string.
    */

   private final Map<String, CommandLineParameterDefinition> longMap;

   /**
    * Creates a new {@link CommandLineParser} with the provided <code>commandLineParameterDefinitions</code>.
    *
    * @param commandLineParameterDefinitions (Nullable) the {@link CommandLineParameterDefinition}s for the command line
    * options to be known to the parser.
    */

   public CommandLineParser(@Nullable CommandLineParameterDefinition... commandLineParameterDefinitions) {

      this.shortMap = new HashMap<>();
      this.longMap = new HashMap<>();

      if (commandLineParameterDefinitions == null) {
         return;
      }

      for (int i = 0; i < commandLineParameterDefinitions.length; i++) {

         CommandLineParameterDefinition commandLineParameterDefinition = commandLineParameterDefinitions[i];

         if (commandLineParameterDefinition == null) {
            continue;
         }

         this.shortMap.put(commandLineParameterDefinition.getShortOption(), commandLineParameterDefinition);
         this.longMap.put(commandLineParameterDefinition.getLongOption(), commandLineParameterDefinition);

      }

   }

   /**
    * Add a {@link CommandLineParameterDefinition} to the parser.
    *
    * @param commandLineParameterDefinition the {@link CommandLineParameterDefinition} to be added.
    */

   public void add(@Nullable CommandLineParameterDefinition commandLineParameterDefinition) {

      if (commandLineParameterDefinition == null) {
         return;
      }

      this.shortMap.put(commandLineParameterDefinition.getShortOption(), commandLineParameterDefinition);
      this.longMap.put(commandLineParameterDefinition.getLongOption(), commandLineParameterDefinition);
   }

   /**
    * Determines if the command line argument is an option, value, or error. A {@link CommandLineParameterToken} is
    * created for the argument and added to {@code list}.
    *
    * @param list the created {@link CommandLineParameterToken} created for the argument is appended to this list.
    * @param argument the command line argument to be classified.
    */

   private void classifyArgument(List<CommandLineParameterToken> list, String argument) {

      boolean isLongOption = argument.startsWith("--");
      boolean isShortOption = !isLongOption && argument.startsWith("-");
      boolean isValueOption = !isLongOption & !isShortOption;

      if (isValueOption) {
         //value token
         CommandLineParameterToken commandLineParameterToken = TokenType.VALUE.create(argument);
         list.add(commandLineParameterToken);
         return;
      }

      if (isShortOption) {
         //short option
         //one character short option may have a parameter, multi character short option must not have parameters

         int delimiterPosition = argument.indexOf('=', 1);

         switch (delimiterPosition) {
            case -1: /* multi-character short option, no values */
            {
               for (int i = 1; i < argument.length(); i++) {
                  String optionChar = argument.substring(i, i + 1);
                  CommandLineParameterDefinition commandLineParameterDefinition = this.shortMap.get(optionChar);
                  if (Objects.isNull(commandLineParameterDefinition)) {
                     CommandLineParameterToken commandLineParameterToken = TokenType.ERROR.create(argument);
                     list.add(commandLineParameterToken);
                  } else {
                     CommandLineParameterToken commandLineParameterToken =
                        TokenType.OPTION.create(commandLineParameterDefinition, argument);
                     list.add(commandLineParameterToken);
                  }
               }

               return;
            }

            case 2: /* Single short option with value */
            {
               String optionChar = argument.substring(1, 2);
               String parameter = argument.substring(delimiterPosition + 1);
               CommandLineParameterDefinition commandLineParameterDefinition = this.shortMap.get(optionChar);
               if (Objects.isNull(commandLineParameterDefinition)) {
                  CommandLineParameterToken commandLineParameterToken = TokenType.ERROR.create(argument);
                  list.add(commandLineParameterToken);
               } else {
                  CommandLineParameterToken commandLineParameterToken =
                     TokenType.OPTION.create(commandLineParameterDefinition, optionChar);
                  list.add(commandLineParameterToken);
                  commandLineParameterToken = TokenType.VALUE.create(parameter);
                  list.add(commandLineParameterToken);
               }
               return;
            }

            default: /* Error */
            {
               return;
            }
         }
      }

      if (isLongOption) {
         //long option

         int delimiterPosition = argument.indexOf('=', 2);
         String longArgument =
            delimiterPosition >= 0 ? argument.substring(2, delimiterPosition) : argument.substring(2);
         CommandLineParameterDefinition commandLineParameterDefinition = this.longMap.get(longArgument);

         if (Objects.isNull(commandLineParameterDefinition)) {
            CommandLineParameterToken commandLineParameterToken = TokenType.ERROR.create(argument);
            list.add(commandLineParameterToken);
            return;
         }

         CommandLineParameterToken commandLineParameterToken =
            TokenType.OPTION.create(commandLineParameterDefinition, longArgument);
         list.add(commandLineParameterToken);

         if (delimiterPosition >= 0) {
            /* Long option with value */
            String parameter = argument.substring(delimiterPosition + 1);

            commandLineParameterToken = TokenType.VALUE.create(parameter);
            list.add(commandLineParameterToken);

            return;
         }
      }
   }

   /**
    * Determines if each argument is an option, value, or error and creates a {@link CommandLineParameterToken} for
    * each.
    *
    * @param args an array of the command line arguments
    * @return a list of {@link CommandLineParameterToken}s representing the command line arguments.
    */

   private List<CommandLineParameterToken> classifyArguments(String[] args) {
      List<CommandLineParameterToken> list = new ArrayList<>(args.length);

      for (int i = 0; i < args.length; i++) {
         this.classifyArgument(list, args[i]);
      }

      return list;
   }

   /**
    * Associates the {@link CommandLineParameterToken}s for values with the option name tokens. Errors are generated for
    * options with required values where a value was not found.
    *
    * @param list the tokenized command line parameters.
    */

   private void linkParameters(List<CommandLineParameterToken> list) {

      for (int i = 0; i < list.size(); i++) {

         CommandLineParameterToken commandLineParameterToken = list.get(i);
         Optional<CommandLineParameterDefinition> commandLineParameterOptional =
            commandLineParameterToken.getCommandLineParameter();

         if (!commandLineParameterOptional.isPresent()) {
            continue;
         }

         CommandLineParameterDefinition commandLineParameter = commandLineParameterOptional.get();

         if (!commandLineParameter.mayHaveParameter()) {
            continue;
         }

         CommandLineParameterToken nextCommandLineParameterToken = list.get(i + 1);

         if (nextCommandLineParameterToken.getTokenType() == TokenType.VALUE) {
            commandLineParameterToken.setLinkedCommandLineParameterToken(nextCommandLineParameterToken);
            continue;
         }

         if (commandLineParameter.hasRequiredParameter()) {
            //@formatter:off
            StringBuilder errorMessage =
               new StringBuilder(1 * 1024)
                  .append("ERROR: Required parameter value is missing.").append("\n")
                  .append( "   Short Option: ").append(commandLineParameter.getShortOption()).append("\n")
                  .append( "   Long Option:  ").append(commandLineParameter.getLongOption()).append("\n");
            //@formatter:on
            commandLineParameterToken.setError(errorMessage);
         }
      }
   }

   /**
    * Verifies that required command line parameters were present within the command line arguments.
    *
    * @param list the tokenized command line arguments.
    * @param errorMessage error messages for missing required command line parameters are added to this
    * {@link StringBuilder}.
    * @return {@code true} when all required command line parameters are present and no other errors have been recorded;
    * otherwise, {@code false}.
    */

   private boolean checkForRequiredParameters(List<CommandLineParameterToken> list, StringBuilder errorMessage) {
      //@formatter:off
      Set<String> specifiedShortOptions =
         list.stream()
            .filter( ( token ) -> token.getTokenType().equals( TokenType.OPTION ) )
            .map( ( token ) -> token.getCommandLineParameter().orElseThrow( NoSuchElementException::new ) )
            .map( CommandLineParameterDefinition::getShortOption )
            .collect( Collectors.toSet() );
            ;

      this.shortMap.values().stream().filter(CommandLineParameterDefinition::isRequired).forEach
         (
            (requiredCommandLineParameter) ->
            {
               if (!specifiedShortOptions.contains(requiredCommandLineParameter.getShortOption())) {
                  //@formatter:off
                  errorMessage
                     .append( "ERROR: Required option is missing." ).append( "\n" )
                     .append( "   Short Option: ").append( requiredCommandLineParameter.getShortOption() ).append( "\n" )
                     .append( "   Long Option:  ").append( requiredCommandLineParameter.getLongOption()  ).append( "\n" );
                     ;
               }
            }
         );
      //@formatter:on
      return errorMessage.length() <= 0;
   }

   /**
    * Runs the command line argument processor for each option type command line argument.
    *
    * @param list a tokenized list of the command line arguments.
    * @param application the {@link Application} instance whose command line arguments are being processed.
    * @param errorMessage command line argument processing errors are appended to this {@link StringBuilder}.
    * @return {@code true} when all option command line parameter tokens are processed successfully and the {@code list}
    * does not contain any error tokens; otherwise, {@code false}.
    */

   private boolean processParameters(List<CommandLineParameterToken> list, Application application, StringBuilder errorMessage) {

      boolean rv = true;

      //@formatter:off
      for( CommandLineParameterToken commandLineParameterToken : list ) {

         if (commandLineParameterToken.isOption()) {
            rv &= commandLineParameterToken
                     .getCommandLineParameter()
                     .map( ( commandLineParameterDefinition ) -> commandLineParameterDefinition.process( application, commandLineParameterToken, errorMessage ) )
                     .get();
            continue;
         }

         if (commandLineParameterToken.isError()) {
            errorMessage.append(commandLineParameterToken.getErrorMessage());
            rv = false;
            continue;
         }

      }

      return rv;
   }

   /**
    * Parsers the command line arguments for the application.
    *
    * @param application the {@link Application} instance whose command line arguments are being parsed.
    * @param args the command line arguments passed to the application.
    * @param errorMessage an command line argument processing errors are appended to this {@link StringBuidler}.
    * @return {@code true} when parsing was successfull; otherwise, {@code false}.
    */

   public boolean parse(Application application, String[] args, StringBuilder errorMessage) {
      List<CommandLineParameterToken> list = this.classifyArguments(args);
      this.linkParameters(list);
      boolean processResult = this.processParameters(list, application, errorMessage);
      boolean checkResult = checkForRequiredParameters(list, errorMessage);
      return processResult && checkResult;
   }

   /**
    * Generates a string with a help/usage message for the application.
    *
    * @return a {@link String} containing the application help/usage message.
    */

   public String optionsHelp() {
      //@formatter:off
      Message outMessage = new Message();

      outMessage
         .title( "Command Line Options" )
         .indentInc();

      this.longMap
         .values()
         .stream()
         .sorted()
         .forEach
            (
               ( CommandLineParameterDefinition commandLineParameterDefinition ) ->
               {

                  outMessage
                     .blank()
                     .segment( "Short Option", commandLineParameterDefinition.getShortOption() )
                     .segment( "Long Option",  commandLineParameterDefinition.getLongOption()  )
                     ;

                  String required =
                     commandLineParameterDefinition.isRequired()
                        ? "This is a required command line parameter "
                        : "This is an optional command line parameter ";

                  required +=
                     commandLineParameterDefinition.doesNotHaveParameterValue()
                        ? "that does not have a value."
                        : commandLineParameterDefinition.hasOptionalParameterValue()
                             ? "that may have a value."
                             : commandLineParameterDefinition.hasRequiredParameter()
                                  ? "that requires a value."
                                  : "."
                        ;

                  outMessage
                     .title( required )
                     .blank()
                     .indentInc()
                     .title( commandLineParameterDefinition.getSynopsis() )
                     .indentDec()
                     ;
               }
            );
      //@formatter:on

      return outMessage.toString();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      Message outMessage = Objects.isNull(message) ? new Message() : message;
      //@formatter:off
      outMessage
         .indent( indent )
         .title( "CommandLineParser" )
         .indentInc()
         .segmentMap( "Command Line Parameter Definitions", this.shortMap )
         .indentDec()
         ;
      //@formatter:on
      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}

/* EOF */
