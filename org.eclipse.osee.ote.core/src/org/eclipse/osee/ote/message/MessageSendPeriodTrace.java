package org.eclipse.osee.ote.message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.eclipse.osee.ote.message.data.MessageData;

/**
 * This class will measure the time between sends.
  * <pre>
 * {@code
 * MessageSendPeriodTrace trace = new MessageSendPeriodTrace(TimeUnit.NANOSECONDS, testMessage, logger);
 * trace.start();
 * //wait or do stuff
 * trace.stop();
 * trace.printResults();
 * }
 * </pre>
 * @author Andrew M. Finkbeiner
 *
 */
public class MessageSendPeriodTrace extends TimeTrace {
   
   private SendTimer sendTimer;
   @SuppressWarnings("rawtypes")
   private Message message;
   private int maxFlag = Integer.MAX_VALUE;
   private ITestLogger logger = null;
   private TimeUnit timeUnit;
   private int minError = Integer.MIN_VALUE;
   private int maxError = Integer.MAX_VALUE;
   
   /**
    * This version of the constructor will log results to the outfile.  
    * 
    * @param message
    * @param logger
    */
   @SuppressWarnings("rawtypes")
   public MessageSendPeriodTrace(TimeUnit timeUnit, Message message, ITestLogger logger) {
      this(timeUnit, message);
      this.logger = logger;
   }
   
   @SuppressWarnings("rawtypes")
   public MessageSendPeriodTrace(TimeUnit timeUnit, Message message) {
      super(String.format("MessageSendPeriodTrace[%s]", message.getMessageName()));
      this.timeUnit = timeUnit;
      this.message = message;
      sendTimer = new SendTimer(this);
   }
   
   /**
    * 
    * @param expected the expected period in the constructed TimeUnit
    * @param maxError the max variance off of the period in the constructed TimeUnit
    */
   public void setMaxVariance(int expected, int maxError){
      this.minError = expected - maxError;
      this.maxError = expected + maxError;
   }

   public void addStartEvent(String message){
      add(new TimeEvent(message));
   }


   @Override
   public void start(){
      super.start();
      message.getDefaultMessageData().addSendListener(sendTimer);
   }
   
   @Override
   public void stop(){
      super.stop();
      message.getDefaultMessageData().removeSendListener(sendTimer);
   }
   
   public void printResults(){
      List<TimeEvent> events = get();
      int count = 0;
      int exceedanceCount = 0;
      double average = 0.0;
      double max = -1.0;
      double min = -1.0;
      
      SendEvent pre1 = null;
      SendEvent pre2 = null;
      for(TimeEvent event:events){
         if(event instanceof SendEvent){
            SendEvent sendEvent = (SendEvent)event;
            if(sendEvent.type == SendEventType.pre){
               pre1 = pre2;
               pre2 = sendEvent; 
            } 
         }
         if(pre1 != null && pre2 != null){
            long nanoDiff = pre2.getNanoTime() - pre1.getNanoTime();
            count++;
            long currentTime = timeUnit.convert(nanoDiff, TimeUnit.NANOSECONDS);
            
            if(currentTime >= 0){
               if(max < 0 || currentTime > max){
                  max = currentTime;
               }
               if(min < 0 || currentTime < min){
                  min = currentTime;
               }
               if(currentTime > maxError || currentTime < minError){
                  exceedanceCount++;
                  String maxMessage = String.format("%s: count[%d] %d [%d (count)] [%s]", getName(), count, currentTime, exceedanceCount, timeUnit.name());
                  System.out.println(maxMessage);
                  if(logger != null){
                     logger.log(TestLevel.ATTENTION, maxMessage, null);
                  }
               }
               average = (((count-1) * average) + currentTime)/count;
            }
         }
      }
      String summaryMessage = String.format("%s: count[%d] avg[%f] min[%f] max[%f] units[%s] { exceedanceCount [%d] (%d) }", getName(), count, average, min, max, timeUnit.name(), exceedanceCount, maxFlag);
      if(logger != null){
         logger.log(TestLevel.ATTENTION, summaryMessage, null);
      }
      System.out.println(summaryMessage);
   }
   
   private static class SendTimer implements IMessageSendListener {

      private MessageSendPeriodTrace messageSendOperator;

      public SendTimer(MessageSendPeriodTrace messageSendOperator) {
         this.messageSendOperator = messageSendOperator;
      }

      @Override
      public void onPreSend(MessageData messageData) {
         messageSendOperator.add(new SendEvent(SendEventType.pre));
      }

      @Override
      public void onPostSend(MessageData messageData) {
      }
      
   }
   
   private static class SendEvent extends TimeEvent {
      
      public final SendEventType type;
      
      public SendEvent(SendEventType type) {
         super(type.name());
         this.type = type;
      }
      
   }
   
   private enum SendEventType {
      pre
   }
   
}
