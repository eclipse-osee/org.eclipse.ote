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


public class OTEStatusCallbackForTests<T> implements OTEStatusCallback<ConfigurationStatus> {

   @Override
   public void complete(ConfigurationStatus done) {
      System.out.println("done");
   }

   @Override
   public void setTotalUnitsOfWork(int totalUnitsOfWork) {
      System.out.println("units " + totalUnitsOfWork);
   }

   @Override
   public void incrememtUnitsWorked(int count) {
      System.out.println("units " + count);
   }

   @Override
   public void log(String string) {
      System.out.println(string);
   }

   @Override
   public void error(String message, Throwable th) {
      System.out.println(message);
      th.printStackTrace();
   }

   @Override
   public void error(String message) {
      System.out.println(message);
   }

}
