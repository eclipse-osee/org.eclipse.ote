/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.core;

import java.io.Serializable;

/**
 * @author Andrew M. Finkbeiner
 */
public class ReturnStatus implements Serializable {

   private static final long serialVersionUID = -7774073812320127561L;

   private final boolean status;
   private final boolean unauthorizedUser;
   private final String message;

   public ReturnStatus(String message, boolean status, boolean unauthorizedUser) {
      this.status = status;
      this.message = message;
      this.unauthorizedUser = unauthorizedUser;
   }

   public boolean getStatus() {
      return status;
   }

   public String getMessage() {
      return message;
   }
   
   public boolean isUnauthorizedUser() {
      return unauthorizedUser;
   }
}
