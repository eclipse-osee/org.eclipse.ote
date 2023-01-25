/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import java.util.ArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.ote.ui.message.search.MessageInfoSelectionListener;
import org.eclipse.ote.ui.message.tree.WatchedMessageNode;
import org.eclipse.ote.ui.message.watch.action.WatchElementAction;

public class MessageInfoSelectionHandler implements MessageInfoSelectionListener {

   private WatchView view;

   public MessageInfoSelectionHandler(WatchView view) {
      this.view = view;
   }

   @Override
   public void associatedClassSelected(final String className) {
      OseeLog.log(getClass(), Level.ALL, "Add event for: "+className);
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               WatchedMessageNode node = view.getWatchList().getMessageNode(className);
               if (node == null) {
                  node = (WatchedMessageNode) view.getWatchList().createElements(className, MessageMode.READER, new ArrayList<ElementPath>());
               }
               new WatchElementAction(view, node).run();
            } catch (Throwable th) {
               OseeLog.log(getClass(), Level.WARNING, th);
            }
         }
      });
   }

   @Override
   public String getAssociatedToolTip() {
      return "Click to watch the selected message";
   }

}
