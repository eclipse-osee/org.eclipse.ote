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

import org.eclipse.osee.ote.messaging.dds.entity.DomainParticipant;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;

/**
 * @author Michael P. Masterson
 */
public class TopicDescriptionImpl implements TopicDescription {

   private final String topic;
   private final String namespace;
   private final int hashCode;

   public TopicDescriptionImpl(String topic, String namespace) {
      this.topic = topic;
      this.namespace = namespace;
      this.hashCode = calculateStaticHashCode(topic, namespace);
   }

   /**
    * Since the topic and namespace are final there is no need to recalculate the hash code in the
    * hashCode method.
    * 
    * @param topic
    * @param namespace
    * @return The immutable hash code value
    */
   private int calculateStaticHashCode(String topic, String namespace) {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
      result = prime * result + ((topic == null) ? 0 : topic.hashCode());
      return result;
   }

   @Override
   public String getName() {
      return topic;
   }

   @Override
   public String getNamespace() {
      return namespace;
   }

   @Override
   public DomainParticipant getParticipant() {
      return null;
   }

   @Override
   public String getTypeName() {
      return null;
   }

   @Override
   public int hashCode() {
      return hashCode;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TopicDescriptionImpl other = (TopicDescriptionImpl) obj;
      if (namespace == null) {
         if (other.namespace != null)
            return false;
      }
      else if (!namespace.equals(other.namespace))
         return false;
      if (topic == null) {
         if (other.topic != null)
            return false;
      }
      else if (!topic.equals(other.topic))
         return false;
      return true;
   }

}
