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

package org.eclipse.osee.ote.messaging.dds.entity;

import org.eclipse.osee.ote.messaging.dds.listener.TopicListener;
import org.eclipse.osee.ote.messaging.dds.service.TypeSignature;

//UNSURE This class has not been implemented, but is called out in the spec

/**
 * This class is here for future functionality that is described in the DDS specification but has not been implemented
 * or used.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class SubscriptionBuiltinTopicData extends Topic {

   SubscriptionBuiltinTopicData(DomainParticipant participant, TypeSignature typeName, String name, String namespace, boolean enabled, TopicListener listener, EntityFactory parentFactory) {
      super(participant, typeName, name, namespace, enabled, listener, parentFactory);
   }

}
