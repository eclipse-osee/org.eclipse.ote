/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.ote.tools.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite for the package org.eclipse.ote.tools.util.
 *
 * @author Loren K. Ashley
 */

//@formatter:off
@RunWith(Suite.class)
@Suite.SuiteClasses
   (
      {
         DoubleMapTest.class,
         DoubleMapSetTest.class,
         MapSetTest.class
      }
   )
public class ToolsUtilTestSuite {
   // Test Suite
}
//@formatter:on
