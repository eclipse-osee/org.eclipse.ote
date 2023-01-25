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


public class OTEException extends RuntimeException {

   private static final long serialVersionUID = 8430838531056913404L;

   public OTEException(String format, Throwable t) {
      super(format, t);
   }

   public OTEException(String string) {
      super(string);
   }

   public OTEException(Throwable e) {
      super(e);
   }


}
