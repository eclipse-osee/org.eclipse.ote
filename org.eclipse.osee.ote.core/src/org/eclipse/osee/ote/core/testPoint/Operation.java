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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.print.attribute.EnumSyntax;
import org.eclipse.osee.framework.jdk.core.util.EnumBase;

public class Operation extends EnumBase {

   private static final long serialVersionUID = -3132727420541603024L;
   static final int OR_ID = 0;
   public static final Operation OR = new Operation(OR_ID);
   static final int AND_ID = 1;
   public static final Operation AND = new Operation(AND_ID);
   static final int NOR_ID = 2;
   public static final Operation NOR = new Operation(NOR_ID);
   static final int NAND_ID = 3;
   public static final Operation NAND = new Operation(NAND_ID);

   private static final String[] stringTable = new String[] {"OR", "AND", "NOR", "NAND"};
   private static final Operation[] enumValueTable = new Operation[] {OR, AND, NOR, NAND};

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