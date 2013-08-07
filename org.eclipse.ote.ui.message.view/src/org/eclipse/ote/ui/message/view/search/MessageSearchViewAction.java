/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.message.view.search;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.plugin.util.ViewPartUtil;
import org.eclipse.ote.ui.message.view.MessageSearchView;

public class MessageSearchViewAction extends Action {

   public MessageSearchViewAction() {
      super("Open Message Search View");
   }

   @Override
   public void run() {
      ViewPartUtil.openOrShowView(MessageSearchView.VIEW_ID);
   }
}
