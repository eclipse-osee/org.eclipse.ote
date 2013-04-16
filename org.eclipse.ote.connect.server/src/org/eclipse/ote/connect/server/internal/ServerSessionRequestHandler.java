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
package org.eclipse.ote.connect.server.internal;

import org.eclipse.ote.bytemessage.OteByteMessageResponseCallable;
import org.eclipse.ote.connect.messages.ServerSessionRequest;

public class ServerSessionRequestHandler implements OteByteMessageResponseCallable<ServerSessionRequest> {

   private InputServerSessionRequest inputServerSesisonRequest;

   public ServerSessionRequestHandler(InputServerSessionRequest inputServerSesisonRequest){
      this.inputServerSesisonRequest = inputServerSesisonRequest;
   }
   
   public void timeout(){
      
   }
   
   public void call(ServerSessionRequest received){
      inputServerSesisonRequest.set(received);
      inputServerSesisonRequest.addToStateMachineQueue();
   }
   
 }