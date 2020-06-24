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

package org.eclipse.osee.ote.message;

/**
 * @author Andrew M. Finkbeiner
 */
public interface MessageDefinitionProvider {

   /**
    * This function returns an ID that is intended to be unique to the running system. The system should not allow more
    * than one active instance of a service with the same singletonId.
    * @return System unique ID for this provider
    */
   String singletonId();

   String majorVersion();

   String minorVersion();

   void generateMessageIndex(MessageSink sink) throws Exception;
}
