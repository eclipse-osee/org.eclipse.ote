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
package org.eclipse.ote.verify;

import org.eclipse.osee.ote.core.environment.UutApi;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * @author Michael P. Masterson
 */
public class MockOteApi implements UutApi {
   
   private MockTestLogger mockLogger;

   public MockOteApi() {
      this.mockLogger = new MockTestLogger();
   }

   @Override
   public ITestAccessor testAccessor() {
      return null;
   }

   @Override
   public ITestEnvironmentAccessor testEnv() {
      return null;
   }

   @Override
   public MockTestLogger testLogger() {
      return mockLogger;
   }

}
