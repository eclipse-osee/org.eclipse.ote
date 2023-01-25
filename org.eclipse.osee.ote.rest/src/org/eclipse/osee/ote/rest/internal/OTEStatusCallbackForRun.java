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
package org.eclipse.osee.ote.rest.internal;

import org.eclipse.osee.ote.ConfigurationStatus;
import org.eclipse.osee.ote.OTEStatusCallback;

public class OTEStatusCallbackForRun<T> implements OTEStatusCallback<ConfigurationStatus> {

   @Override
   public void complete(ConfigurationStatus done) {
      // TODO Auto-generated method stub

   }

   @Override
   public void setTotalUnitsOfWork(int totalUnitsOfWork) {
      // TODO Auto-generated method stub

   }

   @Override
   public void incrememtUnitsWorked(int count) {
      // TODO Auto-generated method stub

   }

   @Override
   public void log(String message) {
      // TODO Auto-generated method stub

   }

   @Override
   public void error(String message, Throwable th) {
      // TODO Auto-generated method stub

   }

   @Override
   public void error(String message) {
      // TODO Auto-generated method stub

   }

}
