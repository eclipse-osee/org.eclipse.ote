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
package org.eclipse.ote.statemachine;

import org.eclipse.ote.statemachine.BaseInput;
import org.eclipse.ote.statemachine.ChildStateMachineState;
import org.eclipse.ote.statemachine.StateMachine;

public class Hierarchy extends ChildStateMachineState {

   public Hierarchy(StateMachine sm) {
      super(sm);
   }

   @Override
   public void exit() {
      // TODO Auto-generated method stub

   }

   @Override
   public void preRunStateMachine(BaseInput input) {
      // TODO Auto-generated method stub
   }

   @Override
   public void postRunStateMachine(BaseInput input) {
      // TODO Auto-generated method stub
      
   }

}
