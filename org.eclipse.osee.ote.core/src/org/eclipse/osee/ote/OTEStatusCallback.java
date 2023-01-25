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


public interface OTEStatusCallback<V> {

   void complete(V done);
   
   void setTotalUnitsOfWork(int totalUnitsOfWork);
   
   void incrememtUnitsWorked(int count);

   void log(String message);

   void error(String message, Throwable th);
   
   void error(String message);
   
}
