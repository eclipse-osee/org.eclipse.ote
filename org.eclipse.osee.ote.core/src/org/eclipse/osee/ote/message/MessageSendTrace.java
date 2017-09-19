package org.eclipse.osee.ote.message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.data.MessageData;

/**
 * This class will time the send call of a message.  From just after you call send to after socket.send() has been called.
 * 
 * The results will be written to System.out or System.out and the Outfile, depending on which constructor you use.
 * 
 * @author Andrew M. Finkbeiner
 *
 */
public class MessageSendTrace extends MessageTimeTrace {
   
   public final String TRACE_TYPE = "MessageSendTrace";
   private SendTimer sendTimer;

   private int maxFlag = Integer.MAX_VALUE;
   
   private MessageTraceOutput messageTraceOutput;
   private MessageTraceLogger messageTraceLogger;
   
   /**
    * @param environment - the test environment.
    * @param timeUnit - determines the resolution of the timing that is measured.
    * @param message - the message to measure
    * @param messageTraceLogger - logger to write output
    */
   @SuppressWarnings("rawtypes")
   public MessageSendTrace(ITestEnvironmentAccessor environment, TimeUnit timeUnit, Message message, MessageTraceLogger messageTraceLogger) {
      super(environment, message, timeUnit);
      sendTimer = new SendTimer(this);
      this.messageTraceOutput = new MessageTraceOutput();
      this.messageTraceLogger = messageTraceLogger;
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
      getMessage().getDefaultMessageData().addSendListener(sendTimer);
   }
   
   @Override
   public void stop(){
      super.stop();
      getMessage().getDefaultMessageData().removeSendListener(sendTimer);
   }
   
   @Override
   public synchronized void printResults(){
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
            long currentTime = getTimeUnit().convert(nanoDiff, TimeUnit.NANOSECONDS);
            
            if(currentTime >= 0){
               if(max < 0 || currentTime > max){
                  max = currentTime;
               }
               if(min < 0 || currentTime < min){
                  min = currentTime;
               }
               if(currentTime > maxFlag){
                  exceedanceCount++;
                  String maxMessage = String.format("%s: count[%d] %d [%d (count)] [%s]", getName(), count, currentTime, exceedanceCount, getTimeUnit().name());
                  System.out.println(maxMessage);
               }
               average = (((count-1) * average) + currentTime)/count;
            }
         }
      }
      messageTraceOutput.setMessage(getMessage().getMessageName());
      messageTraceOutput.setMessageRate(getMessageRate());
      messageTraceOutput.setTimeUnit(getTimeUnit().name());
      messageTraceOutput.setTraceType(TRACE_TYPE);
      messageTraceOutput.setCount(count);
      messageTraceOutput.setAverage(average);
      messageTraceOutput.setMin(min);
      messageTraceOutput.setMax(max);
      messageTraceOutput.setExceedanceCount(exceedanceCount);
      messageTraceOutput.setExceedanceThreshold(maxFlag);
      messageTraceOutput.setTestDurationSec(testDurationSec);
      if (messageTraceLogger != null){
         messageTraceLogger.logMessageTraceOutput(messageTraceOutput);
      }
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
