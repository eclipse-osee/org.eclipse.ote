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

import java.util.Collection;

/**
 * @author Dominic Guss
 */
public interface IProgramButtonProviderService {

   void addProgramButtonProvider(IProgramButtonProvider programButtonProvider);

   Collection<IProgramButtonProvider> getProgramButtonProviders();
}
