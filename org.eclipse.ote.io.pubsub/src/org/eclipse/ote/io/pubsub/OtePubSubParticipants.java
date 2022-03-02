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

/**
 * @author Michael P. Masterson
 */
public enum OtePubSubParticipants implements BasicLogicalParticipant {

   OTE(0);

   private final int ID;
   private boolean isEnabled = true;

   private OtePubSubParticipants(int ID) {
      this.ID = ID;
   }

   @Override
   public int getId() {
      return ID;
   }

   @Override
   public String getName() {
      return name();
   }

   @Override
   public boolean isEnabled() {
      return isEnabled;
   }

   @Override
   public void setEnabled(boolean isEnabled) {
      this.isEnabled = isEnabled;
   }
}