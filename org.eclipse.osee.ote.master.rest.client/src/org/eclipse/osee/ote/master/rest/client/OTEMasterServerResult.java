/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.master.rest.client;

public class OTEMasterServerResult {

   private Throwable th = null;
   private boolean success = true;
   
   public Throwable getThrowable() {
      return th;
   }
   public void setThrowable(Throwable th) {
      this.th = th;
   }
   public boolean isSuccess() {
      return success;
   }
   public void setSuccess(boolean success) {
      this.success = success;
   }
   
}
