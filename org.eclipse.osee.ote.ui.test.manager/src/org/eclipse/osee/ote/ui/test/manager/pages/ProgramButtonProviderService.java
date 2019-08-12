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
package org.eclipse.osee.ote.ui.test.manager.pages;

import java.util.ArrayList;
import java.util.Collection;

import org.osgi.framework.FrameworkUtil;

/**
 * @author Dominic Guss
 */
public class ProgramButtonProviderService implements IProgramButtonProviderService {

   private final Collection<IProgramButtonProvider> programButtonProviders = new ArrayList<>();

   public ProgramButtonProviderService() {
      // Register the service
      FrameworkUtil.getBundle(getClass()).getBundleContext().registerService(ProgramButtonProviderService.class, this,
            null);
   }

   @Override
   public Collection<IProgramButtonProvider> getProgramButtonProviders() {
      return programButtonProviders;
   }

   @Override
   public void addProgramButtonProvider(IProgramButtonProvider programButtonProvider) {
      programButtonProviders.add(programButtonProvider);
   }
}
