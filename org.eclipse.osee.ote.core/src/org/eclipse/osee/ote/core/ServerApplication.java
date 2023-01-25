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
package org.eclipse.osee.ote.core;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class ServerApplication implements IApplication {

   @Override
   public Object start(IApplicationContext context) throws Exception {
      while(true){
         synchronized (this) {
            this.wait();
         }
      }
   }

   @Override
   public void stop() {
      // TODO Auto-generated method stub

   }

}
