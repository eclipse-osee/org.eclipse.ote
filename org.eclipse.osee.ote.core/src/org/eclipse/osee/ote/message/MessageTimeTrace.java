/*
 * Created on Oct 16, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message;

import java.util.concurrent.TimeUnit;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;

public class MessageTimeTrace extends TimeTrace {
   
   protected int testDurationSec = 10;
   private ITestEnvironmentAccessor environment;
   @SuppressWarnings("rawtypes")
   private Message message;
   private TimeUnit timeUnit;
   private double messageRate;

   @SuppressWarnings("rawtypes")
   public MessageTimeTrace(ITestEnvironmentAccessor environment, Message message, TimeUnit timeUnit) {
      super(message.getName());
      this.environment = environment;
      this.message = message;
      this.timeUnit= timeUnit;
      this.messageRate = message.getRate();
   }

   public void testWait() throws InterruptedException {
      environment.getTimerCtrl().envWait(testDurationSec*1000);
   }

   public void runTraceTest() throws InterruptedException {
      start();
      testWait();
      stop();
   }

   public void setTestDurationTime(int testDurationSec) {
      this.testDurationSec = testDurationSec;
   }
   
   @SuppressWarnings("rawtypes")
   public Message getMessage() {
      return message;
   }
   
   public TimeUnit getTimeUnit() {
      return timeUnit;
   }
   
   public double getMessageRate() {
      return messageRate;
   }

}
