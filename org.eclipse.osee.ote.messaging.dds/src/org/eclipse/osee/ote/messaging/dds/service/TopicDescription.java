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

package org.eclipse.osee.ote.messaging.dds.service;

import org.eclipse.osee.ote.messaging.dds.entity.DomainParticipant;

/**
 * Provides a description of all topic classes. Any class that is a topic of some sort should implement this interface.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public interface TopicDescription {

   /**
    * The method which will supply the name used to create the topic.
    * 
    * @return A string representation of the topic name.
    */
   public String getName();

   /**
    * The method which will return a reference to the <code>DomainParticipant</code> which created the topic.
    * 
    * @return The <code>DomainParticipant</code> that created this topic.
    */
   public DomainParticipant getParticipant();

   /**
    * The method which will supply the type name used to create the topic.
    * 
    * @return A string representation of the type name.
    */
   public String getTypeName();

   public String getNamespace();
}
