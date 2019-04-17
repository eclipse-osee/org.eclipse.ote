/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.client.ui;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Andrew M. Finkbeiner
 */
public class ViewUtil {

   public static void closeViewAcrossPerspectives(String viewId){
      IWorkbench workbench = PlatformUI.getWorkbench();
      if (workbench == null || workbench.getActiveWorkbenchWindow() == null)
         return;

      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IPerspectiveDescriptor actPd = page.getPerspective();
      IViewReference[] activeReferences = page.getViewReferences();
      boolean notFound = true;
      for (IViewReference viewReference : activeReferences) {
         if (viewReference.getId().equals(viewId)){
            page.hideView(viewReference);
            notFound = false;
         }
      }
      if(notFound){
         //find the view in other perspectives
         IPerspectiveDescriptor[] pd = page.getOpenPerspectives();
         for (int i = 0; i < pd.length; i++) {
            try {
               page.setPerspective(pd[i]);
            } catch (Exception ex) {
               // Ignore, this can get an NPE in Eclipse, see bug 4454
            }
            activeReferences = page.getViewReferences();

            for (IViewReference viewReference : activeReferences) {
               if (viewReference.getId().equals(viewId)){
                  page.hideView(viewReference);
               }
            }
         }
         page.setPerspective(actPd);
      }
   }

   public static void detachView(IViewPart part, int width, int height) {
      EModelService s = part.getViewSite().getService(EModelService.class);
      MPartSashContainerElement p = part.getViewSite().getService(MPart.class);
      Rectangle bounds = part.getSite().getShell().getBounds();
      if (p.getCurSharedRef() != null){
         p = p.getCurSharedRef();
         if (!isDetached(p.getParent())) {
            s.detach(p, bounds.x, bounds.y, width, height);
         }
      }
   }


   @SuppressWarnings({ "rawtypes" })
   public static boolean isDetached(MElementContainer p) {
      try {
         if (p == null) {
            return true;
         }
         if (p instanceof MApplication) {
            return false;
         }
         return isDetached(p.getParent());
      } catch (Throwable th) {
         // INTENTIONALLY EMPTY BLOCK
      }
      return false;
   }

}
