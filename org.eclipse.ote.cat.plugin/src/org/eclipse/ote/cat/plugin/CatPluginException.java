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

package org.eclipse.ote.cat.plugin;

import java.util.Objects;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * The {@link RuntimeException} class used by classes in the CAT Plugin.
 * 
 * @author Loren K. Ashley
 */

public class CatPluginException extends RuntimeException {

   /**
    * The {@link IStatus} implementation class used when reporting the {@link CatPluginException} to the
    * {@link StatusManager}.
    */

   private class CatPluginStatus extends Status {

      /**
       * Save the linked child {@link IStatus} object. <code>null</code> is a sentinel value to indicate that this
       * status does not have a child.
       */

      private final IStatus child;

      /**
       * Creates a new {@link CatPluginStatus}. When the <code>cause</code> exception is a {@link CatPluginException} or
       * a {@link CoreException}, the {@link IStatus} from the <code>cause</code> exception is linked as the
       * {@link #child} of this status.
       *
       * @param severity the {@link IStatus} severity of the error. @see {@link IStatus#ERROR ERROR},
       * {@link IStatus#WARNING WARNING}, and {@link IStatus#INFO INFO}.
       * @param code the plug-in-specific status code, or <code>OK</code>
       * @param message a detailed message describing the exception. When <code>null</code> the message
       * {@value #undescribedMessage} will be used.
       * @param cause the causing exception. This parameter may be <code>null</code>.
       */

      private CatPluginStatus(int severity, int code, String message, Throwable cause) {
         super(severity, CatPlugin.getIdentifier(), code, CatPluginException.getSafeMessage(message), cause);
         //@formatter:off
         this.child =
            Objects.isNull(cause)
               ? null
               : (cause instanceof CatPluginException)
                    ? ((CatPluginException) cause).status
                    : (cause instanceof CoreException)
                         ? ((CoreException) cause).getStatus()
                         : null;
         //@formatter:on
         if (Objects.nonNull(child)) {
            int maxSeverity = Math.max(severity, child.getSeverity());
            if (maxSeverity > severity) {
               this.setSeverity(maxSeverity);
            }
         }
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public IStatus[] getChildren() {
         if (Objects.nonNull(child)) {
            IStatus[] result = new IStatus[1];
            result[0] = this.child;
            return result;
         }
         return new IStatus[0];
      }

      /**
       * {@inheritDoc}
       */

      @Override
      public boolean isMultiStatus() {
         return Objects.nonNull(this.child);
      }

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
    * Gets a non-<code>null</code> message.
    * 
    * @param unsafeMessage the message provided to the {@link CatPluginException} which may be <code>null</code>.
    * @return <code>unsafeMessage</code> when <code>unsafeMessage</code> is non-<code>null</code>; otherwise,
    * {@value #undescribedMessage}.
    */

   private static String getSafeMessage(String unsafeMessage) {
      return Objects.nonNull(unsafeMessage) ? unsafeMessage : CatPluginException.undescribedMessage;
   }

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

   private final IStatus status;

   /**
    * Saves the {@link StatusManager} style bits. @see {@link StatusManager#BLOCK BLOCK}, {@link StatusManager#SHOW
    * SHOW}, and {@link StatusManager#LOG LOG}.
    */

   private int style;

   /**
    * Saves the title to be used for the error dialog.
    */

   private final String title;

   /**
    * Creates a new runtime {@link CatPluginException}. When the <code>cause</code> exception is either a
    * {@link CatPluginException} or a {@link CoreException}, the {@link IStatus} object from the <code>cause</code>
    * exception will be linked as the child for the {@link IStatus} created for this exception.
    * 
    * @param style the {@link StatusManager} display bits. @see {@link StatusManager#BLOCK BLOCK},
    * {@link StatusManager#SHOW SHOW}, and {@link StatusManager#LOG LOG}.
    * @param title the title used for the error dialog. When <code>null</code> the default title {@value #defaultTitle}
    * will be used.
    * @param severity the {@link IStatus} severity of the error. @see {@link IStatus#ERROR ERROR},
    * {@link IStatus#WARNING WARNING}, and {@link IStatus#INFO INFO}.
    * @param message a detailed message describing the exception. When <code>null</code> the message
    * {@value #undescribedMessage} will be used.
    * @param cause the causing exception. This parameter may be <code>null</code>.
    */

   public CatPluginException(int style, String title, int severity, String message, Throwable cause) {
      super(CatPluginException.getSafeMessage(message));
      if (Objects.nonNull(cause)) {
         this.initCause(cause);
      }
      this.status = new CatPluginStatus(severity, 0, message, cause);
      this.style = style;
      this.title = CatPluginException.getSafeTitle(title);
   }

   /**
    * Logs the provided <code>status</code> with the {@link StatusManager}.
    * 
    * @param status the {@link IStatus} to be logged.
    */

   public void log() {

      StatusAdapter statusAdapter = new StatusAdapter(this.status);
      statusAdapter.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, this.title);
      statusAdapter.setProperty(IStatusAdapterConstants.TIMESTAMP_PROPERTY, System.currentTimeMillis());
      StatusManager.getManager().handle(statusAdapter, this.style);
   }

}
