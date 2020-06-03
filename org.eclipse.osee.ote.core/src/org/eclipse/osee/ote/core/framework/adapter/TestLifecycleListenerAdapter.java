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

package org.eclipse.osee.ote.core.framework.adapter;

import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.ITestLifecycleListener;
import org.eclipse.osee.ote.core.framework.MethodResultImpl;
import org.eclipse.osee.ote.core.framework.ReturnCode;
import org.eclipse.osee.ote.core.framework.event.IEventData;

/**
 * @author Roberto E. Escobar
 */
public class TestLifecycleListenerAdapter implements ITestLifecycleListener {

   @Override
   public IMethodResult postDispose(IEventData eventData, TestEnvironment env) {
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult postInstantiation(IEventData eventData, TestEnvironment env) {
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult preDispose(IEventData eventData, TestEnvironment env) {
      return new MethodResultImpl(ReturnCode.OK);
   }

   @Override
   public IMethodResult preInstantiation(IEventData eventData, TestEnvironment env) {
      return new MethodResultImpl(ReturnCode.OK);
   }
}
