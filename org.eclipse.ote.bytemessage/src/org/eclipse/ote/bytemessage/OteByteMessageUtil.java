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
package org.eclipse.ote.bytemessage;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import org.eclipse.ote.services.core.ServiceUtility;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class OteByteMessageUtil {

   public static void sendEvent(OteByteMessage message) {
      EventAdmin eventAdmin = ServiceUtility.getService(EventAdmin.class);
      sendEvent(message, eventAdmin);
   }

   public static void sendEvent(OteByteMessage message, EventAdmin eventAdmin) {
      message.getHeader().UUID_HIGH.setNoLog((long) 0x0);
      message.getHeader().UUID_LOW.setNoLog((long) 0x0);
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("bytes", message.getData());
      Event newevent = new Event(message.getHeader().TOPIC.getValue(), data);
      eventAdmin.sendEvent(newevent);
   }
   
   public static void postEvent(OteByteMessage message) {
      EventAdmin eventAdmin = ServiceUtility.getService(EventAdmin.class);
      postEvent(message, eventAdmin);
   }
   
   public static void postEvent(OteByteMessage message, EventAdmin eventAdmin) {
      message.getHeader().UUID_HIGH.setNoLog((long) 0x0);
      message.getHeader().UUID_LOW.setNoLog((long) 0x0);
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("bytes", message.getData());
      Event newevent = new Event(message.getHeader().TOPIC.getValue(), data);
      eventAdmin.postEvent(newevent);
   }

   public static UUID getUUID(OteByteMessage msg) {
      return new UUID(msg.getHeader().UUID_HIGH.getValue(), msg.getHeader().UUID_LOW.getValue());
   }
   
   public static UUID getUUID(byte[] data) {
      long low = getLong(data, 74);
      long high = getLong(data, 82);
      return new UUID(high, low);
   }

   private static long getLong(byte[] data, int index){
      return
      (long)(0xff & data[index]) << 56  |
      (long)(0xff & data[index+1]) << 48  |
      (long)(0xff & data[index+2]) << 40  |
      (long)(0xff & data[index+3]) << 32  |
      (long)(0xff & data[index+4]) << 24  |
      (long)(0xff & data[index+5]) << 16  |
      (long)(0xff & data[index+6]) << 8   |
      (long)(0xff & data[index+7]) << 0;
   }
   
   public static void setUUID(OteByteMessage msg, UUID id) {
      msg.getHeader().UUID_HIGH.setValue(id.getMostSignificantBits());
      msg.getHeader().UUID_LOW.setValue(id.getLeastSignificantBits());
   }

   public static OteByteMessage getOteByteMessage(Event event) {
      Object obj = event.getProperty("bytes");
      if (obj != null && obj instanceof byte[]) {
         return new OteByteMessage((byte[]) obj);
      }
      return null;
   }
   
   public static byte[] getBytes(Event event) {
      Object obj = event.getProperty("bytes");
      if (obj != null && obj instanceof byte[]) {
         return (byte[]) obj;
      } else {
         return null;
      }
   }

   public static void putBytes(Event event, OteByteMessage signal) {
      signal.getDefaultMessageData().getMem().setData(getBytes(event));
   }

   public static ServiceRegistration<EventHandler> subscribe(String topic, EventHandler handler){
      Dictionary<String, String> props = new Hashtable<String, String>();
      props.put("event.topics", topic);
      return ServiceUtility.getContext().registerService(EventHandler.class, handler, props);
   }
   
   public static ServiceRegistration<EventHandler> subscribe(OteByteMessage signal, EventHandler eventHandler) {
      BundleContext context = ServiceUtility.getContext();
      if (context == null) {
         return null;
      }
      Hashtable<String, Object> props = new Hashtable<String, Object>();
      props.put("event.topics", signal.getHeader().TOPIC.getValue());
      return context.registerService(EventHandler.class, eventHandler, props);
   }

}
