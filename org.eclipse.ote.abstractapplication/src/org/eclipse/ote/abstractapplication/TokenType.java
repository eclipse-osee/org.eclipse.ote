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

import java.util.function.BiFunction;

/**
 * Enumeration used to classify command line tokens as options, values, or errors.
 * 
 * @author Loren K. Ashley
 */

public enum TokenType {

   /**
    * Command line arguments that are determined to be a long or short command line option are classified as
    * {@link #OPTION}.
    */

   OPTION(CommandLineParameterToken::new),

   /**
    * Command line arguments that are determined to be a value associated with an option are classified as
    * {@link VALUE}.
    */

   VALUE(CommandLineParameterToken::new),

   /**
    * Command line arguments that cannot be properly classified as an {@link OPTION} or {@link VALUE} are classified as
    * {@link ERROR}.
    */

   ERROR(CommandLineParameterToken::new);

   /**
    * Saves the factory method used to create {@link CommadLineParameterToken}s for command line arguments of the type
    * associated with the enumeration member.
    */

   private BiFunction<TokenType, String, CommandLineParameterToken> factory;

   /**
    * Creates a new {@link TokenType} enumeration member and saves the {@link CommandLineParameterToken} factory used to
    * create the tokens for command line arguments of the type associated with the enumeration member.
    * 
    * @param factory the method used to create a {@link CommandLineParameterToken} for a command line option of the type
    * associated with the enumeration member.
    */

   private TokenType(BiFunction<TokenType, String, CommandLineParameterToken> factory) {
      this.factory = factory;
   }

   /**
    * Creates a new {@link CommandLineParameterToken} from the command line argument ({@code token}).
    * 
    * @param token the command line argument.
    * @return a {@Link CommandLineParameterToken} created from the command line argument.
    */

   public CommandLineParameterToken create(String token) {
      return this.factory.apply(this, token);
   }

   /**
    * Creates a new {@link CommandLineParameterToken} with an associated {@link CommandLineParameterDefinition}.
    * 
    * @param commandLineParameterDefinition the definition of the command line parameter.
    * @param token the command line argument.
    * @return a {@link CommandLineParameterToken} created from the command line argument with the associated
    * {@link CommandLineParameterDefinition}.
    */

   public CommandLineParameterToken create(CommandLineParameterDefinition commandLineParameterDefinition, String token) {
      CommandLineParameterToken commandLineParameterToken = this.factory.apply(this, token);
      commandLineParameterToken.setCommandLineParameterDefinition(commandLineParameterDefinition);
      return commandLineParameterToken;
   }

}
