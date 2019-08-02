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
package org.eclipse.osee.ote.ui.output.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.ui.output.Activator;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OteOutput extends FormEditor implements IOutputDataCallback {
   @Override
   public void close(boolean save) {
      super.close(save);
   }

   private OverviewPage overview;
   private DetailPage details;
   private final List<IMarker> markersToDelete;
   private String name;

   public OteOutput() {
      markersToDelete = new ArrayList<>();

   }

   @Override
   protected void setInput(IEditorInput input) {
      super.setInput(input);

      setPartName(input.getName());

      ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {

         @Override
         public void resourceChanged(final IResourceChangeEvent event) {
            IResourceDelta delta = event.getDelta();
            try {
               delta.accept(new IResourceDeltaVisitor() {
                  @Override
                  public boolean visit(IResourceDelta delta) {
                     if (overview == null) {
                        return false;
                     }
                     IPath path = delta.getFullPath();
                     String extension = path.getFileExtension();
                     if (extension != null) {
                        if ("tmo".equals(extension)) {
                           IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
                           IFile filed = ((IFileEditorInput) getEditorInput()).getFile();
                           if (file.equals(filed)) {
                              switch (delta.getKind()) {
                              case IResourceDelta.ADDED:
                                 overview.setNeedRefresh(true);
                                 break;
                              case IResourceDelta.CHANGED:
                                 overview.setNeedRefresh(true);
                                 break;
                              case IResourceDelta.REMOVED:
                                 break;
                              }
                           }
                        }
                     }
                     if (delta.getAffectedChildren().length > 0) {
                        return true;
                     }
                     return false;
                  }
               });
            } catch (CoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }

      }, IResourceChangeEvent.POST_CHANGE);
   }

   private void processOutfile(IEditorInput contents) {
      if (overview != null) {
         overview.setNeedRefresh(false);
      }
      Jobs.runInJob(String.format("Process [%s]", contents.getName()), new ProcessOutfileOverview(contents, this),
            Activator.class, Activator.PLUGIN_ID);

   }

   @Override
   protected void addPages() {
      try {
         overview = new OverviewPage(this);
         addPage(overview);
         details = new DetailPage(this);
         addPage(details);
         details.setCallback(this);
         details.setInput(getEditorInput());
         this.setActivePage(1);
         this.setActivePage(0);
         details.setRunProcessJob(true);
         processOutfile(getEditorInput());
      } catch (PartInitException ex) {
         // Intentionally Empty Block
      }
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      // Intentionally Empty Block
   }

   @Override
   public void doSaveAs() {
      // Intentionally Empty Block
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   @Override
   public void dispose() {
      super.dispose();

      int count = this.getPageCount();

      for (int i = 0; i < count; i++) {
         this.removePage(0);
      }
      overview = null;
      details = null;

      for (IMarker marker : markersToDelete) {
         try {
            marker.delete();
         } catch (CoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

   }

   public void navigateToItemOnDetailsPage(final IOutfileTreeItem outfileTreeItem) {

      if (details.needToRunProcessJob()) {
         details.setRunProcessJob(false);
         final OteJob otejob = details.getProcessDetailsJob();
         Job job = new Job(otejob.toString()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               return otejob.run(monitor);
            }

         };
         job.addJobChangeListener(new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               Displays.pendInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     setActivePage(details.getId());
                     details.navigateToTreeItem(outfileTreeItem);
                  }
               });
            }
         });
         job.schedule();
      } else {
         this.setActivePage(details.getId());
         details.navigateToTreeItem(outfileTreeItem);
      }
   }

   public void addContent(final StringBuilder sb) {
      if (details.needToRunProcessJob()) {
         final OteJob otejob = details.getProcessDetailsJob();
         Job job = new Job(otejob.toString()) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               return otejob.run(monitor);
            }

         };
         job.addJobChangeListener(new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               overview.addContent(sb);
               details.addContent(sb);
            }
         });
         job.schedule();
      } else {
         overview.addContent(sb);
         details.addContent(sb);
      }
   }

   public void refresh() {
      overview.clear();
      details.clear();
      processOutfile(getEditorInput());
   }

   @Override
   public void addMarkersToDelete(List<IMarker> markers) {
      markersToDelete.addAll(markers);
   }

   @Override
   public void addDetailsData(final IOutfileTreeItem root) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            long time = System.currentTimeMillis();
            if (details != null) {
               details.addDetailsData(root);
            }
            System.out.println(System.currentTimeMillis() - time);
         }
      });
   }

   @Override
   public void addOverviewData(final String name, final String value) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            overview.addOverviewData(name, value);
         }
      });
   }

   @Override
   public void complete() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            overview.refresh();
            details.refresh();
         }
      });
   }

   @Override
   public void addSummaryData(final IOutfileTreeItem item) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            overview.addSummaryData(item);
         }
      });
   }

   @Override
   public void addUutLogData(final IOutfileTreeItem item) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            overview.addUutLogData(item);
         }
      });
   }

   @Override
   public void addUutVersionData(final IOutfileTreeItem item) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            overview.addUutVersionData(item);
         }
      });
   }

   @Override
   public void addOteLogData(final IOutfileTreeItem item) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            overview.addOteLogData(item);
         }
      });
   }

   @Override
   public void addSummaryHeader(final String header) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            overview.addSummaryHeader(header);
         }
      });
   }

   @Override
   public void addJumpToList(IOutfileTreeItem testpoint) {
      details.addJumpToList(testpoint);
   }

   @Override
   public String getScriptName() {
      if (name == null) {
         name = getEditorInput().getName();
         int index = name.indexOf('.');
         if (index > 0) {
            name = name.substring(0, index);
         }
      }
      return name;
   }

   public void openInFlatTextFile() {

      final OteJob openInTextFile =
            new OteJob(String.format("Opening OTE output [%s] as a text file.",
                  ((IFileEditorInput) getEditorInput()).getFile().getName())) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            StringBuilder sb = new StringBuilder();
            addContent(sb);
            IPath path = ((IFileEditorInput) getEditorInput()).getFile().getFullPath();
            path = path.addFileExtension("txt");
            final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
            try {
               AIFile.writeToFile(file, new CharBackedInputStream(sb.toString()));
               Displays.pendInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                     try {
                        IDE.openEditor(page, file, true);
                     } catch (PartInitException ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  }
               });

            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            return Status.OK_STATUS;
         }

      };

      if (details.needToRunProcessJob()) {
         OteJob processDetails = details.getProcessDetailsJob();
         details.setRunProcessJob(false);

         JobList jobs = new JobList(processDetails, openInTextFile);
         jobs.schedule();
      } else {
         Job job = new Job(openInTextFile.toString()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
               return openInTextFile.run(monitor);
            }
         };
         job.schedule();
      }

   }

   @Override
   public void setSummaryData(IOutfileTreeItem rootTestPointSummaryItem) {
      overview.setSummaryData(rootTestPointSummaryItem);
   }

   @Override
   public void setLargeFile(boolean isLarge) {
      overview.setLargeFile(isLarge);
      details.setLargeFile(isLarge);
   }

   @Override
   public void setFailCount(int failCount) {
      overview.setFailCount(failCount);
      details.setFailCount(failCount);
   }

}
