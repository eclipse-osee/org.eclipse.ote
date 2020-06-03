/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.ote.test.manager.uut.selector.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.event.FileAvailableRequest;
import org.eclipse.osee.ote.message.event.FileAvailableStatus;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

/**
 * Provide info about file availability from the test server
 * 
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutAvailableEventHandler implements EventHandler {
   private static UutAvailableEventHandler handler;

   private List<UutAvailableChangeListener> listeners;
   private Map<String, AvailableStatus>availability;

   private Timer timer;
   private Boolean timerActive;
   private volatile boolean notifyNeeded;

   public static UutAvailableEventHandler getHandler() {
      return handler;
   }

   /**
    * Use UutAvailableEventHandler.getHandler() instead
    */
   public UutAvailableEventHandler() {
      listeners = new ArrayList<>();
      availability = new HashMap<>();
      timer = new Timer("UUT Available Delay");
      timerActive = false;
      notifyNeeded = false;
   }

   /**
    * OSGI
    */
   public void start() {
      handler = this;
   }

   /**
    * OSGI
    */
   public void stop() {
      handler = null;
   }


   public void addListener(UutAvailableChangeListener listener) {
      listeners.add(listener);
   }

   public void removeListener(UutAvailableChangeListener listener) {
      listeners.remove(listener);
   }

   @Override
   public void handleEvent(Event event) {
      if (event.getTopic().equals(FileAvailableStatus._TOPIC)) {
         byte[] bytes = OteEventMessageUtil.getBytes(event);
         if (null != bytes) {
            FileAvailableStatus status = new FileAvailableStatus();
            status.setData(bytes);
            synchronized (availability) {
               AvailableStatus currentAvail = availability.get(status.FILE.getValue());
               if (currentAvail == null) {
                  return; // We didn't ask about this file
               }
               boolean availabilityChanged = false;
               if (currentAvail.available != status.EXISTS.getValue()) {
                  availabilityChanged = true;
               }
               currentAvail.available = status.EXISTS.getValue();
               if (availabilityChanged) {
                  availabilityChanged();
               }
            }
         }
      }
   }

   public boolean getAvailability(String file) {
      if (file == null || file.trim().isEmpty()) {
         return false;
      }
      file = file.trim();
      synchronized (availability) {
         AvailableStatus currentAvail = availability.get(file);

         if (currentAvail == null) {
            currentAvail = new AvailableStatus();
            availability.put(file, currentAvail);
            currentAvail.available = false;
            currentAvail.time = 0;
         }

         // Ask the server if its been a few seconds
         long currentTimeMillis = System.currentTimeMillis();
         if ((currentAvail.time + 5000) < currentTimeMillis) {
            currentAvail.time = currentTimeMillis;
            if(Display.findDisplay(Thread.currentThread()) != null){
               final String theFile = file;
               new Thread(new Runnable(){
                  @Override
                  public void run() {
                     FileAvailableRequest request = new FileAvailableRequest();
                     request.FILE.setValue(theFile);
                     OteEventMessageUtil.sendEvent(request, getEventAdmin());
                  }
                  
               }).start();
            } else {
               FileAvailableRequest request = new FileAvailableRequest();
               request.FILE.setValue(file);
               OteEventMessageUtil.sendEvent(request, getEventAdmin());
            }
         }

         return currentAvail.available;
      }
   }

   public void resetAvailability() {
      synchronized (availability) {
         availability.clear();
      }
   }

   /**
    * Make sure we notify the listeners when a change happens, but don't spam(tm) them.
    */
   private void availabilityChanged() {
      synchronized (timerActive) {
         if (!timerActive) {
            notifyListeners();
            timer.schedule(new TimerTask() {
               @Override
               public void run() {
                  synchronized (timerActive) {
                     if (notifyNeeded) {
                        notifyListeners();
                        notifyNeeded = false;
                     }
                     timerActive = false;
                  }
               }
            }, 100);
            timerActive = true;
         } else {
            notifyNeeded = true;
         }
      }
   }

   private void notifyListeners() {
      List<UutAvailableChangeListener> listList = new ArrayList<>(listeners);
      for (UutAvailableChangeListener listener : listList) {
         try {
            listener.uutAvailableChange();
         }
         catch (Throwable th) {
            listeners.remove(listener);
            OseeLog.log(UutAvailableEventHandler.class, Level.SEVERE, th);
         }
      }
   }


   private static EventAdmin getEventAdmin() {
      return getService(EventAdmin.class);
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private static <T> T getService(Class<T> clazz){
      BundleContext context = getContext();
      if(context == null){
         return null;
      }
      ServiceReference serviceReference = context.getServiceReference(clazz.getName());
      if(serviceReference == null){
         return null;
      }
      return (T)getContext().getService(serviceReference);
   }

   private static BundleContext getContext(){
      return FrameworkUtil.getBundle(UutAvailableEventHandler.class).getBundleContext();
   }

   private class AvailableStatus {
      public long time;
      public boolean available;
   }

}
