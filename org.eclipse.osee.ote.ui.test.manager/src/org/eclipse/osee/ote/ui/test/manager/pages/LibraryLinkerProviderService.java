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
public class LibraryLinkerProviderService implements ILibraryLinkerProviderService {

   private final Collection<ILibraryLinkerProvider> libraryLinkerProviders = new ArrayList<>();

   public LibraryLinkerProviderService() {
      // Register the service
      FrameworkUtil.getBundle(getClass()).getBundleContext().registerService(LibraryLinkerProviderService.class, this,
            null);
   }

   @Override
   public void addLibraryLinkerProvider(ILibraryLinkerProvider libraryLinkerProvider) {
      libraryLinkerProviders.add(libraryLinkerProvider);
   }

   @Override
   public Collection<ILibraryLinkerProvider> getLibraryLinkerProviders() {
      return libraryLinkerProviders;
   }
}
