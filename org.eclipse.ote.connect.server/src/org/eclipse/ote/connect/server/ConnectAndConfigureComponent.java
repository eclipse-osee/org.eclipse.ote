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
package org.eclipse.ote.connect.server;

import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.ote.connect.server.internal.SessionStateMachine;
import org.osgi.service.event.EventAdmin;

public class ConnectAndConfigureComponent {
   
   private IHostTestEnvironment host;
   private SessionStateMachine stateMachine;
   private EventAdmin eventAdmin;

   public void start(){
      
      stateMachine = new SessionStateMachine(host, eventAdmin);
      
      stateMachine.start();
   }
   
   public void stop(){
      if(stateMachine != null){
         stateMachine.stop();
      }
   }
   
   public void bindHostTestEnvironment(IHostTestEnvironment host){
      this.host = host;
   }
   
   public void unbindHostTestEnvironment(IHostTestEnvironment host){
      this.host = null;
   }
   
   public void bindEventAdmin(EventAdmin eventAdmin){
      this.eventAdmin = eventAdmin;
   }
   
   public void unbindEventAdmin(EventAdmin eventAdmin){
      this.eventAdmin = null;
   }
   
}
