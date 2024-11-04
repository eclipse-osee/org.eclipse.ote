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
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorSupportProvider;
import org.eclipse.ote.cat.plugin.composites.CatBrowserErrorSupport;
import org.eclipse.ote.cat.plugin.composites.CatMessageErrorSupport;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Creates a {@link Control} to display the CAT Plug-In user support area on {@link StatusManager} dialogs. An instance
 * of this class is registered with the {@link StatusManager} by the CAT Plug-In activator.
 * 
 * @author Loren K. Ashley
 */

public class CatErrorSupportProvider extends ErrorSupportProvider {

   /**
    * Exception title string used for non-CAT Plug-In status and exceptions.
    */

   private static final String defaultExceptionTitle = "CAT Plug-In Internal Error";

   /**
    * Creates a {@link Control} to be displayed in the user support area of a {@link StatusManager} error dialog for
    * non-{@link CatPluginException.CatPluginStatus} statuses.
    * 
    * @param parent the {@link Composite} of the {@link StatusManager} error dialog for the user support area.
    * @param status the non-{@link CatPluginException.CatPluginStatus} to create a general support message for.
    * @return a {@link Control} to be displayed in the user support area of an error dialog.
    */

   private static Control createGeneralSupportArea(Composite parent, IStatus status) {
      String message = status.getMessage();
      Throwable exception = status.getException();
      String trace = null;
      if (Objects.nonNull(exception)) {
         String exceptionMessage = exception.getMessage();
         //@formatter:off
         message = new StringBuilder( message.length() + exceptionMessage.length() + 128 )
                          .append( message ).append( "\n" )
                          .append( "\n" )
                          .append( exceptionMessage )
                          .toString();
         //@formatter:on
         StringWriter stringWriter = new StringWriter();
         PrintWriter printWriter = new PrintWriter(stringWriter);
         exception.printStackTrace(printWriter);
         trace = stringWriter.toString();
      }
      return new CatMessageErrorSupport(parent, CatErrorSupportProvider.defaultExceptionTitle, message, trace);
   }

   /**
    * Creates a {@link Control} to be displayed in the user support area of a {@link StatusManager} error dialog
    * according to the <code>status</code> class as follows:
    * <dl style="margin-left:2em;">
    * <dt>When <code>status</code> is an instance of {@link CatPluginException.CatPluginStatus}:</dt>
    * <dd>A {@link CatBrowserErrorSupport} {@link Control} is created for {@link CatErrorCode.Type#UserError}s and a
    * {@link CatMessageErrorSupport} {@link Control} is created for {@link CatErrorCode.Type#InternalError}s.</dd>
    * <dt>When <code>status</code> is not an instance of {@link CatPluginException.CatPluginStatus}:</dt>
    * <dd>If the exception chain for the <code>status</code> contains a {@link CatPluginException} a {@link Control} is
    * created for the {@link CatPluginException.CatPluginStatus} contained in the exception chain as described above.
    * Otherwise, the method {@link #createGeneralSupportArea} is used to create the {@link Control}.</dd>
    * </dl>
    * 
    * @param parent the {@link Composite} of the {@link StatusManager} error dialog for the user support area.
    * @param status the {@link IStatus} to create user support area {@link Control} for.
    * @return a {@link Control} to be displayed in the user support area of an error dialog.
    */

   private static Control createSupportAreaControl(Composite parent, IStatus status) {

      if (!(status instanceof CatPluginStatus)) {

         Optional<CatPluginException> catPluginExceptionOptional =
            CatErrorSupportProvider.getCatPluginException(status);

         if (!catPluginExceptionOptional.isPresent()) {
            return CatErrorSupportProvider.createGeneralSupportArea(parent, status);
         }

         CatPluginException catPluginException = catPluginExceptionOptional.get();
         status = catPluginException.getStatus();
      }

      CatPluginStatus catPluginStatus = (CatPluginStatus) status;
      CatErrorCode catErrorCode = catPluginStatus.getCatErrorCode();
      CatErrorCode.Type catErrorType = catErrorCode.getType();

      switch (catErrorType) {

         case UserError:
            Optional<URL> urlOptional = catErrorCode.getErrorSupportUrl();
            if (urlOptional.isPresent()) {
               return new CatBrowserErrorSupport(parent, urlOptional.get());
            }
            //case fall through to create an internal error control.

         case InternalError:
            CatPluginException catPluginException = (CatPluginException) catPluginStatus.getException();
            String title = catPluginException.getTitle();
            StringBuilder stringBuilder = new StringBuilder(1024);
            stringBuilder.append(status.getMessage()).append("\n\n");
            IStatus[] children = status.getChildren();
            for (IStatus child : children) {
               stringBuilder.append(child.getMessage()).append("\n\n");
            }
            String message = stringBuilder.toString();
            String trace = catPluginException.getTrace();
            return new CatMessageErrorSupport(parent, title, message, trace);
      }

      return CatErrorSupportProvider.createGeneralSupportArea(parent, catPluginStatus);

   }

   /**
    * Looks for and extracts the {@link CatPluginException} associated with the {@link IStatus}. When the
    * {@link IStatus} contains a {@link CatPluginException} it is returned. When the {@link IStatus} contains an
    * exception that is not a {@link CatPluginException}, the first {@link CatPluginException} in the exception cause
    * chain is returned.
    * 
    * @param status the {@link IStatus} to extract a {@link CatPluginException} from.
    * @return an {@link Optional} containing a {@link CatPluginException} when one is found; otherwise, an empty
    * {@link Optional}.
    */

   private static Optional<CatPluginException> getCatPluginException(IStatus status) {
      if (status instanceof CatPluginStatus) {
         return Optional.of((CatPluginException) status.getException());
      }
      for (Throwable exception = status.getException(); Objects.nonNull(exception); exception = exception.getCause()) {
         if (exception instanceof CatPluginException) {
            return Optional.of((CatPluginException) exception);
         }
      }
      return Optional.empty();
   }

   /**
    * Creates the {@link ErrorSupportProvider} for the CAT Plug-In.
    */

   public CatErrorSupportProvider() {
   }

   /**
    * Creates the {@link Control} for the user support area on {@link StatusManager} dialogs for the CAT Plug-In.
    * <p>
    * {@inheritDoc}
    */

   @Override
   public Control createSupportArea(Composite parent, IStatus status) {
      return CatErrorSupportProvider.createSupportAreaControl(parent, status);
   }

}
