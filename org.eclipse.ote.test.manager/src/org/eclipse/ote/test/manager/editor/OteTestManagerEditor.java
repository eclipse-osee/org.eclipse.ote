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

package org.eclipse.ote.test.manager.editor;

import java.util.logging.Level;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.ote.client.ui.core.widgets.HostSelectionTable;
import org.eclipse.ote.test.manager.OteTestManagerFactory;
import org.eclipse.ote.test.manager.internal.OteTestManagerModel;
import org.eclipse.ote.test.manager.internal.OteTestManagerPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteTestManagerEditor extends TestManagerEditor implements IResourceChangeListener {

   private static final String OTE_TEST_LIBS_PROJECT = "ote.test.support.libraries";
   private final IProject supportLibsProject;
   private HostSelectionTable hostSelectionTable;

   public OteTestManagerEditor() {
      super(OteTestManagerFactory.getInstance(), new OteTestManagerModel());
      ((OteTestManagerModel)getModel()).setTestManagerEditor(this);
      OseeLog.log(OteTestManagerPlugin.class, Level.INFO, "Constructing OteTestManagerEditor");
      IWorkspace ws = ResourcesPlugin.getWorkspace();
      ws.addResourceChangeListener(this, IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);
      supportLibsProject = ws.getRoot().getProject(OTE_TEST_LIBS_PROJECT);
   }

   @Override
   public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
      OseeLog.log(OteTestManagerPlugin.class, Level.INFO, "Initializing OTE Editor");
      super.init(site, editorInput);
   }

   @Override
   public void resourceChanged(IResourceChangeEvent event) {
      switch (event.getType()) {
         case IResourceChangeEvent.PRE_DELETE:
            if (supportLibsProject != null && event.getResource().equals(supportLibsProject)) {
               Displays.pendInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     MessageDialog.openInformation(Displays.getActiveShell(), "Project Deletion",
                        "The support libraries project is being deleted/replaced. Test Manager needs to shutdown");
                     PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(
                        OteTestManagerEditor.this, true);
                  }
               });
               try {
                  Thread.sleep(500);
               } catch (InterruptedException ex) {
                  OseeLog.log(OteTestManagerPlugin.class, Level.SEVERE, ex);
               }
            }
            break;
         case IResourceChangeEvent.POST_CHANGE:
            try {
               IResourceDelta delta = event.getDelta().findMember(new Path(OTE_TEST_LIBS_PROJECT));
               if (delta == null) {
                  return;
               }
               delta.accept(new IResourceDeltaVisitor() {

                  @Override
                  public boolean visit(IResourceDelta delta) {
                     int kind = delta.getKind();
                     if (kind == IResourceDelta.ADDED) {
                     }
                     return false;
                  }

               });
            } catch (CoreException ex) {
               OseeLog.log(OteTestManagerPlugin.class, Level.SEVERE, ex);
            }
            break;
      }
   }

   @Override
   public void dispose() {
      OseeLog.log(OteTestManagerPlugin.class, Level.INFO, "shutting down test manager");
      ResourcesPlugin.getWorkspace().removeResourceChangeListener(OteTestManagerEditor.this);
      hostSelectionTable.dispose();
      super.dispose();
   }

   @Override
   public void createHostWidget(Composite parent) {
	   hostSelectionTable = new HostSelectionTable(parent, SWT.NONE);
   }
   
   public OteTestManagerModel getTestManagerModel() {
      return ((OteTestManagerModel)getModel());
   }

}
