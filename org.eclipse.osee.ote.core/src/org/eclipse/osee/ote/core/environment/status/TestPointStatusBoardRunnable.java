/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.core.environment.status;

import org.eclipse.osee.ote.core.environment.status.msg.TestPointUpdateMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.osgi.service.event.EventAdmin;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestPointStatusBoardRunnable extends StatusBoardRunnable {

   private final EventAdmin eventAdmin;

   public TestPointStatusBoardRunnable(TestPointUpdateMessage testPointUpdateMessage, EventAdmin eventAdmin) {
      super(testPointUpdateMessage);
      this.eventAdmin = eventAdmin;
   }

   @Override
   public void run() {
	   OteEventMessageUtil.sendEvent(getData(), eventAdmin);
   }

}
