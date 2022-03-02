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

import org.eclipse.osee.ote.messaging.dds.IDestination;

/**
 * @author Michael P. Masterson
 */
public class PubSubSubscriber implements IDestination {

   private BasicLogicalParticipant destinationParticipant;

   public PubSubSubscriber(BasicLogicalParticipant physicalParticipant) {
      this.destinationParticipant = physicalParticipant;
   }

   public PubSubSubscriber() {

   }

   /**
    * This is so we do not have to create new objects.
    *
    * @param physicalParticipant
    */
   public void set(BasicLogicalParticipant physicalParticipant) {
      this.destinationParticipant = physicalParticipant;
   }

   public BasicLogicalParticipant getParticipant() {
      return destinationParticipant;
   }
}
