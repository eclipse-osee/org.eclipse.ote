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

package org.eclipse.osee.ote.messaging.dds;

/**
 * This class is used to mark areas of code that are in the DDS Specification but are not currently implemented.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class NotImplementedException extends RuntimeException {

   private static final long serialVersionUID = 8437766402272756599L;

   /**
    * Creates a new exception with a message specifying that the given functionality is not implemented.
    */
   public NotImplementedException() {
      super("This functionality has not been implemented");
   }

}
