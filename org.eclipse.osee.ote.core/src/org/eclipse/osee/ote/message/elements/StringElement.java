/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.elements;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.condition.EmptyStringCondition;
import org.eclipse.osee.ote.message.condition.StringTrimCondition;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingStringElement;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class StringElement extends DiscreteElement<String> {

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asStringElement(this);
   }

   public StringElement(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb) {
      this(message, elementName, messageData, byteOffset, msb, lsb, msb, lsb);
   }

   public StringElement(Message message, String elementName, MessageData messageData, int bitOffset, int bitLength) {
      super(message, elementName, messageData, bitOffset, bitLength);
   }

   public StringElement(Message message, String elementName, MessageData messageData, int byteOffset, int msb, int lsb, int originalLsb, int originalMsb) {
      super(message, elementName, messageData, byteOffset, msb, lsb, originalLsb, originalMsb);
   }

   @Override
   public StringElement findElementInMessages(Collection<? extends Message> messages) {
      return (StringElement) super.findElementInMessages(messages);
   }
   
   @Override
   public StringElement switchMessages(Collection<? extends Message<?,?,?>> messages) {
      return (StringElement) super.switchMessages(messages);
   }

   @Override
   public String toString(String obj) {
      return obj;
   }

   @Override
   public void setValue(String value) {
      getMsgData().getMem().setASCIIString(value, byteOffset, msb, lsb);
   }

   @Override
   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      set(accessor, value);
   }

   @Override
   public String getValue() {
      return getMsgData().getMem().getASCIIString(byteOffset, msb, lsb);
   }

   /**
    * copies this elements chars into the given array. The array must be big enough to contain this elements data or an
    * ArrayIndexOutOfBoundsException will be thrown. Does not exclude or stop on null (\0) characters in the element
    * data.
    * 
    * @param destination the destination array that will receive the char data
    * @return the actual number of characters copied. The destination array will contain undefined data starting at this
    * index until the end of the char array.
    * @throws ArrayIndexOutOfBoundsException if the destination array is too small
    */
   public int getChars(char[] destination) throws ArrayIndexOutOfBoundsException {
      return getMsgData().getMem().getASCIIChars(byteOffset, msb, lsb, destination);
   }

   public void setChars(final char[] data) {
      final int sizeInBytes = (lsb - msb + 1) / 8;
      if (data.length > sizeInBytes) {
         OseeLog.log(getClass(), Level.WARNING,
            "char[] passed to setChars() is bigger than the element, setting with subset of passed data");
      } else if (data.length < sizeInBytes) {
         OseeLog.log(getClass(), Level.WARNING,
            "char[] passed to setChars() is smaller than element, setting subset of element");
      }
      MemoryResource memory = getMsgData().getMem();
      final int bytesToSet = Math.min(data.length, sizeInBytes);
      for (int i = 0; i < bytesToSet; i++) {
         memory.setByte(data[i], byteOffset + i, 0, 7);
      }
   }

   public boolean equals(String other) {
      return getMsgData().getMem().asciiEquals(byteOffset, msb, lsb, other);
   }

   @Override
   public String valueOf(MemoryResource mem) {
      return mem.getASCIIString(byteOffset, msb, lsb);
   }

   /**
    * Verifies that the element is set to "value" within the number of "milliseconds" passed.
    * 
    * @param value Expected value.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    */
   public boolean checkTrimWhiteSpace(ITestAccessor accessor, String value, int milliseconds) throws InterruptedException {
      if (accessor == null) {
         throw new NullPointerException("The parameter accessor is null");
      }

      accessor.getLogger().methodCalledOnObject(accessor, getFullName(),
         new MethodFormatter().add(value).add(milliseconds), getMessage());
      final StringTrimCondition c = new StringTrimCondition(this, value);

      MsgWaitResult result = getMessage().waitForCondition(accessor, c, false, milliseconds);
      CheckPoint passFail =
         new CheckPoint(getFullName(), toString(value), toString(c.getLastCheckValue()), result.isPassed(),
            result.getElapsedTime());

      accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);

      accessor.getLogger().methodEnded(accessor);
      return passFail.isPass();
   }

   /**
    * Sets the element to the "value" passed and immediately sends the message that contains it..
    * 
    * @param value The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, String value) {
      parseAndSet(accessor, value);
      super.sendMessage();
   }

   @Override
   protected NonMappingStringElement getNonMappingElement() {
      return new NonMappingStringElement(this);
   }

   @Override
   public void zeroize() {
      int sizeInBytes = (lsb - msb + 1) / 8;
      getMsgData().getMem().zeroizeFromOffset(byteOffset, sizeInBytes);
   }

   public boolean isEmpty() {
      return new EmptyStringCondition(this).check();
   }

   public boolean checkEmpty(ITestAccessor accessor, int milliseconds) throws InterruptedException {
      if (accessor == null) {
         throw new NullPointerException("The parameter accessor is null");
      }

      accessor.getLogger().methodCalledOnObject(accessor, getFullName(), new MethodFormatter().add(milliseconds),
         getMessage());
      final EmptyStringCondition c = new EmptyStringCondition(this);

      MsgWaitResult result = getMessage().waitForCondition(accessor, c, false, milliseconds);
      CheckPoint passFail =
         new CheckPoint(getFullName(), "Empty", result.isPassed() ? "Empty" : "Not Empty", result.isPassed(),
            result.getElapsedTime());

      accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);

      accessor.getLogger().methodEnded(accessor);
      return passFail.isPass();
   }

   public boolean checkMaintainEmpty(ITestAccessor accessor, int milliseconds) throws InterruptedException {
      if (accessor == null) {
         throw new NullPointerException("The parameter accessor is null");
      }

      accessor.getLogger().methodCalledOnObject(accessor, getFullName(), new MethodFormatter().add(milliseconds),
         getMessage());
      final EmptyStringCondition c = new EmptyStringCondition(this);

      MsgWaitResult result = getMessage().waitForCondition(accessor, c, true, milliseconds);
      CheckPoint passFail =
         new CheckPoint(getFullName(), "Empty", result.isPassed() ? "Empty" : "Not Empty", result.isPassed(),
            result.getElapsedTime());

      accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), passFail);

      accessor.getLogger().methodEnded(accessor);
      return passFail.isPass();
   }

   @Override
   public String elementMask(String value) {
      return value;
   }

}