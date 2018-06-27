package org.eclipse.osee.ote.message;

/**
 * The {@code MessageTraceOutput} class is a container for the different properties that 
 * are being measured and calculated for the different Message Trace classes.
 * 
 * 
 * @author Andy Jury
 *
 */
public class MessageTraceOutput {

   private String traceType;

   private String message;
   private double messageRate;
   private String timeUnit;
   
   private int count;

   private double average;
   private double max;
   private double min;
   
   private int exceedanceCount;
   private int exceedanceThreshold;
   
   private int testDurationSec;
   
   private Object extraTraceOutput;
   private int listenerWaitTimeMs;
   
   public void setTraceType(String traceType) {
      this.traceType = traceType;
   }

   public String getTraceType() {
      return traceType;
   }
   
   public void setMessage(String message) {
      this.message = message;
   }

   public String getMessage() {
      return message;
   }
   
   public void setMessageRate(double messageRate) {
      this.messageRate = messageRate;
   }
   
   public double getMessageRate() {
      return messageRate;
   }
   
   public void setTimeUnit(String timeUnit) {
      this.timeUnit = timeUnit;
   }
   
   public String getTimeUnit() {
      return timeUnit;
   }

   public int getCount() {
      return count;
   }

   public void setCount(int count) {
      this.count = count;
   }

   public int getExceedanceCount() {
      return exceedanceCount;
   }
   
   public void setExceedanceCount(int exceedanceCount) {
      this.exceedanceCount = exceedanceCount;
   }

   public double getAverage() {
      return average;
   }

   public void setAverage(double average) {
      this.average = average;
   }

   public double getMax() {
      return max;
   }

   public void setMax(double max) {
      this.max = max;
   }

   public double getMin() {
      return min;
   }

   public void setMin(double min) {
      this.min = min;
   }

   public int getExceedanceThreshold() {
      return exceedanceThreshold;
   }

   public void setExceedanceThreshold(int exceedanceThreshold) {
      this.exceedanceThreshold = exceedanceThreshold;
   }

   public int getTestDurationSec() {
      return testDurationSec;
   }

   public void setTestDurationSec(int testDurationSec) {
      this.testDurationSec = testDurationSec;
   }

   public Object getExtraTraceOutput() {
      return extraTraceOutput;
   }

   public void setExtraTraceOutput(Object extraTraceOutput) {
      this.extraTraceOutput = extraTraceOutput;
   }

   public int getListenerWaitTimeMs() {
      return listenerWaitTimeMs;
   }

   public void setListenerWaitTimeMs(int listenerWaitTimeMs) {
      this.listenerWaitTimeMs = listenerWaitTimeMs;
   }
}
