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

package org.eclipse.ote.tools;

import org.eclipse.ote.tools.util.ToolsUtilTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite for the package org.eclipse.ote.tools.
 *
 * @author Loren K. Ashley
 * @implSpec Add a test suite for each new sub-package of org.eclipse.ote.tools.
 */

//@formatter:off
@RunWith(Suite.class)
@Suite.SuiteClasses
   (
      {
         ToolsUtilTestSuite.class
      }
   )
public class ToolsTestSuite {
   // Test Suite
}
//@formatter:on
