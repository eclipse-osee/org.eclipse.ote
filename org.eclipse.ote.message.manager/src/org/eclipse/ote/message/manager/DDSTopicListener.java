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

package org.eclipse.ote.message.manager;

import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.messaging.dds.entity.Topic;
import org.eclipse.osee.ote.messaging.dds.listener.TopicListener;
import org.eclipse.osee.ote.messaging.dds.status.InconsistentTopicStatus;

/**
 * This empty topic listener is used when creating a topic which requires a non-null listener.
 *  
 * @author Andrew M. Finkbeiner
 * @author Michael P. Masterson
 */
public class DDSTopicListener implements TopicListener{

   public DDSTopicListener(){
      GCHelper.getGCHelper().addRefWatch(this);
   }
   
   @Override
   public void onInconsistentTopic(Topic theTopic, InconsistentTopicStatus status) {
      // Intentionally left blank
   }
}
