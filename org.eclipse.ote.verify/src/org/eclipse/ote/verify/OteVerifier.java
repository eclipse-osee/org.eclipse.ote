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

import org.eclipse.osee.ote.core.environment.OteInternalApi;
import org.eclipse.osee.ote.core.environment.interfaces.ITestPoint;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;

/**
 * @author Michael P. Masterson
 * @param <T> The concrete implementation
 */
public abstract class OteVerifier<T extends OteVerifier<T>> {

   /**
    * Creates a test point comparing this object to the actual argument
    * 
    * @param actual The value that was actually seen during the test
    * @return A test point indicating if this object matches the actual argument
    */
   public abstract ITestPoint verify(T actual);

   public void logResults(OteInternalApi api, ITestPoint tp) {
      api.testLogger().testpoint(api, tp);
   }

   /**
    * Adds a new test point to the checkgroup comparing the expected and actual are equal ONLY IF the attributes are
    * used or required
    * 
    * @param expected
    * @param actual
    * @param groupToUpdate
    */
   protected <A> void addToCheckGroup(OteVerifierAttribute<A> expected, OteVerifierAttribute<A> actual, CheckGroup groupToUpdate) {
      OteMatchResult matches = expected.matches(actual);

      if (!matches.equals(OteMatchResult.NOT_USED)) {
         groupToUpdate.add(new CheckPoint(expected.getName(), expected.toString(), actual.toString(),
            matches.equals(OteMatchResult.PASSED)));
      }
   }

   /**
    * Adds a new test point to the checkgroup comparing the expected and actual are not equal ONLY IF the attributes are
    * used or required
    * 
    * @param expected
    * @param actual
    * @param groupToUpdate
    */
   protected <A> void addToCheckGroupNot(OteVerifierAttribute<A> expected, OteVerifierAttribute<A> actual, CheckGroup groupToUpdate) {
      OteMatchResult matches = expected.matches(actual);

      if (!matches.equals(OteMatchResult.NOT_USED)) {
         groupToUpdate.add(new CheckPoint(expected.getName(), "Not " + expected.toString(), actual.toString(),
            matches.equals(OteMatchResult.FAILED)));
      }
   }

}
