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

package org.eclipse.ote.io.pio;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A {@link RuntimeException} thrown by classes in the {@link org.eclipse.ote.io.pio} package for detected or wrapped
 * checked {@link Exception}s.
 * 
 * @author Loren K. Ashley
 */

public class PIOException extends RuntimeException {

   /**
    * Serialization identifier.
    */

   private static final long serialVersionUID = -1655812754723206018L;

   /**
    * Creates a new {@link PIOException} with the provided {@code message}.
    * 
    * @param message the exception message.
    */

   public PIOException(@Nullable String message) {
      super(message);
   }

   /**
    * Creates a new {@link PIOException} with the provided {@code message} and {@code cause}.
    * 
    * @param message the exception message.
    * @param cause the exception that caused this exception.
    */

   public PIOException(@Nullable String message, @Nullable Throwable cause) {
      super(message, cause);
   }

}
