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
package org.eclipse.ote.event.bridge.jms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.ote.bytemessage.OteByteMessage;
import org.eclipse.ote.bytemessage.OteByteMessageUtil;
import org.eclipse.ote.jms.node.JmsConnectionNodeProvider;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class EventToJmsComponent extends OseeMessagingListener implements EventHandler {

   private JmsConnectionNodeProvider connecitonNode;
   private EventAdmin eventAdmin;
   private List<Event> eventQueue;
   private final UUID MYID;

   public EventToJmsComponent(){
      eventQueue = new CopyOnWriteArrayList<Event>();
      MYID = UUID.randomUUID();
   }

   void start(){
      System.out.println("hehe");
   }

   void stop(){
   }

   public synchronized void bindConnectionNode(JmsConnectionNodeProvider connectionNode){
      this.connecitonNode = connectionNode;
      connecitonNode.getConnectionNode().subscribe(BridgeMessages.BYTE_MESSAGE, this);
      for(Event event:eventQueue){
         sendEvent(event);
      }
      eventQueue.clear();
   }

   public synchronized void unbindConnectionNode(JmsConnectionNodeProvider connectionNode){
      connecitonNode.getConnectionNode().unsubscribe(BridgeMessages.BYTE_MESSAGE, this);
      this.connecitonNode = null;
   }

   public void bindEventAdmin(EventAdmin eventAdmin){
      this.eventAdmin = eventAdmin;
   }

   public void unbindEventAdmin(EventAdmin eventAdmin){
      this.eventAdmin = null;
   }

   @Override
   public synchronized void handleEvent(Event event) {
      if(connecitonNode != null){
         sendEvent(event);
      } else {
         eventQueue.add(event);
      }
   }

   private void sendEvent(Event event){
      Object obj = event.getProperty("bytes");
      if(obj != null && obj instanceof byte[]){
         try {
            OteByteMessage msg = new OteByteMessage((byte[])obj);
            UUID id = OteByteMessageUtil.getUUID(msg);
            if(!id.equals(MYID)){
               OteByteMessageUtil.setUUID(msg, MYID);
               connecitonNode.getConnectionNode().send(BridgeMessages.BYTE_MESSAGE, obj);
            }
         } catch (OseeCoreException e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
      if(message instanceof byte[]){
         OteByteMessage msg = new OteByteMessage((byte[])message);
         UUID id = OteByteMessageUtil.getUUID(msg);
         if(!id.equals(MYID)){
            OteByteMessageUtil.setUUID(msg, MYID);
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("bytes", msg.getData());
            Event newevent = new Event(msg.getHeader().TOPIC.getValue(), data);
            eventAdmin.sendEvent(newevent);
         }
      } else {
         OseeLog.log(EventToJmsComponent.class, Level.SEVERE, "not a recognized message" + message.getClass());
      }
   }
}
