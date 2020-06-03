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

package org.eclipse.osee.ote.ui.output.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class NavigateToFile implements Runnable {

   private final String source;
   private final String line;

   public NavigateToFile(String source, String line) {
      this.source = source;
      this.line = line;
   }

   @Override
   public void run() {
      String file = source.substring(source.lastIndexOf(".") + 1);
      int innerMarker = file.indexOf("$");
      if (innerMarker > 0) {
         file = file.substring(0, file.indexOf("$"));
      }
      file += ".java";
      try {
         int linenumber = Integer.parseInt(line);
         IResource resource = AWorkspace.findWorkspaceFile(file);
         if (resource != null) {

            IMarker marker;
            marker = resource.createMarker(IMarker.MARKER);
            MarkerUtilities.setLineNumber(marker, linenumber);
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            IDE.openEditor(page, marker, true);
            marker.delete();
         }
      } catch (CoreException ex) {
         // Do Nothing
      }
   }
}
