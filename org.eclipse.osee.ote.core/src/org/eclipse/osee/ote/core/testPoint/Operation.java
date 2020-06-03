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

package org.eclipse.osee.ote.core.testPoint;

import javax.print.attribute.EnumSyntax;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.osee.framework.jdk.core.util.EnumBase;

public class Operation extends EnumBase {

   private static final long serialVersionUID = -3132727420541603024L;
   public static final Operation OR = new Operation(0);
   public static final Operation AND = new Operation(1);

   private static final String[] stringTable = new String[] {"OR", "AND"};
   private static final Operation[] enumValueTable = new Operation[] {OR, AND};

   private Operation(int value) {
      super(value);
   }

   @JsonCreator
   public static Operation toEnum(String str) {
      return (Operation) getEnum(str, stringTable, enumValueTable);
   }

   protected static Operation toEnum(int value) {
      return (Operation) getEnum(value, enumValueTable);
   }

   @Override
   protected String[] getStringTable() {
      return stringTable;
   }

   @Override
   protected EnumSyntax[] getEnumValueTable() {
      return enumValueTable;
   }

   @Override
   @JsonProperty
   public String getName() {
      return super.getName();
   }
}