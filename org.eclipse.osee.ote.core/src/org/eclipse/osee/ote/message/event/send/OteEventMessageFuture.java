/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ote.message.event.send;

import org.eclipse.osee.ote.message.event.OteEventMessage;



public interface OteEventMessageFuture<T extends OteEventMessage, R extends OteEventMessage> {

   void cancel();
   
   void waitForCompletion();
   
   public boolean isDone();
   
   public boolean isTimedOut();
   
   public boolean gotResponse();

   void complete();
}
