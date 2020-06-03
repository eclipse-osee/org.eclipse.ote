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

public interface ITestLifecycleListenerProvider {
   void clear();

   boolean addListener(ITestLifecycleListener listener);

   boolean removeListener(ITestLifecycleListener listener);

   IMethodResult notifyPostDispose(IPropertyStore propertyStore, TestEnvironment env);

   IMethodResult notifyPostInstantiation(IPropertyStore propertyStore, TestScript test, TestEnvironment env);

   IMethodResult notifyPreDispose(IPropertyStore propertyStore, TestScript test, TestEnvironment env);

   IMethodResult notifyPreInstantiation(IPropertyStore propertyStore, TestEnvironment env);
}
