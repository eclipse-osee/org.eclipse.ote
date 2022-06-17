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

package org.eclipse.osee.ote.service;

/**
 * @author Ken J. Aguilar
 */
public class TestSessionException extends Exception {

   private static final long serialVersionUID = -2013110839832896588L;

   public TestSessionException(String message, Throwable cause) {
      super(message, cause);
   }

   public TestSessionException(String message) {
      super(message);
   }

}
