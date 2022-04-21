/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ote.rest.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew M. Finkbeiner
 */
@XmlRootElement
public class OTEConfiguration {

   private OTEConfigurationIdentity identity;

   private final List<OTEConfigurationItem> items;

   private boolean install;

   public OTEConfiguration() {
      items = new ArrayList<>();
      this.install = true;
   }

   public OTEConfigurationIdentity getIdentity() {
      return identity;
   }

   @XmlElementWrapper
   @XmlElement(name = "OTEConfigurationItem")
   public List<OTEConfigurationItem> getItems() {
      return items;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((identity == null) ? 0 : identity.hashCode());
      result = prime * result + ((items == null) ? 0 : items.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      OTEConfiguration other = (OTEConfiguration) obj;
      //      if (identity == null) {
      //         if (other.identity != null)
      //            return false;
      //      } else if (!identity.equals(other.identity))
      //         return false;
      if (items == null) {
         if (other.items != null) {
            return false;
         }
      } else if (!items.equals(other.items)) {
         return false;
      }
      return true;
   }

   public void setIdentity(OTEConfigurationIdentity identity) {
      this.identity = identity;
   }

   public void addItem(OTEConfigurationItem item) {
      items.add(item);
   }

   public void setInstall(boolean install) {
      this.install = install;
   }

   public boolean getInstall() {
      return this.install;
   }

}
