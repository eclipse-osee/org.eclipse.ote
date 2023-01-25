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
package org.eclipse.osee.ote;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;

public class OseeLogStatusCallback implements OTEStatusCallback<ConfigurationStatus> {

   @Override
   public void complete(ConfigurationStatus done) {
      OseeLog.log(getClass(), Level.INFO, done.getMessage());
   }

   @Override
   public void setTotalUnitsOfWork(int totalUnitsOfWork) {
      
   }

   @Override
   public void incrememtUnitsWorked(int count) {
      
   }

   @Override
   public void log(String message) {
      OseeLog.log(getClass(), Level.INFO, message);
   }

   @Override
   public void error(String message, Throwable th) {
      OseeLog.log(getClass(), Level.SEVERE, message, th);      
   }

   @Override
   public void error(String message) {
      OseeLog.log(getClass(), Level.SEVERE, message);
   }

}
