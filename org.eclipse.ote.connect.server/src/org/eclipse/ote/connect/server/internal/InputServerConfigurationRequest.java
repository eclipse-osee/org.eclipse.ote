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

import org.eclipse.ote.connect.messages.ServerConfigurationRequest;
import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.StateMachine;

public class InputServerConfigurationRequest extends BaseInput {

   public static final Object TYPE = new Object();
   
   private ServerConfigurationRequest recieved;
   
   public InputServerConfigurationRequest(StateMachine stateMachine) {
      super(stateMachine);
   }

   @Override
   public Object getType() {
      return TYPE;
   }

   public void set(ServerConfigurationRequest recieved){
      this.recieved = recieved;
   }
   
   public ServerConfigurationRequest get(){
      return recieved;
   }
}
