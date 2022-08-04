/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ote.core.framework.outfile;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Andy Jury
 */
public class CreateArtifactsJsonPojo {

   private String typeName;
   private String name;
   private ArrayList<AttributeJsonPojo> attribute;

   public String getTypeName() {
      return typeName;
   }

   public void setTypeName(String typeName) {
      this.typeName = typeName;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @JsonProperty("attributes")
   public ArrayList<AttributeJsonPojo> getAttributes() {
      return attribute;
   }

   public void addAttribute(AttributeJsonPojo attribute1) {
      if (attribute == null) {
         attribute = new ArrayList<AttributeJsonPojo>();
      }
      this.attribute.add(attribute1);
   }
}
