/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import org.eclipse.ote.tools.util.Message;
import org.eclipse.ote.tools.util.ToMessage;

/**
 * Each command line argument is wrapped in an instance of this class to provide access to the following:
 * <ul>
 * <li>The {@link CommandLineParameterDefinition} for {@link TokenType#OPTION} arguments.</li>
 * <li>An error message accumulator for errors processing the wrapped argument.</li>
 * <li>For {@link TokenType#OPTION} arguments with an associated value, a link to the {@link CommandLineParameterToken}
 * containting the wrapped argument's value.</li>
 * <li>The {@link TokenType} classification of the argument.</li>
 * <li>The argument string.</li>
 * </ul>
 * 
 * @author Loren K. Ashely
 */

public class CommandLineParameterToken implements ToMessage {

   private CommandLineParameterDefinition commandLineParameterDefinition;
   private StringBuilder errorMessage;
   private CommandLineParameterToken linkedCommandLineParameterToken;
   private TokenType tokenType;
   private final String token;

   /**
    * Creates a new {@link CommandLineParameterToken} and sets the {@link TokenType} and the token string.
    *
    * @param tokenType the type of command line parameter token
    * @param token the command line argument token
    * @throws NullPointerException when <code>tokenType</code> or <code>token</code> are <code>null</code>.
    */

   CommandLineParameterToken(TokenType tokenType, String token) {
      this.tokenType = Objects.requireNonNull(tokenType);
      this.token = Objects.requireNonNull(token);

      this.commandLineParameterDefinition = null;
      this.errorMessage = null;
      this.linkedCommandLineParameterToken = null;
   }

   /**
    * Gets the {@CommandLineParameter} associated with the command line token.
    *
    * @return when a {@link CommandLineParameterDefinition} is present, {@link Optional} containing the
    * {@link CommandLineParameterDefinition}; otherwise, an empty {@link Optional}.
    */

   Optional<CommandLineParameterDefinition> getCommandLineParameter() {
      return Optional.ofNullable(commandLineParameterDefinition);
   }

   /**
    * Gets the error message that was set for the {@link CommandLineParameterToken} if an error message was set;
    * otherwise, an error message stating a request for the error message was made for a token without an error.
    *
    * @return {@link StringBuilder} containg an error message.
    */

   StringBuilder getErrorMessage() {
      //@formatter:off
      return 
         this.errorMessage != null 
            ? this.errorMessage 
            : new StringBuilder( 1 * 1024 )
                     .append( "ERROR: Error requested for token without an error." )
                     .append( "\n" );
      //@formatter:on
   }

   /**
    * Gets the {@link CommandLineParameterToken} that has been linked to this {@link CommandLineParameterToken}. The
    * {@link CommandLineParameterToken} for an option value is linked to the {@link CommandLineParameterToken} for the
    * command line option.
    *
    * @return if a {@link CommandLineParameterToken} is linked, an {@link Optional} containing the linked
    * {@link CommandLineParameterToken}; otherwise, and empty {@link Optional} is returned.
    */

   Optional<CommandLineParameterToken> getLinkedCommandLineParameterToken() {
      return Optional.ofNullable(linkedCommandLineParameterToken);
   }

   /**
    * Gets the type of command line parameter token represented.
    *
    * @return the {@link TokenType} of this {@link CommandLineParameterToken}.
    */

   TokenType getTokenType() {
      return this.tokenType;
   }

   /**
    * Gets the command line parameter token.
    *
    * @return the command line parameter token (argument).
    */

   String getToken() {
      return this.token;
   }

   /**
    * Predicate to determine if the token type is {@link TokenType#ERROR}.
    *
    * @return <code>true</code>, when the token type is {@link TokenType#ERROR}; otherwise, <code>false</code>.
    */

   boolean isError() {
      return this.tokenType == TokenType.ERROR;
   }

   /**
    * Predicate to determine if the token type is {@link TokenType#OPTION}.
    *
    * @return <code>true</code>, when the token type is {@link TokenType#OPTION}; otherwise, <code>false</code>.
    */

   boolean isOption() {
      return this.tokenType == TokenType.OPTION;
   }

   /**
    * Sets the {@link CommandLineParameterDefinition} of the token.
    * 
    * @param commandLineParameterDefinition the {@link CommandLineParameterdefinition} that matches the command line
    * argument.
    */

   void setCommandLineParameterDefinition(CommandLineParameterDefinition commandLineParameterDefinition) {

      assert Objects.nonNull(commandLineParameterDefinition) && Objects.isNull(this.commandLineParameterDefinition);

      this.commandLineParameterDefinition = commandLineParameterDefinition;
   }

   /**
    * Appends an error message to the error accumulator for this token.
    * 
    * @param errorMessage the error message string to be appended.
    */

   void setError(StringBuilder errorMessage) {
      this.errorMessage = Objects.requireNonNull(errorMessage);
      this.tokenType = TokenType.ERROR;
   }

   /**
    * Links the {@link CommandLineParameterToken} of the value argument associated with an option argument.
    * 
    * @param linkedCommandLineParameterToken the {@link CommandLineParameterToken} to be linked.
    */

   void setLinkedCommandLineParameterToken(CommandLineParameterToken linkedCommandLineParameterToken) {

      assert Objects.isNull(this.linkedCommandLineParameterToken);

      this.linkedCommandLineParameterToken = Objects.requireNonNull(linkedCommandLineParameterToken);
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
         .title( "CommandLineParameterToken" )
         .indentInc()
         .segment( "Token Type",                          this.tokenType                                                        )
         .segment( "Token",                               this.token                                                            )
         .segment( "CommandLineParameter",                commandLineParameterDefinition,            CommandLineParameterDefinition::getShortOption )
         .segment( "Linked Command Line Parameter Token", linkedCommandLineParameterToken, CommandLineParameterToken::getToken  )
         .segment( "Error Messge",                        this.errorMessage                                                     )
         .indentDec()
         ;

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