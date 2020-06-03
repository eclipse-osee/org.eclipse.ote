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

package org.eclipse.osee.ote.core.framework;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

public interface IRunManager {

   public boolean addListener(ITestLifecycleListener listener);

   public void clearAllListeners();

   public boolean removeListener(ITestLifecycleListener listener);

   public IMethodResult run(TestEnvironment env, IPropertyStore propertyStore);

   public boolean abort();

   public boolean abort(Throwable th, boolean wait);

   public boolean isAborted();

   public TestScript getCurrentScript();

   public boolean isCurrentThreadScript();
}
