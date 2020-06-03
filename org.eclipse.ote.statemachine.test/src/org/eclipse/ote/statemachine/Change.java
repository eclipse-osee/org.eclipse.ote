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

package org.eclipse.ote.statemachine;


public class Change extends BaseInput{

   private Object obj;
   private String name;
   
   public Change(StateMachine sm, Object obj){
      super(sm);
      this.obj = obj;
   }
   
   public Change(StateMachine sm, Object obj, String name){
      super(sm);
      this.obj = obj;
      this.name = name;
   }
   
   @Override
   public
   Object getType() {
      return obj;
   }
   
   public String toString(){
      if(name != null){
         return this.name;
      } else {
         return obj.toString();
      }
   }

}
