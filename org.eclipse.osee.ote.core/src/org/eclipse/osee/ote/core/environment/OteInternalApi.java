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

package org.eclipse.osee.ote.core.environment;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;

/**
 * Intended for API accessed by non-test developers only
 * 
 * @author Michael P. Masterson
 */
public interface OteInternalApi {
   ITestAccessor testAccessor();
   ITestEnvironmentAccessor testEnv();
   ITestLogger testLogger();
}
