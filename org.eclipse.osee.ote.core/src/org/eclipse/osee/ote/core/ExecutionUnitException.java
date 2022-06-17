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

package org.eclipse.osee.ote.core;

import java.util.logging.Level;

public class ExecutionUnitException extends TestException {

   private static final long serialVersionUID = -9119275292591321042L;

   public ExecutionUnitException(String message, Level level, Throwable cause) {
      super(message, level, cause);
   }

   public ExecutionUnitException(String message, Level level) {
      super(message, level);
   }

}
