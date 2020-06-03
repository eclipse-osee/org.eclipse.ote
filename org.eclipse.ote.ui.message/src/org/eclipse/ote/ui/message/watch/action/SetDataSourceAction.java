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

package org.eclipse.ote.ui.message.watch.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.ote.ui.message.tree.WatchedMessageNode;

/**
 * @author Ken J. Aguilar
 */
public class SetDataSourceAction extends Action {

   private final WatchedMessageNode node;
   private final DataType type;

   public SetDataSourceAction(WatchedMessageNode node, DataType type) {
      super(type.name(), IAction.AS_RADIO_BUTTON);
      this.node = node;
      this.type = type;
      setChecked(node.getSubscription().getMemType() == type);
   }

   @Override
   public void run() {
      node.getSubscription().changeMemType(type);
   }

}
