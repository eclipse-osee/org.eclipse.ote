/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
