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
package org.eclipse.ote.bytemessage.internal;

import org.eclipse.ote.bytemessage.OteByteMessage;
import org.eclipse.ote.bytemessage.OteByteMessageResponseCallable;
import org.eclipse.ote.bytemessage.OteByteMessageResponseFuture;
import org.eclipse.ote.bytemessage.OteByteMessageUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;


public class OteByteMessageResponseFutureImpl<R extends OteByteMessage> implements OteByteMessageResponseFuture<R>, EventHandler {
   private final ServiceRegistration<EventHandler> reg;
   private final OteByteMessageResponseCallable<R> callable;
   private final Class<R> recieveClasstype;

   public OteByteMessageResponseFutureImpl(Class<R> recieveClasstype, OteByteMessageResponseCallable<R> callable, String responseTopic) {
      this.callable = callable;
      this.recieveClasstype = recieveClasstype;
      reg = OteByteMessageUtil.subscribe(responseTopic, this);
   }
   
   @Override
   public void handleEvent(Event event) {
      try {
         R msg = recieveClasstype.newInstance();
         OteByteMessageUtil.putBytes(event, msg);
         callable.call(msg);
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
   }

   public void cancel(){
      dispose();
   }
   
   private void dispose(){
      reg.unregister();
   }
}
