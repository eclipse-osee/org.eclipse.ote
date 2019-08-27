package org.eclipse.osee.ote.message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
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
public class MessageSendPeriodTrace extends MessageTimeTrace {
   
   public final String TRACE_TYPE = "MessageSendPeriodTrace";
   private SendTimer sendTimer;
   private int maxFlag = Integer.MAX_VALUE;
   private int minError = Integer.MIN_VALUE;
   private int maxError = Integer.MAX_VALUE;
   
   private MessageTraceOutput messageTraceOutput;
   private MessageTraceLogger messageTraceLogger;
   
   /**
    * @param environment - the test environment.
    * @param timeUnit - determines the resolution of the timing that is measured.
    * @param message - the message to measure
    * @param messageTraceLogger - logger to write output
    */
   @SuppressWarnings("rawtypes")
   public MessageSendPeriodTrace(ITestEnvironmentAccessor environment, TimeUnit timeUnit, Message message, MessageTraceLogger messageTraceLogger) {
      super(environment, message, timeUnit);
      sendTimer = new SendTimer(this);
      this.messageTraceOutput = new MessageTraceOutput();
      this.messageTraceLogger = messageTraceLogger;
   }
   
   /**
    * 
    * @param expected the expected period in the constructed TimeUnit
    * @param maxError the max variance off of the period in the constructed TimeUnit
    */
   public void setMaxVariance(int expected, int maxError){
      this.maxFlag = maxError;
      this.minError = expected - maxError;
      this.maxError = expected + maxError;
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
            long currentTime = getTimeUnit().convert(nanoDiff, TimeUnit.NANOSECONDS);
            
            if(currentTime >= 0){
               if(max < 0 || currentTime > max){
                  max = currentTime;
               }
               if(min < 0 || currentTime < min){
                  min = currentTime;
               }
               if(currentTime > maxError || currentTime < minError){
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
         // Intentionally empty block
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