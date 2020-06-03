/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.ote.ui.message.watch;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.plugin.util.ViewPartUtil;

/**
 * @author Donald G. Dunne
 */
public class MessageWatchAction extends Action {

   public MessageWatchAction() {
      super("Open Message Watch");
   }

   @Override
   public void run() {
      ViewPartUtil.openOrShowView(WatchView.VIEW_ID);
   }
}
