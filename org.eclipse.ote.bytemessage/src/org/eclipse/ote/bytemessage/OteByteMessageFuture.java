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
package org.eclipse.ote.bytemessage;



public interface OteByteMessageFuture<T extends OteByteMessage, R extends OteByteMessage> {

   void cancel();
   
   void waitForCompletion();
   
   public boolean isTimedOut();
   
   public boolean gotResponse();
}
