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

package org.eclipse.osee.ote.core.environment.status;

import java.io.Serializable;

public class EnvironmentError implements IServiceStatusData, Serializable {

   private static final long serialVersionUID = -7077313410529981519L;
   private Throwable err;

   public EnvironmentError(Throwable err) {
      this.err = err;
   }

   public EnvironmentError() {
   }

   public Throwable getErr() {
      return err;
   }

   public void set(Throwable err) {
      this.err = err;
   }

   @Override
   public void accept(IServiceStatusDataVisitor visitor) {
      if (visitor != null) {
         visitor.asEnvironmentError(this);
      }
   }
}
