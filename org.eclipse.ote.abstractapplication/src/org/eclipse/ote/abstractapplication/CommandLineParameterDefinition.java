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
import java.util.List;
import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ote.tools.util.Message;
import org.eclipse.ote.tools.util.ToMessage;

/**
 * This class provides the basic definition for a command line parameter.
 *
 * @author Loren K. Ashley
 */

public class CommandLineParameterDefinition implements ToMessage {

   /**
    * Valid sums of option, environment, and property priorities for the number of priorities specified above zero.
    */

   private static final int[] v = {0, 1, 3, 6};

   /**
    * Saves the environment variable this parameter may be read from. This member will be {@code null} when this
    * parameter does not have an environment variable defined that it's value can be read from.
    */

   private final @Nullable String environmentVariable;

   /**
    * Saves the long command line option string. This member may be {@code null} when this parameter is read from an
    * environment variable or property and a command line option is not defined.
    */

   private final @Nullable String longOption;

   /**
    * Specifies whether the command line parameter is required or optional.
    */

   private final @NonNull ParameterType parameterType;

   /**
    * Specifies whether the command line parameter has a required or optional value; or if it does not have a value.
    */

   private final @NonNull ParameterValueType parameterValueType;

   /**
    * A list in priority order of the command line parameter value sources (command line, environment, property).
    */

   private final @NonNull List<OptionType> priorityList;

   /**
    * Saves the {@link CommandLineParameterProcessor} used to process this command line parameter.
    */

   private final @NonNull CommandLineParameterProcessor processor;

   /**
    * Saves the property name this parameter may be read from. This member will be {@code null} when this parameter does
    * not have a property defined that it's value can be read from.
    */

   private final @Nullable String property;

   /**
    * Saves the short command line option string. This member may be {@code null} when this parameter is read from an
    * environment variable or property and a command line option is not defined.
    */

   private final @Nullable String shortOption;

   /**
    * Saves a description of the command line option.
    */

   private final @NonNull String synopsis;

   /**
    * Creates a new command line parameter definition.
    * 
    * @param shortOption (NonNull) the single letter for a short command line option. Short options are started with a
    * single dash ( -o ).
    * @param longOption (NonNull) the word for a long command line options. Long options are started with a double dash
    * ( --orange ).
    * @param parameterValueType set to indicate whether the command line option has an optional parameter, required
    * parameter, or no parameter.
    * @param parameterType <code>true</code> to indicate the presence of this command line parameter is required.
    * @param synopsis a description of the command line parameter for use in help and usages messages.
    * @param environmentVariable the name of an environment variable to obtain the parameter value from.
    * @param property the name of a Java property to obtain the parameter value from.
    * @param priority is an integer composed of 2 bit priority fields for:
    * <dl style="margin-left:2em;">
    * <dt>Property Priority</dt>
    * <dd>Bits 4-5</dd>
    * <dt>Environment Priority</dt>
    * <dd>Bits 2-3</dd>
    * <dt>Command Line Priority</dt>
    * <dd>Bits 0-1</dd>
    * </dl>
    * Field values:
    * <dl style="margin-left:2em;">
    * <dt>0</dt>
    * <dd>The option type is not queried for a value.</dd>
    * <dt>1</dt>
    * <dd>The option type is queried first for a value.</dd>
    * <dt>2</dt>
    * <dd>The option type is queried second for a value.</dd>
    * <dt>3</dt>
    * <dd>The option type is queried last for a value.</dd>
    * </dl>
    * Any of the bit fields may be set to zero. Only one bit field may be set to each of the values 1, 2, and 3.
    * @param processor an implementation of the {@link CommandLineParameterProcessor} interface used to process the
    * parameter.
    * @throws NullPointerException when any of the parameters:
    * <ul>
    * <li>{@code parameterValueType},</li>
    * <li>{@code parameterType},</li>
    * <li>{@code synopsis}, or</li>
    * <li>{@code processor}</li>
    * </ul>
    * are {@code null}.
    * @throws IllegalArgumentException when:
    * <ul>
    * <li>Bits 0-1 of {@code priority} are non-zero; and {@code shortOption} or {@code longOption} are
    * {@code null}.</li>
    * <li>Bits 0-1 of {@code priority} are zero; and {@code shortOption} or {@code longOption} are
    * non-{@code null}.</li>
    * <li>Bits 2-3 of {@code priority} are non-zero and {@code environmentVariable} is {@code null}.</li>
    * <li>Bits 2-3 of {@code priority} are zero and {@code environmentVariable} is non-{@code null}.</li>
    * <li>Bits 4-5 of {@code priority} are non-zero and {@code property} is {@code null}.</li>
    * <li>Bits 4-5 of {@code priority} are zero and {@code property} is non-{@code null}.</li>
    */

   //@formatter:off
   CommandLineParameterDefinition
      (
         @Nullable String                       shortOption,
         @Nullable String                       longOption, 
         @NonNull  ParameterValueType           parameterValueType, 
         @NonNull  ParameterType                parameterType,           
         @NonNull  String                       synopsis, 
         @Nullable String                       environmentVariable, 
         @Nullable String                       property, 
                   int                          priority, 
         @NonNull CommandLineParameterProcessor processor
      ) {

      this.parameterValueType = Objects.requireNonNull( parameterValueType );
      this.parameterType = Objects.requireNonNull( parameterType );
      this.synopsis = Objects.requireNonNull( synopsis );
      this.processor = Objects.requireNonNull(processor);
      
      int optionPriority      = ( priority & 0b000011 );
      int environmentPriority = ( priority & 0b001100 ) >> 2;
      int propertyPriority    = ( priority & 0b110000 ) >> 4;
      
      int count = 
           ( optionPriority      > 0 ? 1 : 0 )
         + ( environmentPriority > 0 ? 1 : 0 )
         + ( propertyPriority    > 0 ? 1 : 0 );
      
      int prioritySum = optionPriority + environmentPriority + propertyPriority;
      int priorityAnd = optionPriority & environmentPriority & propertyPriority;
      
      if( ( v[count] != prioritySum ) || ( priorityAnd != 0 ) ) {
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "CommandLineParameterDefinition::new, invalid priority specification." )
                             .indentInc()
                             .segment( "Option Priority",      optionPriority      )
                             .segment( "Environment Priority", environmentPriority )
                             .segment( "Property Priority",    propertyPriority    )
                             .toString()
                   );
      }

      this.priorityList = new ArrayList<>(count);
      
      if (optionPriority > 0) {
         this.priorityList.add(optionPriority - 1, OptionType.COMMAND_LINE);
      }
      if (environmentPriority > 0) {
         this.priorityList.add(environmentPriority - 1, OptionType.ENVIRONMENT);
      }
      if (propertyPriority > 0) {
         this.priorityList.add(propertyPriority - 1, OptionType.PROPERTY);
      }

      try {
         this.shortOption = this.priorityKeyCheck(optionPriority, shortOption, OptionType.COMMAND_LINE );
         this.longOption = this.priorityKeyCheck(optionPriority, longOption, OptionType.COMMAND_LINE );
         this.environmentVariable = this.priorityKeyCheck(environmentPriority, environmentVariable, OptionType.ENVIRONMENT );
         this.property = this.priorityKeyCheck(propertyPriority, property, OptionType.PROPERTY );
      } catch (Exception e ) {
         
         throw 
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "Priority and Key combination is invalid for CommandLineParameterDefinition." )
                             .indentInc()
                             .segment( "Synopsis", synopsis )
                             .reasonFollows( e )
                             .toString(),
                      e
                   );
      }
      
   }
   //@formatter:on

   /**
    * Returns whether the command line option may have a parameter value.
    * 
    * @return {@code true} when the command line option does not have parameter value; otherwise, {@code false} when the
    * command line option may have a parameter value.
    */

   boolean doesNotHaveParameterValue() {
      return this.parameterValueType.isNone();
   }

   /**
    * Gets the command line option's long option
    * 
    * @return the long option name
    */

   @Nullable
   String getLongOption() {
      return this.longOption;
   }

   /**
    * Gets the command line option's short option
    * 
    * @return the short option
    */

   @Nullable
   String getShortOption() {
      return this.shortOption;
   }

   /**
    * Gets a description for the command line option
    * 
    * @return the synopsis
    */

   @NonNull
   String getSynopsis() {
      return this.synopsis;
   }

   /**
    * Returns whether the command line option parameter value is optional.
    * 
    * @return {@code true} when the command line option parameter value is optional; otherwise, {@code false}.
    */

   boolean hasOptionalParameterValue() {
      return this.parameterValueType.isOptional();
   }

   /**
    * Returns whether the command line option parameter value is required.
    * 
    * @return {@code true} when the command line option parameter value is required; otherwise, {@code false}.
    */

   boolean hasRequiredParameter() {
      return this.parameterValueType.isRequired();
   }

   /**
    * Returns whether the command line option is required.
    * 
    * @return {@code true} when required; otherwise, {@code false}.
    */

   boolean isRequired() {
      return this.parameterType.isRequired();
   }

   /**
    * Returns whether the command line option may have a parameter value.
    * 
    * @return {@code true} when the command line option parameter value is optional or required; otherwise,
    * {@code false}.
    */

   boolean mayHaveParameter() {
      return this.parameterValueType.isOptionalOrIsRequired();
   }

   /**
    * When a priority is greater than zero the key must be non-{@code null} and when the priority is zero the key must
    * be {@code null}.
    * 
    * @param priority the priority value for an option type
    * @param key the option, environment, or property name associated with the priority value.
    * @param optionType the option type the priority and key values are for.
    * @return the {@code key}.
    * @throws IllegalArgumentException when
    * <ul>
    * <li>the priority is non-zero and the key is {@code null}, or</li>
    * <li>the priority is zero and the key is non-{@code null}.</li>
    * </ul>
    */

   private String priorityKeyCheck(int priority, @Nullable String key, @Nullable OptionType optionType) {
      //@formatter:off
      if (priority > 0) {
         if (key == null) {
            throw 
               new IllegalArgumentException
                      (
                          new Message()
                                 .title( "Key must be non-null when Priority is greater than zero." )
                                 .indentInc()
                                 .segmentIfNotNull("Option Type", optionType::name, optionType, "(unknown)" )
                                 .segment("Priority", priority)
                                 .segment("Key", key)
                                 .toString()
                      );
         }
      } else {
         if (key != null) {
            throw
               new IllegalArgumentException
                      (
                         new Message()
                                .title( "Key must be null when Priority is zero." )
                                .indentInc()
                                .segmentIfNotNull( "Option Type", optionType::name, optionType, "(unknown)" )
                                .segment( "Priority", priority )
                                .segment( "Key", key )
                                .toString()
                      );
         }
      }
      //@formatter:on
      return key;
   }

   /**
    * Runs the command line option's processor.
    * 
    * @param application a reference to the application.
    * @param commandLineParameter the {@link CommandLineParameterToken} for the command line option to be processed.
    * @param message a {@link Message} that command line processing errors can be appended to.
    * @return {@code true} when the command line option was successfully processed; otherwise, {@code false}.
    */

   boolean process(@NonNull Application application, @NonNull CommandLineParameterToken commandLineParameter, @NonNull StringBuilder message) {

      Objects.requireNonNull(application);
      Objects.requireNonNull(commandLineParameter);
      Objects.requireNonNull(message);

      return this.processor.process(application, commandLineParameter, message);
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
         .title( "CommandLineParameter" )
         .indentInc()
         .segment( "Short Option",           this.shortOption         )
         .segment( "Long Option",            this.longOption          )
         .segment( "Option Parameter Type",  this.parameterValueType  )
         .segment( "Is Required",            this.parameterType       )
         .segment( "Environment Variable",   this.environmentVariable )
         .segment( "Property",               this.property            )
         .segment( "Synopsis",               this.synopsis            )
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
