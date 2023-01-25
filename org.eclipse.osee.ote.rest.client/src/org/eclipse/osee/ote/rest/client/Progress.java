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
package org.eclipse.osee.ote.rest.client;

public interface Progress {

   void fail(String string);
   void fail(Throwable th);

   void setUnitsOfWork(int totalUnitsOfWork);
   void setUnitsWorked(int unitsWorked);
   void success();
}
