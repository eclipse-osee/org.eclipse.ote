/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.ote.ui.eviewer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ote.ui.eviewer.view.ViewerColumn;
import org.eclipse.ote.ui.eviewer.view.ViewerColumnLong;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Ken J. Aguilar
 */
public abstract class ShowHideColumnAction extends Action {


   public ShowHideColumnAction(String title) {
      super(title, IAction.AS_CHECK_BOX);
      this.setChecked(true);
   }

   @Override
   public void run() {
      ViewerColumn timeColumn = getViewerColumn();
      TableColumn column = timeColumn.getColumn();
      if (isChecked()) {
         column.setWidth(timeColumn.getDefaultColumnWidth());
      } else {
         timeColumn.setDefaultColumnWidth(column.getWidth());
         column.setWidth(0);
      }
   }

   protected abstract ViewerColumnLong getViewerColumn();

}
