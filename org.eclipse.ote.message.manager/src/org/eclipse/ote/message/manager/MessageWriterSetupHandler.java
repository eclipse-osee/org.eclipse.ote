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

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;

/**
 * @author Michael P. Masterson
 */
public interface MessageWriterSetupHandler {

   void setup(Message message);

   /**
    * Return true if the topic should allow for the wrapping of writer data into reader data. This is useful to be set
    * to false if you want to ensure all reader transmissions of the IO are coming from a physical interface. If the
    * data may be simulated it is generally a good idea to return true
    *
    * @param topic
    * @return Whether to wrap or not.
    */
   boolean shouldWrap(TopicDescription topic);
}
