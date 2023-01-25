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
package org.eclipse.osee.ote.master.rest.internal;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class OTEMasterApplication implements IApplication {

   @Override
   public Object start(IApplicationContext context) throws Exception {
      Object obj = new Object();
      synchronized (obj) {
         obj.wait();
      }
      return IApplication.EXIT_OK;
   }

   @Override
   public void stop() {

   }
   
}
