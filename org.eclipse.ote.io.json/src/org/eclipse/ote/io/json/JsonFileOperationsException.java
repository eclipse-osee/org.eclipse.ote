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

import org.eclipse.jdt.annotation.Nullable;

/**
 * A runtime exception thrown when a JSON file operation fails.
 * 
 * @author Loren K. Ashley
 */

public class JsonFileOperationsException extends RuntimeException {

   /**
    * Serialization identifier
    */

   private static final long serialVersionUID = 872483047336719233L;

   /**
    * Creates a new runtime {@link JsonFileOperationsException}.
    * 
    * @param message the exception message.
    * @param cause the exception that caused the exception being reported.
    */

   public JsonFileOperationsException(@Nullable String message, @Nullable Throwable cause) {
      super(message, cause);
   }

   /**
    * Creates a new runtime {@link JsonFileOperationsException}.
    * 
    * @param message the exception message.
    */

   public JsonFileOperationsException(@Nullable String message) {
      this(message, null);
   }

}
