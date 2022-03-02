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

import org.eclipse.osee.ote.messaging.dds.ISource;

/**
 * @author Michael P. Masterson
 */
public class PubSubPublisher implements ISource {
   private BasicLogicalParticipant source;

   public PubSubPublisher(BasicLogicalParticipant source) {
      this.source = source;
   }

   public BasicLogicalParticipant getParticipant() {
      return source;
   }

   public void set(BasicLogicalParticipant source) {
      this.source = source;
   }
}
