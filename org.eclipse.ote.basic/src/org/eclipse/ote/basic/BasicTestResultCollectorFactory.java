/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.ote.basic;

import org.eclipse.osee.ote.core.framework.testrun.ITestResultCollector;
import org.eclipse.osee.ote.core.framework.testrun.ITestResultCollectorFactory;

/**
 * @author Roberto E. Escobar
 * @author Andy Jury
 */
public class BasicTestResultCollectorFactory implements ITestResultCollectorFactory {

   @Override
   public ITestResultCollector createCollector() {
      return new BasicTestResultCollector();
   }
}
