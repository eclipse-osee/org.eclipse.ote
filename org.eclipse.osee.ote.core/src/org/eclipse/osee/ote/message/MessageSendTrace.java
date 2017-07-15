package org.eclipse.osee.ote.message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.eclipse.osee.ote.message.data.MessageData;

/**
 * This class will time the send call of a message.  From just after you call send to after socket.send() has been called.
 * 
 * The results will be written to System.out or System.out and the Outfile, depending on which constructor you use.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class MessageSendTrace extends TimeTrace {
   
   private SendTimer sendTimer;
   @SuppressWarnings("rawtypes")
   private Message message;
   private int maxFlag = Integer.MAX_VALUE;
   private ITestLogger logger = null;
   private TimeUnit timeUnit;
   
   /**
    * This version of the constructor will log results to the outfile.  
    * 
    * @param message
    * @param logger
    */
   @SuppressWarnings("rawtypes")
   public MessageSendTrace(TimeUnit timeUnit, Message message, ITestLogger logger) {
      this(timeUnit, message);
      this.logger = logger;
   }
   
   @SuppressWarnings("rawtypes")
   public MessageSendTrace(TimeUnit timeUnit, Message message) {
      super(String.format("MessageSendTrace[%s]", message.getMessageName()));
      this.timeUnit = timeUnit;
      this.message = message;
      sendTimer = new SendTimer(this);
   }
   
   /**
    * Set the time that you want to flag for exceeding the expected time.  It is in the scale of the passed in TimeUnit.
    * 
    * @param maxFlagNS
    */   
   public void setMaxFlag(int maxFlag){
      this.maxFlag = maxFlag;
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
      
      SendEvent pre = null;
      SendEvent post = null;
      for(TimeEvent event:events){
         if(event instanceof SendEvent){
            SendEvent sendEvent = (SendEvent)event;
            if(sendEvent.type == SendEventType.pre){
               pre = sendEvent;
            } else {
               post = sendEvent;
            }
         }
         if(pre != null && post != null){
            long nanoDiff = post.getNanoTime() - pre.getNanoTime();
            pre = null;
            post = null;
            count++;
            long currentTime = timeUnit.convert(nanoDiff, TimeUnit.NANOSECONDS);
            
            if(currentTime >= 0){
               if(max < 0 || currentTime > max){
                  max = currentTime;
               }
               if(min < 0 || currentTime < min){
                  min = currentTime;
               }
               if(currentTime > maxFlag){
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

      private MessageSendTrace messageSendOperator;

      public SendTimer(MessageSendTrace messageSendOperator) {
         this.messageSendOperator = messageSendOperator;
      }

      @Override
      public void onPreSend(MessageData messageData) {
         messageSendOperator.add(new SendEvent(SendEventType.pre));
      }

      @Override
      public void onPostSend(MessageData messageData) {
         messageSendOperator.add(new SendEvent(SendEventType.post));
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
      pre,
      post
   }
   
}
