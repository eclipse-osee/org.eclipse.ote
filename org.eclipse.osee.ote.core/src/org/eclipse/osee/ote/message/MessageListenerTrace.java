package org.eclipse.osee.ote.message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;

/**
 * This class will time the listeners of a message.  Only one can be set on a message at a time.
 * 
 * <pre>
 * {@code
 * MessageListenerTrace trace = new MessageListenerTrace(TimeUnit.NANOSECONDS, testMessage, logger);
 * trace.start();
 * //wait or do stuff
 * trace.stop();
 * trace.printResults();
 * }
 * </pre>
 * @author Andrew M. Finkbeiner
 *
 */
public class MessageListenerTrace extends TimeTrace {

   @SuppressWarnings("rawtypes")
   private Message message;
   private TimeUnit timeUnit;
   private ITestLogger logger = null;
   private int maxAllListenerTime = Integer.MAX_VALUE;
   private int maxListenerTime = Integer.MAX_VALUE;
   
   /**
    * 
    * @param timeUnit - determines the resolution of the timing that is measured.
    * @param message - the message to measure
    */
   @SuppressWarnings("rawtypes")
   public MessageListenerTrace(TimeUnit timeUnit, Message message) {
      super(String.format("MessageListenerTrace[%s]", message.getName()));
      this.message = message;
      this.timeUnit = timeUnit;
   }
   
   /**
    * 
    * @param timeUnit - determines the resolution of the timing that is measured.
    * @param message - the message to measure
    * @param logger - output will be written to a logger if the value is not null
    */
   @SuppressWarnings("rawtypes")
   public MessageListenerTrace(TimeUnit timeUnit, Message message, ITestLogger logger) {
      this(timeUnit, message);
      this.logger = logger;
   }
   
   public void setMaxAllListeners(int time){
      maxAllListenerTime = time;
   }
   
   public void setMaxListeners(int time){
      maxListenerTime = time;
   }
   
   @Override
   public void start(){
      super.start();
      message.setListenerTrace(this);
   }
   
   @Override
   public void stop(){
      super.stop();
      message.clearListenerTrace();
   }

   public void printResults(){
      List<TimeEvent> events = get();
      int allCount = 0;
      int allexceedanceCount = 0;
      double allaverage = 0.0;
      double allmax = -1.0;
      double allmin = -1.0;
      
      int count = 0;
      int exceedanceCount = 0;
      double average = 0.0;
      double max = -1.0;
      double min = -1.0;
      
      ListenerEvent preNotify = null;
      ListenerEvent postNotify = null; 
      ListenerEvent preListener = null;
      ListenerEvent postListener = null; 
      
      for(TimeEvent event:events){
         if(event instanceof ListenerEvent){
            ListenerEvent sendEvent = (ListenerEvent)event;
            if(sendEvent.type == ListenerEventType.startNotify){
               preNotify = sendEvent;
            } else if (sendEvent.type == ListenerEventType.endNotify) {
               postNotify = sendEvent;
            } else if (sendEvent.type == ListenerEventType.startListener){
               preListener = sendEvent;
            } else if (sendEvent.type == ListenerEventType.endListener){
               postListener = sendEvent;
            }
         }
         if(preNotify != null && postNotify != null){
            long nanoDiff = postNotify.getNanoTime() - preNotify.getNanoTime();
            preNotify = null;
            postNotify = null;
            allCount++;
            long currentTime = timeUnit.convert(nanoDiff, TimeUnit.NANOSECONDS);
            
            if(currentTime >= 0){
               if(allmax < 0 || currentTime > allmax){
                  allmax = currentTime;
               }
               if(allmin < 0 || currentTime < allmin){
                  allmin = currentTime;
               }
               if(currentTime > maxAllListenerTime){
                  allexceedanceCount++;
                  String maxMessage = String.format("%s: count[%d] %d [%d (count)] [%s]", getName(), allCount, currentTime, allexceedanceCount, timeUnit.name());
                  System.out.println(maxMessage);
                  if(logger != null){
                     logger.log(TestLevel.ATTENTION, maxMessage, null);
                  }
               }
               allaverage = (((allCount-1) * allaverage) + currentTime)/allCount;
            }
         }
         
         if(preListener != null && postListener != null && preListener.listener == postListener.listener){
            long nanoDiff = postListener.getNanoTime() - preListener.getNanoTime();
            String listenerLabel = preListener.listener.getClass().getName();
            preListener = null;
            postListener = null;
            count++;
            long currentTime = timeUnit.convert(nanoDiff, TimeUnit.NANOSECONDS);
            
            if(currentTime >= 0){
               if(max < 0 || currentTime > max){
                  max = currentTime;
               }
               if(min < 0 || currentTime < min){
                  min = currentTime;
               }
               if(currentTime > maxListenerTime){
                  exceedanceCount++;
                  String maxMessage = String.format("%s: %s: count[%d] %d [%d (count)] [%s]", getName(), listenerLabel, count, currentTime, exceedanceCount, timeUnit.name());
                  System.out.println(maxMessage);
                  if(logger != null){
                     logger.log(TestLevel.ATTENTION, maxMessage, null);
                  }
               }
               average = (((count-1) * average) + currentTime)/count;
            }
         }
      }
      String summaryMessage = String.format("%s: ALL count[%d] avg[%f] min[%f] max[%f] units[%s] { exceedanceCount [%d] (%d) }", getName(), allCount, allaverage, allmin, allmax, timeUnit.name(), allexceedanceCount, maxAllListenerTime);
      String summaryMessage2 = String.format("%s: Listeners count[%d] avg[%f] min[%f] max[%f] units[%s] { exceedanceCount [%d] (%d) }", getName(), count, average, min, max, timeUnit.name(), exceedanceCount, maxListenerTime);
      if(logger != null){
         logger.log(TestLevel.ATTENTION, summaryMessage, null);
         logger.log(TestLevel.ATTENTION, summaryMessage2, null);
      }
      System.out.println(summaryMessage);
      System.out.println(summaryMessage2);
   }
   
   public void addStartNotify() {
      add(new ListenerEvent(ListenerEventType.startNotify));
   }

   public void addEndNotify() {
      add(new ListenerEvent(ListenerEventType.endNotify));
   }

   public void addStartListener(IOSEEMessageListener listener) {
      add(new ListenerEvent(ListenerEventType.startListener, listener));
   }

   public void addEndListener(IOSEEMessageListener listener) {
      add(new ListenerEvent(ListenerEventType.endListener, listener));
   }
   
   private static class ListenerEvent extends TimeEvent {
      
      public final ListenerEventType type;
      public IOSEEMessageListener listener;
      
      public ListenerEvent(ListenerEventType type) {
         super(type.name());
         this.type = type;
      }

      public ListenerEvent(ListenerEventType type, IOSEEMessageListener listener) {
         this(type);
         this.listener = listener;
      }
      
   }
   
   private enum ListenerEventType {
      startNotify,
      endNotify,
      startListener,
      endListener
   }
   

}
