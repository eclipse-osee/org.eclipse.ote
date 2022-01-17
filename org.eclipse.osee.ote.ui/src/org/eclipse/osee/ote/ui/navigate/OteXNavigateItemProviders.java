/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ote.ui.navigate;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProvider;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemProviders;

/**
 * @author Michael P. Masterson
 */
public class OteXNavigateItemProviders {

   public static synchronized Set<XNavigateItemProvider> getProviders() {
      Set<XNavigateItemProvider> providers = XNavigateItemProviders.getProviders();

      Iterator<XNavigateItemProvider> iterator = providers.iterator();
      iterator.forEachRemaining(provider -> {
         String typeName = provider.getClass().getTypeName();
         if (!typeName.startsWith("ote.") && !typeName.contains(".ote.")) {
            iterator.remove();
         }
      });

      return providers;
   }
}
