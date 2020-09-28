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

import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * This interface is generally meant to provide test specific API. It is preferred to provide this rather than access to
 * the entire test environment.
 * 
 * @author Michael P. Masterson
 */
public interface OteApi {
   void logTestPoint(boolean isPassed, String testPointName, String expected, String actual);

   void logTestPoint(boolean isPassed, String testPointName, NamedId expected, NamedId actual);
}
