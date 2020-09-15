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
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;

/**
 * @author Michael P. Masterson
 * @param <T> The concrete implementation 
 */
public abstract class OteVerifier<T extends OteVerifier<T>> {
   protected UutApi api;
   
   /**
    * @param api Needed for logging results
    */
   public OteVerifier(UutApi api) {
      this.api = api;
   }

   /**
    * Creates a test point comparing this object to the actual argument
    * 
    * @param actual The value that was actually seen during the test
    * @return A test point indicating if this object matches the actual argument
    */
   public abstract ITestPoint verify(T actual);
   
   public void logResults(ITestPoint tp) {
      api.testLogger().testpoint(api, tp);
   }
   
}
