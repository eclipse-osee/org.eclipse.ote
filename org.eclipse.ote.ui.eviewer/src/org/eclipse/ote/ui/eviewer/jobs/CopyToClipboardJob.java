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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ote.ui.eviewer.view.RowUpdate;
import org.eclipse.ote.ui.eviewer.view.ViewerColumn;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

/**
 * @author b1529404
 */
public class CopyToClipboardJob extends Job {

   private final RowUpdate[] updates;
   private final Clipboard clipboard;
   private final List<ViewerColumn> elementColumns;

   public CopyToClipboardJob(Clipboard clipboard, List<ViewerColumn> elementColumns, RowUpdate[] updates) {
      super("Element Viewer Copy to Clipboard");
      this.clipboard = clipboard;
      this.elementColumns = elementColumns;
      this.updates = updates;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {
      monitor.beginTask("copy", updates.length);
      try {
         StringBuilder buffer = new StringBuilder(8192);
         int i;
         for (i = 0; i < elementColumns.size() - 1; i++) {
            buffer.append(elementColumns.get(i).getName());
            buffer.append('\t');
         }
         buffer.append(elementColumns.get(i).getName());
         buffer.append('\n');

         for (RowUpdate update : updates) {
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
      AWorkbench.getDisplay().syncExec(new Runnable() {

         @Override
         public void run() {
            clipboard.setContents(new Object[] {text}, new Transfer[] {TextTransfer.getInstance()});
         }
      });
   }

}
