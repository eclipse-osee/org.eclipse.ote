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

package org.eclipse.ote.cat.plugin.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * The {@link RuntimeException} class CAT Plug-In exceptions are derived from.
 * 
 * @author Loren K. Ashley
 */

public class CatPluginException extends RuntimeException {

   /**
    * The introduction used for internal exception messages.
    */

   //@formatter:off
   private static final String internalErrorMessageHeader =
      "Please forward the error details to your support team.\n\n";
   //@formatter:on

   /**
    * For internal error type, prepends the exception message with an introduction requesting the user to contact the
    * support team.
    * 
    * @param catErrorCode the exception {@link CatErrorCode} used to determine if the error type is
    * {@link CatErrorCode.Type#InternalError}.
    * @param message the exception message.
    * @return the exception message with introduction.
    */

   static String buildMessage(CatErrorCode catErrorCode, String message) {

      String safeMessage = Objects.nonNull(message) ? message : CatPluginException.undescribedMessage;

      if (catErrorCode.isInternalError()) {
         //@formatter:off
         return
            new StringBuilder( message.length() + internalErrorMessageHeader.length() )
                   .append( internalErrorMessageHeader )
                   .append( message )
                   .toString();
         //@formatter:on
      }

      return safeMessage;
   }

   /**
    * The error dialog title to use when a title was not provided for the {@link CatPluginException}.
    */

   private static final String defaultTitle = "CAT Plug-in Error";

   /**
    * A sentinel value used to indicate that a cause exception is not available.
    */

   public static final Throwable noCause = null;

   /**
    * The serialization version identifier;
    */

   private static final long serialVersionUID = 0L;

   /**
    * The exception message used with a message was not provided for the {@link CatPluginException}.
    */

   private static final String undescribedMessage = "Undescribed Exception";

   /**
    * Gets a non-<code>null</code> title.
    * 
    * @param unsafeTitle the title provided to the {@link CatPluginException} which may be <code>null</code>.
    * @return <code>unsafeTitle</code> when <code>unsafeTitle</code> is non-<code>null</code>; otherwise,
    * {@value #defaultTitle}.
    */

   private static String getSafeTitle(String unsafeTitle) {
      return Objects.nonNull(unsafeTitle) ? unsafeTitle : CatPluginException.defaultTitle;
   }

   /**
    * Saves the {@link IStatus} implementation created from the constructor parameters. This is the {@link IStatus}
    * object passed to the {@link StatusManager} by the {@link #log} method.
    */

   private final CatPluginStatus status;

   /**
    * Saves the {@link StatusManager} error dialog style bits. See {@link StatusManager#BLOCK},
    * {@link StatusManager#SHOW}, and {@link StatusManager@LOG}.
    */

   private int style;

   /**
    * Saves the title to be used for the error dialog.
    */

   private final String title;

   /**
    * Creates a new runtime {@link CatPluginException}.
    * 
    * @param catErrorCode the {@link CatErrorCode} providing a unique reference code for the error, the
    * {@link StatusManager} display hints, and the {@link IStatus} severity.
    * @param message a detailed message describing the exception. When <code>null</code> the message
    * {@value #undescribedMessage} will be used.
    */

   public CatPluginException(CatErrorCode catErrorCode, String message) {
      super(CatPluginException.buildMessage(catErrorCode, message));

      this.status = new CatPluginStatus(catErrorCode, (IStatus) null, this);
      this.style = catErrorCode.getStyle();
      this.title = CatPluginException.getSafeTitle(catErrorCode.getTitle());
   }

   /**
    * Creates a new runtime {@link CatPluginException}. The <code>cause</code> {@link IStatus} object will be linked as
    * the child for the {@link IStatus} created for this exception.
    * 
    * @param catErrorCode the {@link CatErrorCode} providing a unique reference code for the error, the
    * {@link StatusManager} display hints, and the {@link IStatus} severity.
    * @param message a detailed message describing the exception. When <code>null</code> the message
    * {@value #undescribedMessage} will be used.
    * @param cause the {@link IStatus} the exception is being built for. This parameter may be <code>null</code>.
    */

   public CatPluginException(CatErrorCode catErrorCode, String message, IStatus cause) {
      super(CatPluginException.buildMessage(catErrorCode, message));

      this.status = new CatPluginStatus(catErrorCode, cause, this);
      this.style = catErrorCode.getStyle();
      this.title = CatPluginException.getSafeTitle(catErrorCode.getTitle());
   }

   /**
    * Creates a new runtime {@link CatPluginException}. The <code>cause</code> {@link IStatus} object will be linked as
    * the child for the {@link IStatus} created for this exception.
    * 
    * @param catErrorCode the {@link CatErrorCode} providing a unique reference code for the error, the
    * {@link StatusManager} display hints, and the {@link IStatus} severity.
    * @param message a detailed message describing the exception. When <code>null</code> the message
    * {@value #undescribedMessage} will be used.
    * @param cause an array of {@link IStatus} objects the exception is being built for. This parameter may be
    * <code>null</code>.
    */

   public CatPluginException(CatErrorCode catErrorCode, String title, String message, IStatus[] cause) {
      super(CatPluginException.buildMessage(catErrorCode, message));

      this.status = new CatPluginStatus(catErrorCode, cause, this);
      this.style = catErrorCode.getStyle();
      this.title = CatPluginException.getSafeTitle(catErrorCode.getTitle());
   }

   /**
    * Creates a new runtime {@link CatPluginException}. When the <code>cause</code> exception is either a
    * {@link CatPluginException} or a {@link CoreException}, the {@link IStatus} object from the <code>cause</code>
    * exception will be linked as the child for the {@link IStatus} created for this exception.
    * 
    * @param catErrorCode the {@link CatErrorCode} providing a unique reference code for the error, the
    * {@link StatusManager} display hints, and the {@link IStatus} severity.
    * @param message a detailed message describing the exception. When <code>null</code> the message
    * {@value #undescribedMessage} will be used.
    * @param cause the causing exception. This parameter may be <code>null</code>.
    */

   public CatPluginException(CatErrorCode catErrorCode, String message, Throwable cause) {
      super(CatPluginException.buildMessage(catErrorCode, message));
      if (Objects.nonNull(cause)) {
         this.initCause(cause);
      }
      //@formatter:off
      IStatus childStatus =
         Objects.isNull(cause)
            ? null
            : (cause instanceof CatPluginException)
                 ? ((CatPluginException) cause).status
                 : (cause instanceof CoreException)
                      ? ((CoreException) cause).getStatus()
                      : null;
      //@formatter:on
      this.status = new CatPluginStatus(catErrorCode, childStatus, this);
      this.style = catErrorCode.getStyle();
      this.title = CatPluginException.getSafeTitle(catErrorCode.getTitle());
   }

   /**
    * Gets the {@link CatErrorCode} associated with the exception's {@link CatPluginStatus}.
    * 
    * @return the {@link CatErrorCode} for the exception.
    */

   public CatErrorCode getCatErrorCode() {
      return this.status.getCatErrorCode();
   }

   /**
    * Gets the {@link CatPluginStatus} the {@link CatPluginException} was created for or the {@link CatPluginStatus}
    * created from the {@link CatPluginException}.
    * 
    * @return the {@link CatPluginStatus} associated with the exception.
    */

   public CatPluginStatus getStatus() {
      return this.status;
   }

   /**
    * Gets the exception title.
    * 
    * @return title string.
    */

   public String getTitle() {
      return this.title;
   }

   /**
    * Generates a stack trace string for the exception.
    * 
    * @return the exception trace.
    */

   public String getTrace() {
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      this.printStackTrace(printWriter);
      return stringWriter.toString();
   }

   /**
    * Logs the {@link #status} with the {@link StatusManager}.
    */

   public void log() {

      StatusAdapter statusAdapter = new StatusAdapter(this.status);
      statusAdapter.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, this.title);
      statusAdapter.setProperty(IStatusAdapterConstants.TIMESTAMP_PROPERTY, System.currentTimeMillis());
      StatusManager.getManager().handle(statusAdapter, this.style);
   }

}
