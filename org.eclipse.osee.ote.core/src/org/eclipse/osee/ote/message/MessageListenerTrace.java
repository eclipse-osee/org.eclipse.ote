package org.eclipse.osee.ote.message;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;

/**
 * The {@code MessageListenerTrace} class will time the listeners of a message.  Only one can be set on a message at a time.
 * <pre>
 * This is measuring the timing of events that are occurring in the 
 * {@link org.eclipse.osee.ote.message.listener.MessageSystemListener#onDataAvailable(MessageData, DataType) MessageSystemListener.onDataAvailable()}
 * The ALL will measure the time from when the preNotify to the postNotify.
 * The other one will measure the time from right before the inner onDataAvailable is called to right after.
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
public class MessageListenerTrace extends MessageTimeTrace {

   public final String TRACE_TYPE = "MessageListenerTrace";
   private int maxAllListenerTime = Integer.MAX_VALUE;
   private int maxListenerTime = Integer.MAX_VALUE;
   private int listenerWaitTimeMs;

   private MessageTraceOutput messageTraceOutput;
   private MessageTraceOutput messageTraceOutputAll;
   private MessageTraceLogger messageTraceLogger;
   
   /**
    * @param environment - the test environment.
    * @param timeUnit - determines the resolution of the timing that is measured.
    * @param message - the message to measure
    * @param messageTraceLogger - logger to write output
    */
   @SuppressWarnings("rawtypes")
   public MessageListenerTrace(ITestEnvironmentAccessor environment, TimeUnit timeUnit, Message message, MessageTraceLogger messageTraceLogger) {
      super(environment, message, timeUnit);
      this.messageTraceOutput = new MessageTraceOutput();
      this.messageTraceOutputAll = new MessageTraceOutput();
      this.messageTraceLogger = messageTraceLogger;
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
      getMessage().setListenerTrace(this);
   }
   
   @Override
   public void stop(){
      super.stop();
      getMessage().clearListenerTrace();
   }

   public void setListenerWaitTimeMs(int listenerWaitTimeMs) {
      this.listenerWaitTimeMs = listenerWaitTimeMs;
   }

   @Override
   public synchronized void printResults(){
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
            long currentTime = getTimeUnit().convert(nanoDiff, TimeUnit.NANOSECONDS);
            
            if(currentTime >= 0){
               if(allmax < 0 || currentTime > allmax){
                  allmax = currentTime;
               }
               if(allmin < 0 || currentTime < allmin){
                  allmin = currentTime;
               }
               if(currentTime > maxAllListenerTime){
                  allexceedanceCount++;
                  String maxMessage = String.format("%s: count[%d] %d [%d (count)] [%s]", getName(), allCount, currentTime, allexceedanceCount, getTimeUnit().name());
                  System.out.println(maxMessage);
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
            long currentTime = getTimeUnit().convert(nanoDiff, TimeUnit.NANOSECONDS);
            
            if(currentTime >= 0){
               if(max < 0 || currentTime > max){
                  max = currentTime;
               }
               if(min < 0 || currentTime < min){
                  min = currentTime;
               }
               if(currentTime > maxListenerTime){
                  exceedanceCount++;
                  String maxMessage = String.format("%s: %s: count[%d] %d [%d (count)] [%s]", getName(), listenerLabel, count, currentTime, exceedanceCount, getTimeUnit().name());
                  System.out.println(maxMessage);
               }
               average = (((count-1) * average) + currentTime)/count;
            }
         }
      }
      
      messageTraceOutputAll.setMessage(getMessage().getMessageName());
      messageTraceOutputAll.setMessageRate(getMessageRate());
      messageTraceOutputAll.setTimeUnit(getTimeUnit().name());
      messageTraceOutputAll.setTraceType(TRACE_TYPE);
      messageTraceOutputAll.setCount(allCount);
      messageTraceOutputAll.setAverage(allaverage);
      messageTraceOutputAll.setMin(allmin);
      messageTraceOutputAll.setMax(allmax);
      messageTraceOutputAll.setExceedanceCount(allexceedanceCount);
      messageTraceOutputAll.setExceedanceThreshold(maxAllListenerTime);
      messageTraceOutputAll.setListenerWaitTimeMs(listenerWaitTimeMs);
      messageTraceOutputAll.setTestDurationSec(testDurationSec);

      messageTraceOutput.setMessage(getMessage().getMessageName());
      messageTraceOutput.setMessageRate(getMessageRate());
      messageTraceOutput.setTimeUnit(getTimeUnit().name());
      messageTraceOutput.setTraceType(TRACE_TYPE);
      messageTraceOutput.setCount(count);
      messageTraceOutput.setAverage(average);
      messageTraceOutput.setMin(min);
      messageTraceOutput.setMax(max);
      messageTraceOutput.setExceedanceCount(exceedanceCount);
      messageTraceOutput.setExceedanceThreshold(maxListenerTime);
      messageTraceOutput.setListenerWaitTimeMs(listenerWaitTimeMs);
      messageTraceOutput.setTestDurationSec(testDurationSec);
      
      messageTraceOutputAll.setExtraTraceOutput(messageTraceOutput);
      if (messageTraceLogger != null){
         messageTraceLogger.logMessageTraceOutput(messageTraceOutputAll);
      }
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
