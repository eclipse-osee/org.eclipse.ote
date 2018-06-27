package org.eclipse.osee.ote.message;

public class TimeEvent {

   private long nanoTime;
   private String lable;
   
   public TimeEvent(String label) {
      this.lable = label;
      nanoTime = System.nanoTime();
   }

   public long getNanoTime(){
      return nanoTime;
   }

   public void print() {
      System.out.printf("%s %d", lable, nanoTime);
   }
}
