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
public class OutfileXViewerFactory extends XViewerFactory {

   public static XViewerColumn FirstColumn = new XViewerColumn("osee.outfile.view.firstColumn", "", 325,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn SecondColumn = new XViewerColumn("osee.outfile.view.secondColumn", "", 275,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn ThirdColumn = new XViewerColumn("osee.outfile.view.thirdColumn", "", 200,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn FourthColumn = new XViewerColumn("osee.outfile.view.fourthColumn", "", 200,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn FifthColumn = new XViewerColumn("osee.outfile.view.fifthColumn", "", 100,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   private IManagedForm managedForm;

   public OutfileXViewerFactory() {
      super("org.eclipse.osee.ote.ui.output");
      registerColumns(FirstColumn, SecondColumn, ThirdColumn, FourthColumn, FifthColumn);
   }

   public OutfileXViewerFactory(IManagedForm managedForm) {
      this();
      this.managedForm = managedForm;
   }

   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      return new OutfileCustomizations();
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
   public boolean isAdmin() {
      return true;
   }

   @Override
   public boolean isSearhTop() {
      return true;
   }
}
