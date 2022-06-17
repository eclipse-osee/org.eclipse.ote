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
package org.eclipse.ote.simple.rest.server.resources;

/**
 * @author Nydia Delgado
 */
public class OteSimpleJsonPojo {

   private String typeName;
   private String value;

   public String getTypeName() {
      return typeName;
   }

   public void setTypeName(String typeName) {
      this.typeName = typeName;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}
