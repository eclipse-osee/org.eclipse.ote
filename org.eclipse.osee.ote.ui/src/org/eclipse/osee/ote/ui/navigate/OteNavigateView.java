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

package org.eclipse.osee.ote.ui.navigate;

import java.util.logging.Level;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryEventListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.ote.ui.OteNavigateComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Insert the type's description here.
 * 
 * @see ViewPart
 */
public class OteNavigateView extends ViewPart {

   public static final String VIEW_ID = "org.eclipse.osee.ote.ui.navigate.OteNavigateView";
   private OteNavigateComposite xNavComp;

   @Override
   public void setFocus() {
      if (xNavComp != null) {
         xNavComp.setFocus();
      }
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      xNavComp = new OteNavigateComposite(new OteNavigateViewItems(), parent, SWT.NONE);

      xNavComp.getFilteredTree().getViewer().setSorter(new OteNavigateViewerSorter());

      createActions();
      xNavComp.refresh();

      addExtensionPointListenerBecauseOfWorkspaceLoading();

      // TODO: Change to use OteHelpContext
      HelpUtil.setHelp(parent, "ote_navigator", "org.eclipse.osee.ote.help.ui");
      HelpUtil.setHelp(xNavComp, "ote_navigator", "org.eclipse.osee.ote.help.ui");
   }

   private void addExtensionPointListenerBecauseOfWorkspaceLoading() {
      IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
      extensionRegistry.addListener(new IRegistryEventListener() {
         @Override
         public void added(IExtension[] extensions) {
            try {
               refresh();
            } catch (Exception ex) {
               OseeLog.log(getClass(), Level.SEVERE, ex);
            }
         }

         @Override
         public void added(IExtensionPoint[] extensionPoints) {
            try {
               refresh();
            } catch (Exception ex) {
               OseeLog.log(getClass(), Level.SEVERE, ex);
            }
         }

         @Override
         public void removed(IExtension[] extensions) {
            try {
               refresh();
            } catch (Exception ex) {
               OseeLog.log(getClass(), Level.SEVERE, ex);
            }
         }

         @Override
         public void removed(IExtensionPoint[] extensionPoints) {
            try {
               refresh();
            } catch (Exception ex) {
               OseeLog.log(getClass(), Level.SEVERE, ex);
            }
         }
      }, "org.eclipse.osee.framework.ui.plugin.XNavigateItem");
   }

   protected void createActions() {
      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            if (!xNavComp.isDisposed()) {
               xNavComp.refresh();
            }
         }
      };
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      refreshAction.setToolTipText("Refresh");

   }

   public void refresh() {
      if (!xNavComp.isDisposed()) {
         xNavComp.refresh();
      }
   }
}