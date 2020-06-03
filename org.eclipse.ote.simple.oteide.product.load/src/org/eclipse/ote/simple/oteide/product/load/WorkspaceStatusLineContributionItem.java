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

package org.eclipse.ote.simple.oteide.product.load;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.StatusLineContributionItem;

/**
 * @author Andrew M. Finkbeiner
 */
public class WorkspaceStatusLineContributionItem {
   
   private static String ID = "org.eclipse.ote.simple.oteide.product.load";
   private String shortText;
   private StatusLineContributionItem item;
   private String path;
   
   public WorkspaceStatusLineContributionItem() {
      path = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
      shortText = getShortPath(path);
      item = new StatusLineContributionItem(ID, true, shortText.length());
   }

   private static String getShortPath(String path) {
      String elements[] = path.split("\\/");
      if (elements.length >= 2) {
         return elements[elements.length - 2] + File.separator + elements[elements.length - 1];
      }
      return path;
   }

   public static void addToAllViews() {
      Display.getDefault().asyncExec(new Runnable() {
         @Override
         public void run() {
            try {
               if (PlatformUI.getWorkbench() == null || PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null) {
                  return;
               }
               
               IWorkbenchWindow window =
                     PlatformUI.getWorkbench().getActiveWorkbenchWindow();

               if (window != null) {
                  IWorkbenchPage[] pages = window.getPages();
                  for (int i = 0; i < pages.length; i++) {
                     IWorkbenchPage p = pages[i];
                     p.addPartListener(new IPartListener(){
                        @Override
                        public void partActivated(IWorkbenchPart part) {
                           // Intentionally empty block
                        }

                        @Override
                        public void partBroughtToTop(IWorkbenchPart part) {
                           // Intentionally empty block
                        }

                        @Override
                        public void partClosed(IWorkbenchPart part) {
                           // Intentionally empty block
                        }

                        @Override
                        public void partDeactivated(IWorkbenchPart part) {
                           // Intentionally empty block
                        }

                        @Override
                        public void partOpened(IWorkbenchPart part) {
                           if (part != null && part instanceof IViewPart) {
                              addToViewpart((ViewPart) part);
                           }
                        }
                        
                     });
                  }
               }
               for (final IViewReference viewDesc : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences()) {
                  IViewPart viewPart = viewDesc.getView(false);
                  if (viewPart != null) {
                     addToViewpart((ViewPart) viewPart);
                  }
               }
            } catch (Exception ex) {
               // DO NOTHING
            }
         }
      });
   }

   private static void addToViewpart(ViewPart viewPart) {
      try {
         if (viewPart != null) {
            for (IContributionItem item : viewPart.getViewSite().getActionBars().getStatusLineManager().getItems()) {
               if (item instanceof WorkspaceStatusLineContributionItem) {
                  return;
               }
            }
            final WorkspaceStatusLineContributionItem status = new WorkspaceStatusLineContributionItem();
            viewPart.getViewSite().getActionBars().getStatusLineManager().add(status.item);
            status.item.setText(status.shortText);
            status.item.setToolTipText("dbl-clk to open: " + status.path);
            status.item.setActionHandler(new Action() {
               @Override
               public void run() {
                  Program.launch(status.path);
               }
            });
         }
      } catch (Exception ex) {
         // do nothing
      }
   }
}
