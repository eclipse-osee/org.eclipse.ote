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

import java.util.Objects;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ote.cat.plugin.CatPlugin;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * The {@link IStatus} implementation class used when reporting the {@link CatPluginException} to the
 * {@link StatusManager}.
 */

class CatPluginStatus extends Status {

   /**
    * Save the linked children {@link IStatus} objects. <code>null</code> is a sentinel value to indicate that this
    * status does not have a child.
    */

   private final IStatus[] children;

   /**
    * Creates a new {@link CatPluginStatus}.
    *
    * @param catErrorCode the {@link CatErrorCode} providing a unique reference code for the error, the
    * {@link StatusManager} display hints, and the {@link IStatus} severity.
    * @param message a detailed message describing the exception. When <code>null</code> the message
    * {@value #undescribedMessage} will be used.
    */

   public CatPluginStatus(CatErrorCode catErrorCode, String message) {
      super(catErrorCode.getSeverity(), CatPlugin.getIdentifier(), catErrorCode.getStatusCode(),
         CatPluginException.buildMessage(catErrorCode, message), null);
      this.children = null;
   }

   /**
    * Creates a new {@link CatPluginStatus}.
    *
    * @param catErrorCode the {@link CatErrorCode} providing a unique reference code for the error, the
    * {@link StatusManager} display hints, and the {@link IStatus} severity.
    * @param message a detailed message describing the exception. When <code>null</code> the message
    * {@value #undescribedMessage} will be used.
    * @param exception the exception this {@link IStatus} is being created for.
    */

   public CatPluginStatus(CatErrorCode catErrorCode, String message, Exception e) {
      super(catErrorCode.getSeverity(), CatPlugin.getIdentifier(), catErrorCode.getStatusCode(),
         CatPluginException.buildMessage(catErrorCode, message), e);
      this.children = null;
   }

   /**
    * Creates a new {@link CatPluginStatus}.
    *
    * @param catErrorCode the {@link CatErrorCode} providing a unique reference code for the error, the
    * {@link StatusManager} display hints, and the {@link IStatus} severity.
    * @param message a detailed message describing the exception. When <code>null</code> the message
    * {@value #undescribedMessage} will be used.
    * @param childStatus the {@link IStatus} to attach as a child. This parameter may be <code>null</code>.
    */

   public CatPluginStatus(CatErrorCode catErrorCode, String message, IStatus childStatus) {
      super(catErrorCode.getSeverity(), CatPlugin.getIdentifier(), catErrorCode.getStatusCode(),
         CatPluginException.buildMessage(catErrorCode, message), null);
      //@formatter:off
         this.children =
            Objects.nonNull(childStatus)
               ? new IStatus[] { childStatus }
               : null;
      //@formatter:on
      if (Objects.nonNull(this.children)) {
         int maxSeverity = catErrorCode.getSeverity();
         for (IStatus child : this.children) {
            maxSeverity = Math.max(maxSeverity, child.getSeverity());
         }
         this.setSeverity(maxSeverity);
      }
   }

   /**
    * Creates a new {@link CatPluginStatus}.
    *
    * @param catErrorCode the {@link CatErrorCode} providing a unique reference code for the error, the
    * {@link StatusManager} display hints, and the {@link IStatus} severity.
    * @param childStatus the {@link IStatus} to attach as a child. This parameter may be <code>null</code>.
    * @param exception the exception this {@link IStatus} is being created for.
    */

   public CatPluginStatus(CatErrorCode catErrorCode, IStatus childStatus, Exception exception) {
      super(catErrorCode.getSeverity(), CatPlugin.getIdentifier(), catErrorCode.getStatusCode(), exception.getMessage(),
         exception);
      //@formatter:off
         this.children =
            Objects.nonNull(childStatus)
               ? new IStatus[] { childStatus }
               : null;
      //@formatter:on
      if (Objects.nonNull(this.children)) {
         int maxSeverity = catErrorCode.getSeverity();
         for (IStatus child : this.children) {
            maxSeverity = Math.max(maxSeverity, child.getSeverity());
         }
         this.setSeverity(maxSeverity);
      }
   }

   /**
    * Creates a new {@link CatPluginStatus}.
    *
    * @param catErrorCode the {@link CatErrorCode} providing a unique reference code for the error, the
    * {@link StatusManager} display hints, and the {@link IStatus} severity.
    * @param message a detailed message describing the exception. When <code>null</code> the message
    * {@value #undescribedMessage} will be used.
    * @param children an array of child {@link IStatus} objects to attach as a children. This parameter may be
    * <code>null</code>.
    */

   public CatPluginStatus(CatErrorCode catErrorCode, String message, IStatus[] children) {
      super(catErrorCode.getSeverity(), CatPlugin.getIdentifier(), catErrorCode.getStatusCode(),
         CatPluginException.buildMessage(catErrorCode, message), null);
      //@formatter:off
         this.children =
            Objects.nonNull(children)
               ? children
               : null;
      //@formatter:on
      if (Objects.nonNull(this.children)) {
         int maxSeverity = catErrorCode.getSeverity();
         for (IStatus child : this.children) {
            maxSeverity = Math.max(maxSeverity, child.getSeverity());
         }
         this.setSeverity(maxSeverity);
      }

   }

   /**
    * Creates a new {@link CatPluginStatus}.
    *
    * @param catErrorCode the {@link CatErrorCode} providing a unique reference code for the error, the
    * {@link StatusManager} display hints, and the {@link IStatus} severity.
    * @param children an array of child {@link IStatus} objects to attach as a children. This parameter may be
    * <code>null</code>.
    * @param excetion the exception this {@link IStatus} is being created for.
    */

   public CatPluginStatus(CatErrorCode catErrorCode, IStatus[] children, Exception exception) {
      super(catErrorCode.getSeverity(), CatPlugin.getIdentifier(), catErrorCode.getStatusCode(), exception.getMessage(),
         exception);
      //@formatter:off
         this.children =
            Objects.nonNull(children)
               ? children
               : null;
      //@formatter:on
      if (Objects.nonNull(this.children)) {
         int maxSeverity = catErrorCode.getSeverity();
         for (IStatus child : this.children) {
            maxSeverity = Math.max(maxSeverity, child.getSeverity());
         }
         this.setSeverity(maxSeverity);
      }
   }

   /**
    * Gets the {@link IStatus} code as a {@link CatErrorCode}.
    * 
    * @return the {@link CatErrorCode} associated with the {@link IStatus} code.
    */

   public CatErrorCode getCatErrorCode() {
      int code = this.getCode();
      CatErrorCode catErrorCode = CatErrorCode.getByStatusCode(code);
      return catErrorCode;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public IStatus[] getChildren() {
      if (Objects.nonNull(this.children)) {
         return this.children;
      }
      return new IStatus[0];
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public boolean isMultiStatus() {
      return Objects.nonNull(this.children);
   }
}
