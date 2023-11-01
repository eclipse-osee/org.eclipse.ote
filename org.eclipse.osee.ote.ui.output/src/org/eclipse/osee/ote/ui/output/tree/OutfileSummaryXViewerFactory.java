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

package org.eclipse.osee.ote.ui.output.tree;

import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 * @author Andy Jury
 */
public class OutfileSummaryXViewerFactory extends XViewerFactory {

   private static String VIEWER_NAMESPACE = "org.eclipse.osee.ote.ui.output";
   public static XViewerColumn Title = new XViewerColumn("osee.outfilesummary.view.title", "Title", 375,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Expected = new XViewerColumn("osee.outfilesummary.view.expected", "Expected", 200,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Actual = new XViewerColumn("osee.outfilesummary.view.actual", "Actual", 200,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Time = new XViewerColumn("osee.outfilesummary.view.elapsedTime", "Elapsed Time", 105,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn Requirements = new XViewerColumn("osee.outfilesummary.view.requirement", "Requirement", 200,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   private final IManagedForm managedForm;

   public OutfileSummaryXViewerFactory(IManagedForm managedForm) {
      super(VIEWER_NAMESPACE);
      registerColumns(Title, Expected, Actual, Time, Requirements);
      this.managedForm = managedForm;
   }

   @Override
   public XViewerCustomMenu getXViewerCustomMenu() {
      if (managedForm != null) {
         return new OutfileCustomize(managedForm);
      } else {
         return new OutfileCustomize();
      }
   }

   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      return new OutfileCustomizations();
   }

   @Override
   public boolean isAdmin() {
      return true;
   }

   @Override
   public boolean isSearhTop() {
      return true;
   }
}
