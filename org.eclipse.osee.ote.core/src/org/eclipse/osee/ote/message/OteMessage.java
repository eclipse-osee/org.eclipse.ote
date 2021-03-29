/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ote.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.condition.ICondition;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.MsgWaitResult;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.IMessageScheduleChangeListener;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;

/**
 * This class wraps the Message Object to limit the exposed API and make it simpler for the F18 test writer. This
 * class will contain both the reader and writer instance of a message and will decide which object is needed for each
 * function.
 * 
 * @author Michael P. Masterson
 * @param <M> The concrete Message type this class is wrapping
 */
public abstract class OteMessage<M extends Message> {
   protected final IMessageRequestor<Message> requestor;
   private final Class<M> sourceMessageClass;
   private M sourceMessageWriter;
   private M sourceMessageReader;

   public OteMessage(Class<M> sourceMessageClass, IMessageRequestor<Message> requestor) {
      this.sourceMessageClass = sourceMessageClass;
      this.requestor = requestor;
   }

   public M getMessageToWrite() {
      if (sourceMessageWriter == null || sourceMessageWriter.isDestroyed()) {
         sourceMessageWriter = requestor.getMessageWriter(sourceMessageClass);
      }
      return sourceMessageWriter;
   }

   public M getMessageToRead() {
      if (sourceMessageReader == null || sourceMessageReader.isDestroyed()) {
         sourceMessageReader = requestor.getMessageReader(sourceMessageClass);
      }
      return sourceMessageReader;
   }

   public void destroy() {
      if (this.sourceMessageReader != null) {
         sourceMessageReader.destroy();
      }

      if (this.sourceMessageWriter != null) {
         sourceMessageWriter.destroy();
      }

      sourceMessageReader = null;
      sourceMessageWriter = null;
   }

   public void setData(byte[] data) {
      getMessageToWrite().setData(data);
   }

   public void setData(ByteBuffer data, int length) {
      getMessageToWrite().setData(data, length);
   }

   public void setData(byte[] data, int length) {
      getMessageToWrite().setData(data, length);
   }

   public void setBackingBuffer(byte[] data) {
      getMessageToWrite().setBackingBuffer(data);
   }

   public byte[] getData() {
      return getMessageToRead().getData();
   }

   public MessageData getMemoryResource() {
      return getMessageToRead().getMemoryResource();
   }

   /**
    * Returns the number of byte words in the payload of this message.
    * 
    * @return number of bytes in the message payload
    */
   public int getPayloadSize() {
      return getMessageToRead().getPayloadSize();
   }

   public int getPayloadSize(DataType type) {
      return getMessageToRead().getPayloadSize(type);
   }

   /**
    * Returns the number of byte words in the header of this message.
    * 
    * @return the number of bytes in the header
    */
   public int getHeaderSize() {
      return getMessageToRead().getHeaderSize();
   }

   public int getHeaderSize(DataType type) {
      return getMessageToRead().getHeaderSize(type);
   }

   public void send() throws MessageSystemException {
      getMessageToWrite().send();
   }

   public void addSendListener(IMessageSendListener listener) {
      getMessageToWrite().addSendListener(listener);
   }

   public void removeSendListener(IMessageSendListener listener) {
      getMessageToWrite().removeSendListener(listener);
   }

   public boolean containsSendListener(IMessageSendListener listener) {
      return getMessageToWrite().containsSendListener(listener);
   }

   public void send(DataType type) throws MessageSystemException {
      getMessageToWrite().send(type);
   }

   /**
    * Turning off a message causes sends to be short-circuited and the message to be unscheduled.
    */
   public void turnOff() {
      getMessageToWrite().turnOff();
   }

   /**
    * Turning on message allows sends to work again & reschedules message if that is the default state defined by the
    * message constructor call.
    */
   public void turnOn() {
      getMessageToWrite().turnOn();
   }

   /**
    * @return if the message is turned off.
    */
   public boolean isTurnedOff() {
      return getMessageToWrite().isTurnedOff();
   }

   /**
    * This method schedules the message. There is also some code that allows the scheduled state to be updated in
    * Message Watch.
    */
   public void schedule() {
      getMessageToWrite().schedule();
   }

   /**
    * This method unschedules the message. The variable regularUnscheduledCalled is used to preserve unschedules that
    * are called in constructors, which is before the control message goes out for the first time.
    */
   public void unschedule() {
      getMessageToWrite().unschedule();
   }

   /**
    * This is called at the end of a script run to reset the "hard" unschedule variable that is used to preserve
    * unschedules called in constructors.
    */
   public void resetScheduling() {
      getMessageToWrite().resetScheduling();
   }

   /**
    * @return - double - rate of message
    */
   public double getRate() {
      return getMessageToRead().getRate();
   }

   /**
    * @return - int - phase of message
    */
   public int getPhase() {
      return getMessageToRead().getPhase();
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return getMessageToRead().getName();
   }

   private static final int TransmissionTimeoutDefault = 15000;

   @Override
   public String toString() {
      return getMessageToRead().toString();
   }

   /**
    * @return Returns the messageName.
    */
   public String getMessageName() {
      return getMessageToRead().getMessageName();
   }

   /**
    * Zeroize the entire body of this message.  Notice that the header and mask will not be affected.
    */
   public void zeroize() {
      getMessageToWrite().zeroize();
   }
   
   /**
    * Clears/zeroes out the entire mask for this message.
    */
   public void clearMask() {
      getMessageToWrite().clearMask();
   }

   /**
    * Verifies that the message is sent at least once using the default message timeout. DO NOT override this method in
    * production code.
    * 
    * @param accessor For logging results
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkForTransmission(ITestAccessor accessor) throws InterruptedException {
      return checkForTransmission(accessor, TransmissionTimeoutDefault);
   }

   /**
    * Verifies that the message is sent at least once within the time specified. DO NOT override this method in
    * production code.
    * 
    * @param accessor For logging
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkForTransmission(ITestAccessor accessor, int milliseconds) throws InterruptedException {
      return checkForTransmissions(accessor, 1, milliseconds);
   }

   /**
    * Verifies that the message is sent at least "numTransmission" times within the default message timeout. DO NOT
    * override this method in production code.
    * 
    * @param accessor For logging results
    * @param numTransmissions the number of transmissions to look for
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkForTransmissions(ITestAccessor accessor, int numTransmissions) throws InterruptedException {
      return checkForTransmissions(accessor, numTransmissions, TransmissionTimeoutDefault);
   }

   /**
    * Verifies that the message is sent at least "numTransmission" times within the time specified.
    * 
    * @param accessor For loging results
    * @param numTransmissions the number of transmission to look for
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkForTransmissions(ITestAccessor accessor, int numTransmissions, int milliseconds) throws InterruptedException {
      return getMessageToRead().checkForTransmissions(accessor, numTransmissions, milliseconds);
   }

   /**
    * Verifies that the message is not sent within the time specified.
    *
    * @param accessor For logging results
    * @param milliseconds the amount to time (in milliseconds) to check
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean checkForNoTransmissions(ITestEnvironmentMessageSystemAccessor accessor, int milliseconds) throws InterruptedException {
      return getMessageToRead().checkForNoTransmissions(accessor, milliseconds);
   }

   /**
    * Waits until message is sent at least once within the default message timeout.
    * 
    * @param accessor For logging results
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean waitForTransmission(ITestEnvironmentMessageSystemAccessor accessor) throws InterruptedException {
      return waitForTransmission(accessor, TransmissionTimeoutDefault);
   }

   /**
    * Waits until message is sent at least once within the time specified.
    * 
    * @param accessor For logging results
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean waitForTransmission(ITestEnvironmentMessageSystemAccessor accessor, int milliseconds) throws InterruptedException {
      return waitForTransmissions(accessor, 1, milliseconds);
   }

   /**
    * Waits until message is sent at least "numTransmission" times within the default message timeout.
    * 
    * @param accessor For logging results
    * @param numTransmissions the number of transmissions to look for
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean waitForTransmissions(ITestEnvironmentMessageSystemAccessor accessor, int numTransmissions) throws InterruptedException {
      return waitForTransmissions(accessor, numTransmissions, TransmissionTimeoutDefault);
   }

   /**
    * Waits until message is sent at least "numTransmission" times within the time specified.
    * 
    * @param accessor For logging results
    * @param numTransmissions The exact number of transmissions to wait
    * @param milliseconds the amount to time (in milliseconds) to allow
    * @return if the check passed
    * @throws InterruptedException
    */
   public boolean waitForTransmissions(ITestEnvironmentMessageSystemAccessor accessor, int numTransmissions, int milliseconds) throws InterruptedException {
      return getMessageToRead().waitForTransmissionsNoLog(accessor, numTransmissions, milliseconds);
   }

   public boolean waitForTransmissionsNoLog(ITestEnvironmentMessageSystemAccessor accessor, int numTransmissions, int milliseconds) throws InterruptedException {
      return getMessageToRead().waitForTransmissionsNoLog(accessor, numTransmissions, milliseconds);
   }

   public MsgWaitResult waitForCondition(ITestEnvironmentAccessor accessor, ICondition condition, boolean maintain, int milliseconds) throws InterruptedException {
      return getMessageToRead().waitForCondition(accessor, condition, maintain, milliseconds);
   }

   /**
    * @return Returns size value.
    */
   public int getMaxDataSize() {
      return getMessageToRead().getMaxDataSize();
   }

   public int getMaxDataSize(DataType type) {
      return getMessageToRead().getMaxDataSize(type);
   }

   public void addSchedulingChangeListener(IMessageScheduleChangeListener listener) {
      getMessageToWrite().addSchedulingChangeListener(listener);
   }

   public void removeSchedulingChangeListener(IMessageScheduleChangeListener listener) {
      getMessageToWrite().removeSchedulingChangeListener(listener);
   }

   /**
    * @return the memToDataMap
    */
   public Collection<ArrayList<MessageData>> getAllData() {
      return getMessageToRead().getAllData();
   }

   public String getTypeName() {
      return getName();
   }

   /**
    * This variable reflects whether a message is defined to start out being scheduled.
    * 
    * @return Returns the isScheduledFromStart.
    */
   public boolean isScheduledFromStart() {
      return getMessageToWrite().isScheduledFromStart();
   }

   /**
    * This variable reflects whether unsubscribe has been called on the message. The main purpose of this is to preserve
    * if an unschedule is called on a message from a constructor.
    * 
    * @return Returns the regularUnscheduleCalled.
    */
   public boolean isRegularUnscheduleCalled() {
      return getMessageToWrite().isRegularUnscheduleCalled();
   }

   public long getActivityCount() {
      return getMessageToRead().getActivityCount();
   }

   public long getSentCount() {
      return getMessageToWrite().getSentCount();
   }

   /**
    * Changes the rate a message is being published at. NOTE: This is only going to be allowed to be used on periodic
    * message & users are not allowed to set rate to zero.
    * 
    * @param newRate - hz
    */
   public void changeRate(double newRate) {
      getMessageToWrite().changeRate(newRate);
   }

   /**
    * Changes the rate back to the default rate.
    * 
    * @param accessor
    */
   public void changeRateToDefault(ITestEnvironmentMessageSystemAccessor accessor) {
      getMessageToWrite().changeRateToDefault(accessor);
   }

   public void sendWithLog(ITestAccessor accessor) {
      getMessageToWrite().sendWithLog(accessor);
   }

   public int getDefaultByteSize() {
      return getMessageToRead().getDefaultByteSize();
   }

   public int getDefaultOffset() {
      return getMessageToRead().getDefaultOffset();
   }

}