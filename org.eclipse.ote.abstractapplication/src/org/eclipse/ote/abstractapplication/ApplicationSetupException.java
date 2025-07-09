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

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ote.tools.util.Message;

/**
 * A {@link RuntimeException} for application setup errors.
 * 
 * @author Loren K. Ashley
 */

public class ApplicationSetupException extends RuntimeException {

   /**
    * Java serialization identifier.
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link ApplicationSetupException} {@link RuntimeException} for the <code>application</code> and
    * <code>reason</code>.
    * 
    * @param application used to get the application name for the exception message.
    * @param reason a description of the application setup failure.
    * @param cause the exception that caused the application setup failure.
    */

   public ApplicationSetupException(@Nullable Application application, @Nullable String reason, @Nullable Throwable cause) {
      super(ApplicationSetupException.buildMessage(application, reason), cause);
   }

   /**
    * Creates a new {@link ApplicationSetupException} {@link RuntimeException} for the <code>application</code> and
    * <code>reason</code>.
    * 
    * @param application used to get the application name for the exception message.
    * @param reason a description of the application setup failure.
    */

   public ApplicationSetupException(@Nullable Application application, @Nullable String reason) {
      this(application, reason, null);
   }

   /**
    * Builds the exception message.
    * 
    * @param application used to get the application name for the message.
    * @param reason the reason or reasons the application setup failed.
    * @return the exception message.
    */

   private static @NonNull String buildMessage(@Nullable Application application, @Nullable String reason) {
      String applicationName = (application != null) ? application.getShortName() : "(unknown-application)";
      String title = applicationName + ", failed to process the application options.";
      Message message = new Message().title(title);
      if (reason != null) {
         message.block(reason);
      }
      return message.toString();
   }
}

/* EOF */
