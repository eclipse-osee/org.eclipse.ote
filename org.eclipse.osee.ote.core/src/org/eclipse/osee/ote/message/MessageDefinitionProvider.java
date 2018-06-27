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
package org.eclipse.osee.ote.message;


/**
 * @author Andrew M. Finkbeiner
 */
public interface MessageDefinitionProvider {

   /**
    * This function returns an ID that is intended to be unique to the running system. The system should not allow more
    * than one active instance of a service with the same singletonId.
    */
   String singletonId();

   String majorVersion();

   String minorVersion();

   void generateMessageIndex(MessageSink sink) throws Exception;
}
