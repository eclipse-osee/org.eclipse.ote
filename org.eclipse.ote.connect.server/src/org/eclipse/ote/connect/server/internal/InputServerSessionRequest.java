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

import org.eclipse.ote.connect.messages.ServerSessionRequest;
import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.StateMachine;

public class InputServerSessionRequest extends BaseInput{

   public static final Object TYPE = new Object();
   private ServerSessionRequest request;
   
   public InputServerSessionRequest(StateMachine stateMachine) {
      super(stateMachine);
   }

   @Override
   public Object getType() {
      return TYPE;
   }
   
   public void set(ServerSessionRequest request){
      this.request = request;
   }
   
   public ServerSessionRequest get(){
      return request;
   }

}
