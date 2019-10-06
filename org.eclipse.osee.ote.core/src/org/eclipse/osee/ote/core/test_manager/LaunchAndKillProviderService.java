/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.test_manager;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.ote.core.test_manager.interfaces.ILaunchAndKillProvider;
import org.eclipse.osee.ote.core.test_manager.interfaces.ILaunchAndKillProviderService;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Dominic Guss
 */
public class LaunchAndKillProviderService implements ILaunchAndKillProviderService {

   private final Collection<ILaunchAndKillProvider> launchAndKillProviders = new ArrayList<>();

   public LaunchAndKillProviderService() {
      // Register the service
      FrameworkUtil.getBundle(getClass()).getBundleContext().registerService(LaunchAndKillProviderService.class, this,
            null);
   }

   @Override
   public void addLaunchAndKillProvider(ILaunchAndKillProvider launchAndKillProvider) {
      launchAndKillProviders.add(launchAndKillProvider);
   }

   @Override
   public Collection<ILaunchAndKillProvider> getLaunchAndKillProviders() {
      return launchAndKillProviders;
   }
}
