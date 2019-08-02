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
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class ResultSummaryXViewerFactory extends XViewerFactory {

   private static String COLUMN_NAMESPACE = "ote.resultsummary.view.";

   public ResultSummaryXViewerFactory() {
      super("ote.resultsummary.view");
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData custData = new CustomizeData();
      List<XViewerColumn> defaultColumns = new ArrayList<>();
      defaultColumns.add(new XViewerColumn("", "", 20, XViewerAlign.Left, true, SortDataType.String, false, null));
      defaultColumns.add(new XViewerColumn(COLUMN_NAMESPACE + ".Title", "Title", 150, XViewerAlign.Center, true,
         SortDataType.String, false, null));
      defaultColumns.add(new XViewerColumn(COLUMN_NAMESPACE + ".Description", "Description", 400, XViewerAlign.Center,
         true, SortDataType.Float, false, null));
      custData.getColumnData().setColumns(defaultColumns);
      return custData;
   }

   @Override
   public XViewerColumn getDefaultXViewerColumn(String id) {
      for (XViewerColumn xCol : getDefaultTableCustomizeData().getColumnData().getColumns()) {
         if (xCol.getId().equals(id)) {
            return xCol;
         }
      }
      return null;
   }

   @Override
   public boolean isAdmin() {
      return true;
   }

}
