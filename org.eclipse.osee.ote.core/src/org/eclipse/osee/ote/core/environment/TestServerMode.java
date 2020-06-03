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

package org.eclipse.osee.ote.core.environment;

import javax.print.attribute.EnumSyntax;
import org.eclipse.osee.framework.jdk.core.util.EnumBase;

/**
 * @author Robert A. Fisher
 */
public class TestServerMode extends EnumBase {
   private static final long serialVersionUID = 6600576749632500352L;
   public static final TestServerMode MULTI = new TestServerMode(0);
   public static final TestServerMode SINGLE = new TestServerMode(1);
   private static final String[] stringTable = new String[] {"multi", "single"};
   private static final TestServerMode[] enumValueTable = new TestServerMode[] {MULTI, SINGLE};

   private TestServerMode(int value) {
      super(value);
   }

   public static TestServerMode toEnum(String str) {
      return (TestServerMode) getEnum(str, stringTable, enumValueTable);
   }

   protected static TestServerMode toEnum(int value) {
      return (TestServerMode) getEnum(value, enumValueTable);
   }

   @Override
   protected String[] getStringTable() {
      return stringTable;
   }

   @Override
   protected EnumSyntax[] getEnumValueTable() {
      return enumValueTable;
   }
}