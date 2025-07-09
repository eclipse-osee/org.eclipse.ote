/*********************************************************************
 * Copyright (c) 2024 Boeing
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.io.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * A wrapper on a stream {@link JsonParser} that uses a map of {@link EventFunction} by {@link JsonToken}s to process
 * the JSON token stream. JSON tokens for which there is no entry in the map are silently skipped.
 * 
 * @author Loren K. Ashley
 */

public class JsonInputStreamParser {

   /**
    * The functional interface for the function that is called to process a JSON token of a specific type.
    */

   @FunctionalInterface
   public interface EventFunction {

      /**
       * The implementations of this functional interface are used to process a JSON token of a specific type. The
       * parsed JSON data needs to be captured and save via side-effect by the {@link EventFunction} methods.
       * 
       * @see com.fasterxml.jackson.core.JsonToken
       * @param context provides the current parsing context.
       * @return <code>true</code> when parsing should terminate; otherwise, <code>false</code> when parsing should
       * continue.
       */

      Boolean apply(JsonInputStreamParser.Context context);

   }

   /**
    * Object passed to the {@link EventFunction} methods. This class provides the {@link JsonStreamContext} and access
    * methods to obtain the token value.
    */

   public class Context {

      /**
       * Tracks the number of JSON tokens that have been read from the stream.
       */

      private int tokenCount;

      /**
       * Saves the {@link JsonParser} used to process the JSON stream.
       */

      private @NonNull JsonParser jsonParser;

      /**
       * Creates a new {@link Context} with an initial token count of 0 and the <code>jsonParser</code>.
       * 
       * @param jsonParser the {@link JsonParser} being used to parse the stream.
       * @throws NullPointerException when {@code jsonParser} is {@code null}.
       */

      Context(@NonNull JsonParser jsonParser) {
         this.tokenCount = 0;
         this.jsonParser = Objects.requireNonNull(jsonParser);
      }

      /**
       * Called from the main parser loop to increment the count of JSON tokens read from the JSON stream.
       */

      void incrementTokenCount() {
         this.tokenCount++;
      }

      /**
       * Gets the number of JSON tokens that have been read from the JSON stream.
       * 
       * @return the read token count.
       */

      public int getTokenCount() {
         return this.tokenCount;
      }

      /**
       * Method that can be used by {@link EventFunction}s to obtain the stream context.
       * 
       * @return the current {@link JsonStreamContext}.
       */

      public @Nullable JsonStreamContext getContext() {
         return this.jsonParser.getParsingContext();
      }

      /**
       * When the current token is a {@link JsonToken.VALUE_TRUE} or {@link JsonToken.VALUE_FALSE} gets the token value.
       * 
       * @return an {@link Optional} with the token value when the current token is {@link JsonToken.VALUE_TRUE} or
       * {@link JsonToken.VALUE_FALSE}; otherwise, an empty {@link Optional}.
       */

      public Optional<Boolean> getValueAsBoolean() {
         try {
            //@formatter:off
            return 
                  this.jsonParser.hasToken( JsonToken.VALUE_TRUE  )
               || this.jsonParser.hasToken( JsonToken.VALUE_FALSE )
                  ? Optional.ofNullable( this.jsonParser.getValueAsBoolean() ) 
                  : Optional.empty();
            //@formatter:on
         } catch (Exception e) {
            return Optional.empty();
         }
      }

      /**
       * When the current token is a {@link JsonToken.VALUE_NUMBER_INT} or {@link JsonToken.VALUE_NUMBER_FLOAT} gets the
       * token value.
       * 
       * @return an {@link Optional} with the token value when the current token is {@link JsonToken.VALUE_NUMBER_INT}
       * or {@link JsonToken.VALUE_NUMBER_FLOAT}; otherwise, an empty {@link Optional}.
       */

      public Optional<Double> getValueAsDouble() {
         try {
            //@formatter:off
            return 
                  this.jsonParser.hasToken( JsonToken.VALUE_NUMBER_INT   )
               || this.jsonParser.hasToken( JsonToken.VALUE_NUMBER_FLOAT )
                  ? Optional.ofNullable(this.jsonParser.getValueAsDouble()) 
                  : Optional.empty();
            //@formatter:on
         } catch (Exception e) {
            return Optional.empty();
         }
      }

      /**
       * When the current token is a {@link JsonToken.VALUE_NUMBER_INT} gets the token value.
       * 
       * @return an {@link Optional} with the token value when the current token is {@link JsonToken.VALUE_NUMBER_INT};
       * otherwise, an empty {@link Optional}.
       */

      public Optional<Long> getValueAsLong() {
         try {
            //@formatter:off
            return 
               this.jsonParser.hasToken(JsonToken.VALUE_NUMBER_INT)
                  ? Optional.ofNullable(this.jsonParser.getValueAsLong())
                  : Optional.empty();
            //@formatter:on
         } catch (Exception e) {
            return Optional.empty();
         }
      }

      /**
       * When the current token is a {@link JsonToken.VALUE_STRING} gets the token value.
       * 
       * @return an {@link Optional} with the token value when the current token is {@link JsonToken.VALUE_STRING};
       * otherwise, an empty {@link Optional}.
       */

      public Optional<String> getValueAsString() {
         try {
            //@formatter:off
            return 
               this.jsonParser.hasToken(JsonToken.VALUE_STRING)
                  ? Optional.ofNullable(this.jsonParser.getValueAsString())
                  : Optional.empty();
            //@formatter:on
         } catch (Exception e) {
            return Optional.empty();
         }
      }
   }

   /**
    * Saves the {@link JsonToken} to {@link EventFunction} association map. This map may be modified during parsing.
    */

   private final @NonNull Map<@NonNull JsonToken, @NonNull EventFunction> eventMap;

   /**
    * Creates a new parser with the provided map of {@link JsonToken} to {@link EventFunction} associations. The map may
    * be modified by the {@link EventFunction}s during parsing.
    * 
    * @param eventMap (NonNull) a map of the {@link EventFunction}s to be called for each {@link JsonToken}.
    * @throws NullPointerException when <code>eventMap</code> is <code>null</code>.
    */

   public JsonInputStreamParser(@NonNull Map<@NonNull JsonToken, @NonNull EventFunction> eventMap) {
      this.eventMap = Objects.requireNonNull(eventMap);
   }

   /**
    * Parses the JSON <code>inputStream</code> into tokens and processes each token with the associated
    * {@link EventFunction}. Tokens that do not have a defined {@link EventFunction} will be silently skipped.
    * 
    * @param inputStream an {@link InputStream} to read the JSON data from. The <code>inputStream</code> will be closed
    * by the parser upon successful or unsuccessful completion.
    * @throws NullPointerException when <code>inputStream</code> is <code>null</code>.
    * @throws JsonInputStreamParserException when a parsing error occurs.
    */

   public void parse(@NonNull InputStream inputStream) {

      Objects.requireNonNull(inputStream);

      JsonFactory jsonFactory = new JsonFactory();
      jsonFactory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
      Context context = null;

      try (JsonParser jsonParser = jsonFactory.createParser(inputStream)) {

         context = new Context(jsonParser);

         JsonToken jsonToken;

         while ((jsonToken = jsonParser.nextToken()) != null) {

            context.incrementTokenCount();

            EventFunction eventFunction = this.eventMap.get(jsonToken);

            if (Objects.nonNull(eventFunction)) {
               final Boolean done = eventFunction.apply(context);
               if (done) {
                  return;
               }
            }
         }

      } catch (Exception e) {
         //@formatter:off
         throw 
            new JsonInputStreamParserException
                   (
                      "parse(InputStream)",
                      "Failed to parse JSON input stream.",
                      context,
                      e
                   );
         //@formatter:on
      }
   }

   /**
    * Parses the JSON <code>inputString</code> into tokens and processes each token with the associated
    * {@link EventFunction}. Tokens that do not have a defined {@link EventFunction} will be silently skipped.
    * 
    * @param inputString a {@link String} to read the JSON data from.
    * @throws NullPointerException when <code>inputString</code> is <code>null</code>.
    * @throws JsonInputStreamParserException when a parsing error occurs.
    */

   public void parse(@NonNull String inputString) {

      Objects.requireNonNull(inputString);

      JsonFactory jsonFactory = new JsonFactory();
      jsonFactory.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
      Context context = null;

      try (JsonParser jsonParser = jsonFactory.createParser(inputString)) {

         context = new Context(jsonParser);

         JsonToken jsonToken;

         while ((jsonToken = jsonParser.nextToken()) != null) {

            context.incrementTokenCount();

            EventFunction eventFunction = this.eventMap.get(jsonToken);

            if (Objects.nonNull(eventFunction)) {
               final Boolean done = eventFunction.apply(context);
               if (done) {
                  return;
               }
            }
         }

      } catch (Exception e) {
         //@formatter:off
         throw 
            new JsonInputStreamParserException
                   (
                      "parse(String)",
                      "Failed to parse JSON input stream.",
                      context,
                      e
                   );
         //@formatter:on
      }
   }

}
