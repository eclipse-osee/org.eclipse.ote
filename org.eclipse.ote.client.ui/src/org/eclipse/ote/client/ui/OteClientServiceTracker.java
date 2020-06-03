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

package org.eclipse.ote.client.ui;

import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.ote.client.ui.internal.OteClientUiPlugin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteClientServiceTracker extends ServiceTracker {

   public OteClientServiceTracker() {
      super(OteClientUiPlugin.getDefault().getBundleContext(), IOteClientService.class.getName(), null);
   }
}
