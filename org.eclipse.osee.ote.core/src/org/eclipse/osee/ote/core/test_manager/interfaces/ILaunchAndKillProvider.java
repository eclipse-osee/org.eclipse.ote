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

package org.eclipse.osee.ote.core.test_manager.interfaces;

import java.util.Collection;

import org.eclipse.osee.framework.core.executor.ExecutorAdmin;

/**
 * @author Dominic Guss
 */
public interface ILaunchAndKillProvider {

   Collection<ILaunchAndKill> getLaunchers();

   Collection<ILaunchAndKill> getKillers();
   
   void setExecutorAdmin(ExecutorAdmin executorAdmin);
}
