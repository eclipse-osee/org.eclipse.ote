/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.io.pubsub;

import org.eclipse.osee.ote.message.Message;

/**
 * @author Michael P. Masterson
 */
public class PubSubMessage extends Message {

   public PubSubMessage(String name, int defaultByteSize, int defaultOffset, boolean isScheduled, int phase, double rate) {
      super(name, defaultByteSize, defaultOffset, isScheduled, phase, rate);
   }

   protected void addPublisher(BasicLogicalParticipant part) {
   }

   protected void addSubscriber(BasicLogicalParticipant part) {

   }
}