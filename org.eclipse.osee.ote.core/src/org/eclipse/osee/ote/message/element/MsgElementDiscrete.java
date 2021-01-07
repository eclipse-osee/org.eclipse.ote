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
package org.eclipse.osee.ote.message.element;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.interfaces.IMessageRequestor;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 * @param <T> The underlying Comparable type of the element
 */
public abstract class MsgElementDiscrete<T extends Comparable<T>> {

   private final Class<? extends Message> sourceMessageClass;
   private final DiscreteElement<T> sourceElement;
   private final IMessageRequestor<Message> requestor;
   private Message sourceMessageWriter;
   private Message sourceMessageReader;
   private DiscreteElement<T> elementToWrite;
   private DiscreteElement<T> elementToRead;

   public MsgElementDiscrete(Class<? extends Message> sourceMessageClass, DiscreteElement<T> sourceElement, IMessageRequestor<Message> requestor) {
      this.sourceMessageClass = sourceMessageClass;
      this.sourceElement = sourceElement;
      this.requestor = requestor;
   }

   public void setNoLog(T obj) {
      getElementToWrite().setNoLog(obj);
   }
   
   /**
    * Zeroize this element's data and mask.
    */
   public void unset() {
      getElementToWrite().unset();
   }

   @SuppressWarnings("unchecked")
   protected DiscreteElement<T> getElementToWrite() {
      if (sourceMessageWriter == null || sourceMessageWriter.isDestroyed() || elementToWrite == null) {
         sourceMessageWriter = requestor.getMessageWriter(sourceMessageClass);
         elementToWrite = sourceMessageWriter.getElement(sourceElement.getElementName(), sourceElement.getClass());
      }
      return elementToWrite;
   }

   @SuppressWarnings("unchecked")
   protected DiscreteElement<T> getElementToRead() {
      if (sourceMessageReader == null || sourceMessageReader.isDestroyed() || elementToRead == null) {
         sourceMessageReader = requestor.getMessageReader(sourceMessageClass);
         elementToRead = sourceMessageReader.getElement(sourceElement.getElementName(), sourceElement.getClass());
      }
      return elementToRead;
   }

   public T getNoLog() {
      return getElementToRead().getNoLog();
   }

   public T peek(ITestAccessor accessor) {
      return getElementToWrite().get(accessor);
   }

   public T peekNoLog() {
      return getElementToWrite().getNoLog();
   }

   public String toString(T obj) {
      return getElementToRead().toString(obj);
   }

   public String valueOf() {
      return getNoLog().toString();
   }

   public void parseAndSet(ITestEnvironmentAccessor accessor, String value) throws IllegalArgumentException {
      getElementToWrite().parseAndSet(accessor, value);
   }

   /**
    * Sets the element to the "value" passed.
    * 
    * @param accessor Reference to the accessor.
    * @param value The value to set.
    */
   public void set(ITestEnvironmentAccessor accessor, T value) {
      getElementToWrite().setNoLog(value);
   }

   /**
    * Verifies that the element is set to "value".
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param value Expected value
    * @return if the check passed
    */
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, T value) {
      return getElementToRead().check(accessor, checkGroup, value);
   }

   public boolean checkNT(ITestAccessor accessor, CheckGroup checkGroup, T value) {
      return getElementToRead().checkNT(accessor, checkGroup, value);
   }

   public boolean checkNotNT(ITestAccessor accessor, CheckGroup checkGroup, T value) {
      return getElementToRead().checkNotNT(accessor, checkGroup, value);
   }

   public final boolean check(ITestAccessor accessor, T value) {
      return this.check(accessor, (CheckGroup) null, value);
   }

   /**
    * Verifies that the element is set to "value" within the number of "milliseconds" passed.
    * 
    * @param accessor
    * @param value Expected value.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public final boolean check(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return check(accessor, (CheckGroup) null, value, milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @return if the check passed
    */
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive) {
      return getElementToRead().checkRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive);
   }

   public boolean checkRangeNT(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive) {
      return getElementToRead().checkRangeNT(accessor, minValue, minInclusive, maxValue, maxInclusive);
   }

   public boolean checkRangeNT(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int millis) throws InterruptedException {
      return getElementToRead().checkRangeNT(accessor, minValue, minInclusive, maxValue, maxInclusive, millis);
   }

   /**
    * Verifies that the element is set to a value within the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @return if the check passed
    */
   public final boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue) {
      return checkRange(accessor, checkGroup, minValue, true, maxValue, true);
   }

   /**
    * Verifies that the element is NOT set to "value".
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param value value to test against
    * @return if the check passed
    */
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, T value) {

      return getElementToRead().checkNot(accessor, checkGroup, value);
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified. Assumes
    * range is inclusive.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public final T checkMaintainRange(ITestAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkMaintainRange(accessor, null, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public final T checkMaintainRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return checkMaintainRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive,
         milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified. Assumes
    * range is inclusive.
    * 
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public final T checkMaintainNotRange(ITestAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkMaintainNotRange(accessor, null, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Verifies that the element is not set to a value within the range specified for the entire time specified.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public final T checkMaintainNotRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return checkMaintainNotRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive,
         milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified. Assumes
    * range is inclusive.
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public final T checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkMaintainRange(accessor, checkGroup, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    * to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    * to the range value.
    * @param milliseconds Number of milliseconds to wait for the element to be outside the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public final boolean checkNotRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return checkNotRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    * to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    * to the range value.
    * @return if the check passed
    * @throws InterruptedException
    */
   public final boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive) throws InterruptedException {
      return checkNotRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive, 0);
   }

   /**
    * Waits until the element equals the "value" passed. Returns last value observed upon a time out.
    * 
    * @param accessor Reference to the accessor.
    * @param value The expected value to wait for.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value found. Either value expected or value found at timeout.
    * @throws InterruptedException
    */
   public T waitForValue(ITestEnvironmentAccessor accessor, T value, int milliseconds) throws InterruptedException {

      return getElementToRead().waitForValue(accessor, value, milliseconds);
   }

   /**
    * Waits until the element has a value other than the "value" passed. Returns last value observed upon a time out.
    * 
    * @param accessor Reference to the accessor.
    * @param value The expected value to wait for.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T waitForNotValue(ITestEnvironmentAccessor accessor, T value, int milliseconds) throws InterruptedException {

      return getElementToRead().waitForNotValue(accessor, value, milliseconds);
   }

   /**
    * Waits until the element has a value within the range specified. Either end of the range can be inclusive or not.
    * 
    * @param accessor Reference to the accessor.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T waitForRange(ITestEnvironmentAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return getElementToRead().waitForRange(accessor, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   /**
    * Waits until the element has a value within the range specified. Assumes the range is inclusive.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T waitForRange(ITestEnvironmentAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return waitForRange(accessor, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Waits until the element has a value within the range specified. Either end of the range can be inclusive or not.
    * 
    * @param accessor Reference to the accessor.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    * to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    * to the range value.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T waitForNotRange(ITestEnvironmentAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return getElementToRead().waitForNotRange(accessor, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   /**
    * Waits until the element has a value within the range specified. Assumes range is inclusive.
    * 
    * @param accessor
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public T waitForNotRange(ITestEnvironmentAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return waitForRange(accessor, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Waits until the element has a value other than the "value" passed. Returns last value observed upon a timout.
    * 
    * @param accessor
    * @param value The expected value to wait for.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException
    */
   public final T waitNotValue(ITestEnvironmentAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return waitForNotValue(accessor, value, milliseconds);
   }

   /**
    * Verifies that the element is set to "value" within the number of "milliseconds" passed.
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param value Expected value.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException
    */
   public boolean check(ITestAccessor accessor, CheckGroup checkGroup, T value, int milliseconds) throws InterruptedException {
      return getElementToRead().check(accessor, checkGroup, value, milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait for the element to be in the range.
    * @return if the check passed
    * @throws InterruptedException d
    */
   public boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return getElementToRead().checkRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive,
         milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minimum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait for the element to be within the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public final boolean checkRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return checkRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @return if the check passed
    */
   public final boolean checkRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive) {
      return checkRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive);
   }

   /**
    * Verifies that the element is set to a value within the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait for the element to be within the range.
    * @return if the check passed
    * @throws InterruptedException
    */
   public final boolean checkRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkRange(accessor, checkGroup, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait for the element to be within the range.
    * @return if the check passed
    * @throws InterruptedException 
    */
   public final boolean checkRange(ITestAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return this.checkRange(accessor, (CheckGroup) null, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @return if the check passed
    */
   public final boolean checkRange(ITestAccessor accessor, T minValue, T maxValue) {
      return checkRange(accessor, (CheckGroup) null, minValue, true, maxValue, true);
   }

   /**
    * Verifies that the element is set to some value other than "value" within the number of "milliseconds" passed.
    * Passes if at any point with in the time allowed, the elment is set to a value other than "value".
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param value value to test against.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException 
    */
   public boolean checkNot(ITestAccessor accessor, CheckGroup checkGroup, T value, int milliseconds) throws InterruptedException {
      return getElementToRead().checkNot(accessor, checkGroup, value, milliseconds);
   }

   /**
    * Verifies that the element is NOT set to "value".
    * 
    * @param accessor for logging
    * @param value value to test against
    * @return if the check passed
    */
   public final boolean checkNot(ITestAccessor accessor, T value) {
      return checkNot(accessor, (CheckGroup) null, value);
   }

   /**
    * Verifies that the element is set to some value other than "value" within the number of "milliseconds" passed.
    * Passes if at any point with in the time allowed, the element is set to a value other than "value".
    * 
    * @param accessor for logging
    * @param value value to test against.
    * @param milliseconds Number of milliseconds to wait for the element to equal the "value".
    * @return If the check passed.
    * @throws InterruptedException 
    */
   public final boolean checkNot(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return checkNot(accessor, (CheckGroup) null, value, milliseconds);
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    * to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    * to the range value.
    * @param milliseconds 
    * @return if the check passed
    * @throws InterruptedException 
    */
   public boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return getElementToRead().checkNotRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive,
         milliseconds);
   }

   public boolean checkNotRangeNT(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return getElementToRead().checkNotRangeNT(accessor, minValue, minInclusive, maxValue, maxInclusive, milliseconds);
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Assumes that both ends of the range are
    * inclusive. Therefore observed value may not equal either of the range values.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @return if the check passed
    * @throws InterruptedException 
    */
   public final boolean checkNotRange(ITestAccessor accessor, T minValue, T maxValue) throws InterruptedException {
      return checkNotRange(accessor, (CheckGroup) null, minValue, true, maxValue, true);
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait for the element to be outside the range.
    * @return if the check passed
    * @throws InterruptedException 
    */
   public final boolean checkNotRange(ITestAccessor accessor, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkNotRange(accessor, (CheckGroup) null, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Assumes that both ends of the range are
    * inclusive.
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait for the element to be outside the range.
    * @return if the check passed
    * @throws InterruptedException 
    */
   public final boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkNotRange(accessor, checkGroup, minValue, true, maxValue, true, milliseconds);
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Assumes that both ends of the range are
    * inclusive. Therefore observed value may not equal either of the range values.
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @return if the check passed
    * @throws InterruptedException 
    */
   public final boolean checkNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue) throws InterruptedException {
      return checkNotRange(accessor, checkGroup, minValue, true, maxValue, true);
   }

   /**
    * Verifies that the element is set to a value outside the range specified. Either end of the range can be set to be
    * inclusive or not.
    * 
    * @param accessor for logging
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    * to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    * to the range value.
    * @return if the check passed
    * @throws InterruptedException 
    */
   public final boolean checkNotRange(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive) throws InterruptedException {
      return checkNotRange(accessor, (CheckGroup) null, minValue, minInclusive, maxValue, maxInclusive);
   }

   /**
    * Verifies that the element is set to the "value" passed for the entire time passed into "milliseconds". Returns
    * value found that caused failure or last value observed if time expires.
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param value 
    * @param milliseconds 
    * @return last value observed. Either value expected or value found at timeout.
    * @throws InterruptedException 
    */
   public T checkMaintain(ITestAccessor accessor, CheckGroup checkGroup, T value, int milliseconds) throws InterruptedException {
      return getElementToRead().checkMaintain(accessor, checkGroup, value, milliseconds);
   }

   public T checkMaintainNT(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return getElementToRead().checkMaintainNT(accessor, value, milliseconds);
   }

   public T checkMaintainNotNT(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return getElementToRead().checkMaintainNotNT(accessor, value, milliseconds);
   }

   /**
    * Verifies that the element is set to the "value" passed for the entire time passed into "milliseconds". Returns
    * value found that caused failure or last value observed if time expires.
    * 
    * @param accessor for logging
    * @param value 
    * @param milliseconds 
    * @return last value observed. Either value expected or value found at timeout.
    * @throws InterruptedException 
    */
   public final T checkMaintain(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return checkMaintain(accessor, (CheckGroup) null, value, milliseconds);
   }

   /**
    * Verifies that the element is set to a value other than the "value" passed for the entire time passed into
    * "milliseconds". Returns value found that caused failure or last value observed if time expires.
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param value 
    * @param milliseconds 
    * @return last value observed
    * @throws InterruptedException 
    */
   public T checkMaintainNot(ITestAccessor accessor, CheckGroup checkGroup, T value, int milliseconds) throws InterruptedException {
      return getElementToRead().checkMaintainNot(accessor, checkGroup, value, milliseconds);
   }

   /**
    * Verifies that the element is set to the "value" passed for the entire time passed into "milliseconds". Returns
    * value found that caused failure or last value observed if time expires.
    * 
    * @param accessor for logging
    * @param value 
    * @param milliseconds 
    * @return last value observed
    * @throws InterruptedException 
    */
   public final T checkMaintainNot(ITestAccessor accessor, T value, int milliseconds) throws InterruptedException {
      return checkMaintainNot(accessor, (CheckGroup) null, value, milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified.
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public T checkMaintainRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return getElementToRead().checkMaintainRange(accessor, checkGroup, minValue, minInclusive, maxValue, maxInclusive,
         milliseconds);
   }

   public T checkMaintainRangeNT(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return getElementToRead().checkMaintainRangeNT(accessor, minValue, minInclusive, maxValue, maxInclusive,
         milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified.
    * 
    * @param accessor Reference to the accessor.
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param minInclusive If the minumum value of the range is inclusive. If true the actual value must not < and not =
    * to the range value.
    * @param maxValue The maximum value of the range.
    * @param maxInclusive If the maximum value of the range is inclusive. If true the actual value must not > and not =
    * to the range value.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public T checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return getElementToRead().checkMaintainNotRange(accessor, checkGroup, minValue, minInclusive, maxValue,
         maxInclusive, milliseconds);
   }

   public T checkMaintainNotRangeNT(ITestAccessor accessor, T minValue, boolean minInclusive, T maxValue, boolean maxInclusive, int milliseconds) throws InterruptedException {
      return getElementToRead().checkMaintainNotRangeNT(accessor, minValue, minInclusive, maxValue, maxInclusive,
         milliseconds);
   }

   /**
    * Verifies that the element is set to a value within the range specified for the entire time specified. Assumes
    * range is inclusive.
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param minValue The minimum value of the range.
    * @param maxValue The maximum value of the range.
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public final T checkMaintainNotRange(ITestAccessor accessor, CheckGroup checkGroup, T minValue, T maxValue, int milliseconds) throws InterruptedException {
      return checkMaintainNotRange(accessor, checkGroup, minValue, true, maxValue, true, milliseconds);
   }

   public boolean checkPulse(ITestAccessor accessor, CheckGroup checkGroup, T pulsedValue, T nonPulsedValue, int milliseconds, int pulses) throws InterruptedException {
      return getElementToRead().checkPulse(accessor, checkGroup, pulsedValue, nonPulsedValue, milliseconds, pulses);
   }

   public final boolean checkPulse(ITestAccessor accessor, T pulsedValue, T nonPulsedValue) throws InterruptedException {
      return checkPulse(accessor, null, pulsedValue, nonPulsedValue);
   }

   public final boolean checkPulse(ITestAccessor accessor, CheckGroup checkGroup, T pulsedValue, T nonPulsedValue) throws InterruptedException {
      return checkPulse(accessor, checkGroup, pulsedValue, nonPulsedValue, 1000, 2);
   }

   public final boolean checkPulse(ITestAccessor accessor, T pulsedValue, T nonPulsedValue, int milliseconds) throws InterruptedException {
      return checkPulse(accessor, null, pulsedValue, nonPulsedValue, milliseconds, 2);
   }

   public final boolean checkPulse(ITestAccessor accessor, T pulsedValue, T nonPulsedValue, int milliseconds, int pulses) throws InterruptedException {
      return checkPulse(accessor, null, pulsedValue, nonPulsedValue, milliseconds, pulses);
   }

   public boolean checkPulse(ITestAccessor accessor, CheckGroup checkGroup, T pulsedValue, T nonPulsedValue, int milliseconds) throws InterruptedException {
      return checkPulse(accessor, checkGroup, pulsedValue, nonPulsedValue, milliseconds, 2);
   }

   public T valueOf(MemoryResource mem) {
      return getElementToRead().valueOf(mem);
   }

   @Override
   public String toString() {
      return getElementToRead().toString();
   }

   /**
    * Verifies that the element is set to a value in the "list".
    * 
    * @param accessor for logging
    * @param list List of values to check for
    * @return if check passed
    */
   public final boolean checkInList(ITestAccessor accessor, T[] list) {
      return checkList(accessor, null, true, list);
   }

   /**
    * Verifies that the element is set to a value NOT in the "list".
    * 
    * @param accessor for logging
    * @param list List of values to check for
    * @return if check passed
    */
   public final boolean checkNotInList(ITestAccessor accessor, T[] list) {
      return checkList(accessor, null, false, list);
   }

   /**
    * Verifies that the element is set to a value IN or NOT IN the "list" passed. "isInList" determines if checking for
    * IN the list or NOT.
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param isInList Determines if checking for the element's value to be in or not in the "list". Passing TRUE will
    * test for IN the "list".
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException 
    */
   public boolean checkList(ITestAccessor accessor, CheckGroup checkGroup, boolean isInList, T[] list, int milliseconds) throws InterruptedException {
      return getElementToRead().checkList(accessor, checkGroup, isInList, list, milliseconds);
   }

   /**
    * Verifies that the element is set to a value NOT in the "list".
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param list List of values to check for
    * @return if check passed
    */
   public final boolean checkNotInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list) {
      return this.checkList(accessor, checkGroup, false, list);
   }

   /**
    * Verifies that the element is set to a value NOT in the "list".
    * 
    * @param accessor for logging
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException 
    */
   public final boolean checkNotInList(ITestAccessor accessor, T[] list, int milliseconds) throws InterruptedException {
      return this.checkList(accessor, (CheckGroup) null, false, list, milliseconds);
   }

   /**
    * Verifies that the element is set to a value IN or NOT IN the "list" passed. "wantInList" determines if checking
    * for IN the list or NOT.
    * 
    * @param accessor for logging
    * @param wantInList Determines if checking for the element's value to be in or not in the "list". Passing TRUE will
    * test for IN the "list".
    * @param list List of values to check for
    * @return if check passed
    */
   public final boolean checkList(ITestAccessor accessor, boolean wantInList, T[] list) {
      return this.checkList(accessor, null, wantInList, list);
   }

   /**
    * Verifies that the element is set to a value IN or NOT IN the "list" passed. "isInList" determines if checking for
    * IN the list or NOT.
    * 
    * @param accessor for logging
    * @param isInList Determines if checking for the element's value to be in or not in the "list". Passing TRUE will
    * test for IN the "list".
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException 
    */
   public final boolean checkList(ITestAccessor accessor, boolean isInList, T[] list, int milliseconds) throws InterruptedException {
      return checkList(accessor, (CheckGroup) null, isInList, list, milliseconds);
   }

   /**
    * Verifies that the element is set to a value IN or NOT IN the "list" passed. "wantInList" determines if checking
    * for IN the list or NOT.
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param wantInList Determines if checking for the element's value to be in or not in the "list". Passing TRUE will
    * test for IN the "list".
    * @param list List of values to check for
    * @return if check passed
    */
   public boolean checkList(ITestAccessor accessor, CheckGroup checkGroup, boolean wantInList, T[] list) {
      return getElementToRead().checkList(accessor, checkGroup, wantInList, list);
   }

   /**
    * Verifies that the element is set to a value in the "list".
    * 
    * @param accessor for logging
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException 
    */
   public final boolean checkInList(ITestAccessor accessor, T[] list, int milliseconds) throws InterruptedException {
      return this.checkList(accessor, (CheckGroup) null, true, list, milliseconds);
   }

   /**
    * Verifies that the element is set to a value NOT in the "list".
    * 
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * 
    * @param accessor for logging
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException 
    */
   public final boolean checkNotInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list, int milliseconds) throws InterruptedException {
      return this.checkList(accessor, checkGroup, false, list, milliseconds);
   }

   /**
    * Verifies that the element is set to a value in the list for the entire time passed into milliseconds.
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param list The list of values to check against
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public final T checkMaintainInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list, int milliseconds) throws InterruptedException {
      return this.checkMaintainList(accessor, checkGroup, list, true, milliseconds);
   }

   /**
    * Verifies that the element is set to a value not in the list for the entire time passed into milliseconds.
    * 
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * 
    * @param accessor for logging
    * @param list The list of values to check against
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public final T checkMaintainNotInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list, int milliseconds) throws InterruptedException {
      return this.checkMaintainList(accessor, checkGroup, list, false, milliseconds);
   }

   /**
    * Verifies that the element is set to a value in the "list".
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param list List of values to check for
    * @param milliseconds Number of milliseconds to wait
    * @return if check passed
    * @throws InterruptedException 
    */
   public final boolean checkInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list, int milliseconds) throws InterruptedException {
      return this.checkList(accessor, checkGroup, true, list, milliseconds);
   }

   /**
    * Verifies that the element is set to a value in the "list".
    * 
    * @param accessor for logging
    * @param checkGroup If this check is part of a larger set of checks which another method is going to log then the
    * reference to the CheckGroup must be passed and this method will add the result of the check to the group with out
    * logging a point.
    * <p>
    * If an outside method is not going to log the check then a <b>null </b> reference should be passed and this method
    * will log the test point.
    * @param list List of values to check for
    * @return if check passed
    */
   public final boolean checkInList(ITestAccessor accessor, CheckGroup checkGroup, T[] list) {
      return checkList(accessor, checkGroup, true, list);
   }

   /**
    * Waits until the element is set to a value either in or not in the "list" as determined by "isInList".
    * 
    * @param accessor for logging
    * @param list The list of values to check against
    * @param isInList If the value is expected to be in or not in the "list"
    * @param milliseconds Number of milliseconds to wait before failing.
    * @return last value observed
    * @throws InterruptedException 
    */
   public T waitForList(ITestAccessor accessor, T[] list, boolean isInList, int milliseconds) throws InterruptedException {
      return getElementToRead().waitForList(accessor, list, isInList, milliseconds);
   }

   public T checkMaintainList(ITestAccessor accessor, CheckGroup checkGroup, T[] list, boolean isInList, int milliseconds) throws InterruptedException {
      return getElementToRead().checkMaintainList(accessor, checkGroup, list, isInList, milliseconds);
   }

   /**
    * Sets the element to the first enumeration for the wait time and then it sets it to the second enumeration.
    * 
    * @param accessor for logging
    * @param value1 
    * @param value2 
    * @param milliseconds 
    * @throws InterruptedException 
    */
   public synchronized void toggle(ITestEnvironmentAccessor accessor, T value1, T value2, int milliseconds) throws InterruptedException {
      getElementToWrite().toggle(accessor, value1, value2, milliseconds);
   }

   /**
    * gets this element's current value. Does logging
    * 
    * @param accessor for logging
    * @return the value of this element
    */
   public T get(ITestEnvironmentAccessor accessor) {
      return getElementToRead().get(accessor);
   }

   public T elementMask(T value) {
      return getElementToRead().elementMask(value);
   }

   /**
    * Sets the element to the "value" passed and immediately sends the message that contains it..
    * 
    * @param accessor
    * @param value The value to set.
    */
   public void setAndSend(ITestEnvironmentAccessor accessor, T value) {
      getElementToWrite().setAndSend(accessor, value);
   }

}
