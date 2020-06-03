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

import org.eclipse.osee.ote.messaging.dds.listener.Listener;

/**
 * The base class which all of the entity classes in the DDS system extend, except for the
 * <code>DomainParticipant</code>. This intermediate class is in place simply to clarify that a
 * <code>DomainParticipant</code> can contain any other type of entity, except another <code>DomainParticipant</code>.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public abstract class DomainEntity extends Entity {

   public DomainEntity(boolean enabled, Listener listener, EntityFactory parentFactory) {
      super(enabled, listener, parentFactory);
   }

}
