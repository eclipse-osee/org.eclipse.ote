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

package org.eclipse.osee.ote.core.framework;

import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class ResultBuilder {

   private final MethodResultImpl result;
   private final boolean logToHM;

   public ResultBuilder(boolean logToHM) {
      result = new MethodResultImpl(ReturnCode.OK);
      this.logToHM = logToHM;
   }

   public MethodResultImpl append(IMethodResult result) {
      if (logToHM) {
         OseeLog.reportStatus(result.getStatus());
      }
      this.result.addStatus(result.getStatus());
      if (result.getReturnCode() != ReturnCode.OK) {
         this.result.setReturnCode(result.getReturnCode());
      }
      return this.result;
   }

   public IMethodResult get() {
      return this.result;
   }

   public boolean isReturnStatusOK() {
      return this.result.getReturnCode() == ReturnCode.OK;
   }

   @Override
   public String toString() {
      return result.toString();
   }
}
