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

package org.eclipse.ote.client;

import java.net.URL;
import org.osgi.framework.Bundle;

/**
 * @author Ken J. Aguilar
 */
public class BundleClassLoader extends ClassLoader {
   private final Bundle bundle;

   public BundleClassLoader(Bundle bundle) {
      super(BundleClassLoader.class.getClassLoader());
      this.bundle = bundle;

   }

   @Override
   protected Class<?> findClass(String name) throws ClassNotFoundException {
      return bundle.loadClass(name);
   }

   @Override
   protected URL findResource(String name) {
      return bundle.getResource(name);
   }

   public Bundle getBundle() {
      return bundle;
   }
}
