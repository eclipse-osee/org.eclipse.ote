/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
