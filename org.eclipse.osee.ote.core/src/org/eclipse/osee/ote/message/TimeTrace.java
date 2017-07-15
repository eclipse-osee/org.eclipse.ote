package org.eclipse.osee.ote.message;

import java.util.ArrayList;
import java.util.List;

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
      this.name = name;
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
