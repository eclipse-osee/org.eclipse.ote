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
package org.eclipse.ote.simple.test.environment;

import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.ote.core.environment.OteApi;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * @author Nydia Delgado
 */
public class SimpleOteApi implements OteApi {

   private TestEnvironment testEnv;

   @Override
   public void logTestPoint(boolean isPassed, String testPointName, String expected, String actual) {
      testEnv.getTestScript().logTestPoint(isPassed, testPointName, expected, actual);
   }

   @Override
   public void logTestPoint(boolean isPassed, String testPointName, NamedId expected, NamedId actual) {
      logTestPoint(isPassed, testPointName, expected.getName(), actual.getName());
   }
}
