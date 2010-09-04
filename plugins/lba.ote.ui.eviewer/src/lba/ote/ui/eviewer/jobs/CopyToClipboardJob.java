/*
 * Created on Jun 14, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package lba.ote.ui.eviewer.jobs;

import java.util.List;
import lba.ote.ui.eviewer.view.ElementColumn;
import lba.ote.ui.eviewer.view.ElementUpdate;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;

/**
 * @author b1529404
 */
public class CopyToClipboardJob extends Job {

   private final List<ElementUpdate> updates;
   private final Clipboard clipboard;
   private final List<ElementColumn> elementColumns;

   public CopyToClipboardJob(Clipboard clipboard, List<ElementColumn> elementColumns, List<ElementUpdate> updates) {
      super("Element Viewer Copy to Clipboard");
      this.clipboard = clipboard;
      this.elementColumns = elementColumns;
      this.updates = updates;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      monitor.beginTask("copy", updates.size());
      try {
         StringBuilder buffer = new StringBuilder(8192);
         int i;
         for (i = 0; i < elementColumns.size() - 1; i++) {
            buffer.append(elementColumns.get(i).getName());
            buffer.append('\t');
         }
         buffer.append(elementColumns.get(i).getName());
         buffer.append('\n');

         for (ElementUpdate update : updates) {
            if (monitor.isCanceled()) {
               sendToClipboard(buffer.toString());
               return Status.CANCEL_STATUS;
            }

            for (i = 0; i < elementColumns.size() - 1; i++) {
               Object o = update.getValue(elementColumns.get(i));
               if (o != null) {
                  buffer.append(o.toString());
               }
               buffer.append('\t');
            }
            Object o = update.getValue(elementColumns.get(i));
            if (o != null) {
               buffer.append(o.toString());
            }
            buffer.append('\n');
            monitor.worked(1);
         }
         sendToClipboard(buffer.toString());
         return Status.OK_STATUS;
      } finally {
         monitor.done();
      }
   }

   private void sendToClipboard(final String text) {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

         @Override
         public void run() {
            clipboard.setContents(new Object[] {text}, new Transfer[] {TextTransfer.getInstance()});
         }
      });
   }

}
