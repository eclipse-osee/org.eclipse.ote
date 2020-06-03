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

import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.event.IEventData;

public interface ITestLifecycleListener {

   IMethodResult preInstantiation(IEventData eventData, TestEnvironment env);

   IMethodResult postInstantiation(IEventData eventData, TestEnvironment env);

   IMethodResult preDispose(IEventData eventData, TestEnvironment env);

   IMethodResult postDispose(IEventData eventData, TestEnvironment env);
}
