/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.eviewer.jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ViewerColumn;
import org.eclipse.ote.ui.eviewer.view.RowUpdate;

/**
 * @author b1529404
 */
public class CopyToCsvFileJob extends Job {

   private final RowUpdate[] updates;
   private final File file;
   private final List<ViewerColumn> elementColumns;

   public CopyToCsvFileJob(File file, List<ViewerColumn> elementColumns, RowUpdate[] updates) {
      super("Element Viewer Save to CSV");
      this.file = file;
      this.elementColumns = elementColumns;
      this.updates = updates;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      monitor.beginTask("copy", updates.length);
      try {
         PrintWriter writer = new PrintWriter(new FileOutputStream(file));
         try {            
            int i;
            for (i = 0; i < elementColumns.size() - 1; i++) {
               writer.append(elementColumns.get(i).getVerboseName());
               writer.append(',');
            }
            writer.append(elementColumns.get(i).getVerboseName());
            writer.append('\n');

            for (RowUpdate update : updates) {
               if (monitor.isCanceled()) {
                  writer.flush();
                  return Status.CANCEL_STATUS;
               }

               for (i = 0; i < elementColumns.size() - 1; i++) {
                  Object o = update.getValue(elementColumns.get(i));
                  if (o != null) {
                     writer.append('"').append(o.toString()).append('"');
                  }
                  writer.append(',');
               }
               Object o = update.getValue(elementColumns.get(i));
               if (o != null) {
                  writer.append('"').append(o.toString()).append('"');
               }
               writer.append('\n');
               monitor.worked(1);
            }
            writer.flush();
            return Status.OK_STATUS;
         } finally {
            writer.close();
         }
      } catch (Exception e) {
         return new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
            "An exception occurred while copying updates", e);
      } finally {
         monitor.done();
      }
   }

}
