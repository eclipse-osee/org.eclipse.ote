/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ote.core.osgi;

/**
 * An operation that interacts with an OSGI service in an atomic manner
 * 
 * @author Ken J. Aguilar
 */
public interface IServiceOperation {

   /**
    * the operation to perform against the service. The service instance will be valid during the execution of the
    * operation. Subclasses should handle the case when the operation is asynchronously interrupted.
    */
   void doOperation(Object service) throws InterruptedException;

   /**
    * called when the service is about to be removed.
    */
   void interrupt();
}
