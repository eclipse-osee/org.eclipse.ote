/*
 * Created on Jun 14, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
import org.eclipse.ote.ui.eviewer.view.ElementColumn;
import org.eclipse.ote.ui.eviewer.view.ElementUpdate;

/**
 * @author b1529404
 */
public class CopyToCsvFileJob extends Job {

   private final ElementUpdate[] updates;
   private final File file;
   private final List<ElementColumn> elementColumns;

   public CopyToCsvFileJob(File file, List<ElementColumn> elementColumns, ElementUpdate[] updates) {
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
               writer.append(elementColumns.get(i).getName());
               writer.append(',');
            }
            writer.append(elementColumns.get(i).getName());
            writer.append('\n');

            for (ElementUpdate update : updates) {
               if (monitor.isCanceled()) {
                  writer.flush();
                  return Status.CANCEL_STATUS;
               }

               for (i = 0; i < elementColumns.size() - 1; i++) {
                  Object o = update.getValue(elementColumns.get(i));
                  if (o != null) {
                     writer.append(o.toString());
                  }
                  writer.append(',');
               }
               Object o = update.getValue(elementColumns.get(i));
               if (o != null) {
                  writer.append(o.toString());
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
