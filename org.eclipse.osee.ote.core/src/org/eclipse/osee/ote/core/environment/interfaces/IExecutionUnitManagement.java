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

package org.eclipse.osee.ote.core.environment.interfaces;

import org.eclipse.osee.ote.core.TestException;

/**
 * This interface provides the basic abilities that must be able to be performed on any ExecutionUnit being run by the
 * system. All classes which provide control over an ExecutionUnit should implement at least this interface so the
 * environments can make use of them.
 * 
 * @author Robert A. Fisher
 */
public interface IExecutionUnitManagement {
   public void startExecutionUnit() throws Exception;

   public void setupExecutionUnit(Object execUnitConfig) throws Exception;

   public void runPrimaryOneCycle() throws InterruptedException, TestException;

   public void stopExecutionUnit() throws InterruptedException;

   public void init() throws Exception;

   public void dispose();
}
