/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;

/**
 * This is a class that has the base functionality to keep a list of {@link TimeEvent} objects.  This way 
 * we can post process the events to determine timing of a trace of functionality.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class TimeTrace {

   private String name;
   private List<TimeEvent> events;
   private volatile boolean isRunning;

   public TimeTrace(String name){
      this.name = String.format(this.getClass().getName() + "[%s]", name);
      events = new ArrayList<>();
   }
   
   public void stop() {
      isRunning = false;
   }
   
   public void start(){
      isRunning = true;
   }
   
   public synchronized void add(TimeEvent event){
      if(isRunning){
         events.add(event);
      }
   }
   
   public List<TimeEvent> get(){
      return events;
   }
   
   public synchronized void printResults(){
      System.out.println(">>>>>>>>>>>>>>>>>>>>-----------------");
      System.out.println(name);
      System.out.println("-----------------");
      for(int i = 0; i < events.size(); i++){
         events.get(i).print();
         System.out.println();
      }
      System.out.println("<<<<<<<<<<<<<<<<<<<<-----------------");
   }
   
   public synchronized void clear(){
      events.clear();
   }

   public String getName(){
      return name;
   }
}
