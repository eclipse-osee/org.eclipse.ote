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

package org.eclipse.ote.io.json;

import java.util.Objects;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ote.io.json.JsonInputStreamParser.Context;
import org.eclipse.ote.tools.util.Message;

/**
 * An exception thrown by the {@link JsonInputStreamParser} when parsing fails.
 * 
 * @author Loren K. Ashley
 */

public class JsonInputStreamParserException extends RuntimeException {

   /**
    * Serialization identifier
    */

   private static final long serialVersionUID = -4011457302529447986L;

   /**
    * The number of the token in the stream where the failure occurred. When a token count is not available this member
    * will be -1.
    */

   private int tokenCount;

   /**
    * Creates a new runtime {@link JsonInputStreamParserException}.
    * 
    * @param methodName the name of the method where the exception occurred. When {@code null} the method name will be
    * omitted from the exception method.
    * @param title a title string for the exception method. When {@code null} the string "Failed to parse JSON." will be
    * used.
    * @param context the parsing context used to obtain the token number in the JSON stream where the error occurred.
    * When {@link null} the token count is reported as -1.
    * @param cause the exception that caused the exception being reported. When non-{@code null}{ the {@code cause} is
    * reported in the exception message and the {@code cause} is also set as the exception,s cause.
    */

   public JsonInputStreamParserException(@Nullable String methodName, @Nullable String title, @Nullable Context context, @Nullable Throwable cause) {
      super(JsonInputStreamParserException.buildMessage(methodName, title, context, cause), cause);
      this.tokenCount = Objects.nonNull(context) ? context.getTokenCount() : -1;
   }

   /**
    * Creates a new runtime {@link JsonInputStreamParserException}.
    * 
    * @param methodName the name of the method where the exception occurred. When {@code null} the method name will be
    * omitted from the exception method.
    * @param title a title string for the exception method. When {@code null} the string "Failed to parse JSON." will be
    * used.
    * @param context the parsing context used to obtain the token number in the JSON stream where the error occurred.
    * When {@link null} the token count is reported as -1.
    */

   public JsonInputStreamParserException(@Nullable String methodName, @Nullable String title, @Nullable Context context) {
      this(methodName, title, context, null);
   }

   /**
    * Gets the number of the JSON token in the input stream where the parse error occurred.
    * 
    * @return the number of the token where the error occurred; otherwise, -1.
    */

   public int getTokenCount() {
      return this.tokenCount;
   }

   /**
    * Creates the exception message from the reporting method name, a title string, the JSON parsing context, and the
    * causing exception.
    * 
    * @param methodName the name of the method throwing the exception.
    * @param title a title string for the exception.
    * @param context the parsing context.
    * @param cause a causing exception.
    * @return the message string for the exception.
    */
   private static @NonNull String buildMessage(@Nullable String methodName, @Nullable String title, @Nullable Context context, @Nullable Throwable cause) {
      //@formatter:off
      String exceptionTitle =
         "JsonInputSreamParser"
         + ( Objects.nonNull( methodName ) ? "::" + methodName : "" )
         + ": "
         + ( Objects.nonNull( title ) ? title : "Failed to parse JSON." )
         ;
         
      return 
         new Message()
                .title( exceptionTitle )
                .indentInc()
                .segmentIfNotNull( "Token Count", context::getTokenCount, context )
                .reasonFollowsIfNonNull( cause )
                .toString();
      //@formatter:on
   }
}
