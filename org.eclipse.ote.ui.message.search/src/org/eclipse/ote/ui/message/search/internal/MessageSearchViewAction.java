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

package org.eclipse.ote.ui.message.search.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.plugin.util.ViewPartUtil;
import org.eclipse.ote.ui.message.search.MessageSearchView;

public class MessageSearchViewAction extends Action {

   public MessageSearchViewAction() {
      super("Open Message Search View");
   }

   @Override
   public void run() {
      ViewPartUtil.openOrShowView(MessageSearchView.VIEW_ID);
   }
}
