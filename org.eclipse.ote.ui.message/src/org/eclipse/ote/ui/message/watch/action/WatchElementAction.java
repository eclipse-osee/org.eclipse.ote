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

import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.ote.ui.message.internal.WatchImages;
import org.eclipse.ote.ui.message.tree.WatchedMessageNode;
import org.eclipse.ote.ui.message.watch.AddWatchParameter;
import org.eclipse.ote.ui.message.watch.ChildElementNodeContentProvider;
import org.eclipse.ote.ui.message.watch.ChildElementNodeLabelProvider;
import org.eclipse.ote.ui.message.watch.ChildSelectCheckedTreeSelectionDialog;
import org.eclipse.ote.ui.message.watch.ElementPath;
import org.eclipse.ote.ui.message.watch.WatchView;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @author Andrew M. Finkbeiner
 */
public class WatchElementAction extends Action {
   private final WatchView watchView;

   private final WatchedMessageNode msgNode;

   public WatchElementAction(WatchView watchView, WatchedMessageNode node) {
      super("Watch Element", IAction.AS_PUSH_BUTTON);
      setToolTipText("loads additional message elements");
      this.watchView = watchView;
      this.msgNode = node;
      setEnabled(node.isEnabled() && node.getSubscription().isResolved());
   }

   @Override
   public void run() {
      final String msgName = msgNode.getMessageClassName();
      final LinkedList<Element> list = new LinkedList<Element>();
      boolean needsTreeView = false;

      IMessageSubscription subscription = msgNode.getSubscription();
      Message<?, ?, ?> message = subscription.getMessage();
      Collection<Element> msgElements = message.getElements();

      for (final Element element : msgElements) {
         if (element instanceof RecordElement) {
            needsTreeView = true;
            list.add(element);
         } else if (msgNode.findChildElement(new ElementPath(element.getElementPath())) == null) {
            list.add(element);
         }
      }
      if (needsTreeView) {
         ChildSelectCheckedTreeSelectionDialog dialog =
            new ChildSelectCheckedTreeSelectionDialog(Displays.getActiveShell(), new ChildElementNodeLabelProvider(),
               new ChildElementNodeContentProvider());
         dialog.setTitle(msgNode.getName());

         dialog.setImage(ImageManager.getImage(WatchImages.GEAR));
         dialog.setEmptyListMessage("No matching message elements");
         dialog.setInput(msgNode);
         dialog.setMessage("Select elements to watch.");
         if (dialog.open() == Window.OK) {
            Object[] additions = dialog.getResult();
            if (additions.length > 0) {
               AddWatchParameter parameter = new AddWatchParameter();
               for (Object elem : additions) {
                  if (!(elem instanceof RecordElement)) {
                     parameter.addMessage(msgName, new ElementPath(((Element) elem).getElementPath()));
                  }
               }
               watchView.addWatchMessage(parameter);
               watchView.getTreeViewer().refresh(msgNode);
               watchView.saveWatchFile();
            }
         }
      } else {
         final Element[] elements = list.toArray(new Element[list.size()]);
         ElementListSelectionDialog dialog =
            new ElementListSelectionDialog(Displays.getActiveShell(), new ElementLabelProvider());
         dialog.setMultipleSelection(true);
         dialog.setTitle(msgNode.getName());
         dialog.setImage(ImageManager.getImage(WatchImages.GEAR));
         dialog.setMessage("Select element to watch. (? matches any character, * matches any string)");
         dialog.setEmptySelectionMessage("No matching message elements");
         dialog.setElements(elements);
         if (dialog.open() == Window.OK) {
            Object[] additions = dialog.getResult();
            if (additions.length > 0) {
               AddWatchParameter parameter = new AddWatchParameter();
               for (Object elem : additions) {
                  parameter.addMessage(msgName, new ElementPath(((Element) elem).getElementPath()));
               }
               watchView.addWatchMessage(parameter);
               watchView.getTreeViewer().refresh(msgNode);
               watchView.saveWatchFile();
            }
         }
      }
   }
}
