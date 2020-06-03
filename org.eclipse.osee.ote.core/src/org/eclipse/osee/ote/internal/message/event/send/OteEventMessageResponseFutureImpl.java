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

package org.eclipse.osee.ote.internal.message.event.send;

import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.event.send.OteEventMessageResponseCallable;
import org.eclipse.osee.ote.message.event.send.OteEventMessageResponseFuture;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;


public class OteEventMessageResponseFutureImpl<R extends OteEventMessage> implements OteEventMessageResponseFuture<R>, EventHandler {
   private final ServiceRegistration<EventHandler> reg;
   private final OteEventMessageResponseCallable<R> callable;
   private final Class<R> recieveClasstype;

   public OteEventMessageResponseFutureImpl(Class<R> recieveClasstype, OteEventMessageResponseCallable<R> callable, String responseTopic) {
      this.callable = callable;
      this.recieveClasstype = recieveClasstype;
      reg = OteEventMessageUtil.subscribe(responseTopic, this);
   }
   
   @Override
   public void handleEvent(Event event) {
      try {
         R msg = recieveClasstype.newInstance();
         OteEventMessageUtil.putBytes(event, msg);
         callable.call(msg);
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void cancel(){
      dispose();
   }
   
   private void dispose(){
      reg.unregister();
   }
}
