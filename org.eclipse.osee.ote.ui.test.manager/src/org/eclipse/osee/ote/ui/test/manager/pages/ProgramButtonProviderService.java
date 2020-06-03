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
