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
import org.eclipse.ote.statemachine.BaseState;

public class Purple  extends BaseState {

   private Change internalExit;

   public Purple(Change internalExit) {
      this.internalExit = internalExit;
   }

   @Override
   public void run(BaseInput intput) {
      
   }

   @Override
   public void entry() {
      
   }

   @Override
   public void exit() {
      internalExit.addToStateMachineQueue();
   }

}
