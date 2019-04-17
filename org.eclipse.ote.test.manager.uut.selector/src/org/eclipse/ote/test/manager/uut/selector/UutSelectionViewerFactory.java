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
package org.eclipse.ote.test.manager.uut.selector;

import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.ExtendedViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutSelectionViewerFactory extends XViewerFactory {
   private static String COLUMN_NAMESPACE = "xviewer.uut.table";
   public static XViewerColumn BLANK =
      new XViewerColumn(COLUMN_NAMESPACE + ".blank", "", 20, XViewerAlign.Left, true, SortDataType.String, false, null);
   public static XViewerColumn SELECTED = new XViewerColumn(COLUMN_NAMESPACE + ".selected", "", 30, XViewerAlign.Left,
      true, SortDataType.String, false, null);
   public static ExtendedViewerColumn PARTITION = new ExtendedViewerColumn(COLUMN_NAMESPACE + ".partition", "SU", 100,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static ExtendedViewerColumn RATE = new ExtendedViewerColumn(COLUMN_NAMESPACE + ".rate", "Rate", 50,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public static ExtendedViewerColumn PATH = new ExtendedViewerColumn(COLUMN_NAMESPACE + ".path", "Path", 650,
      XViewerAlign.Left, true, SortDataType.String, false, null);

   public UutSelectionViewerFactory() {
      super(COLUMN_NAMESPACE);
      registerColumns(BLANK, SELECTED, PARTITION, RATE, PATH);
      addComboEdit(PARTITION);
      addComboEdit(RATE);
      addTextEdit(PATH);
      SELECTED.setToolTip(
         "Selected\nDot:\nSelect a path to make it the default for that script type.\n\nCheck:\nSelect the SU(s) to force an UUT to be run.");
      PARTITION.setToolTip("SU being controlled.");
      RATE.setToolTip("Specify the rate we kick the simulation at.");
      PATH.setToolTip("Path to build file.");
   }

   private void addTextEdit(ExtendedViewerColumn col) {
      col.addMapEntry(UutItemPath.class,
         new CellEditDescriptor(Text.class, SWT.BORDER, col.getName(), UutItemPath.class));
   }

   private void addComboEdit(ExtendedViewerColumn col) {
      col.addMapEntry(UutItemPath.class,
         new CellEditDescriptor(Combo.class, SWT.BORDER, col.getName(), UutItemPath.class));
   }

   @Override
   public XViewerCustomMenu getXViewerCustomMenu() {
      return new UutSelectCustomMenu();
   }

   @Override
   public IXViewerCustomizations getXViewerCustomizations() {
      return new XViewerCustomizations();
   }

   @Override
   public boolean isAdmin() {
      return true;
   }

   @Override
   public boolean isSearchUiAvailable() {
      return true;
   }

   @Override
   public boolean isFilterUiAvailable() {
      return true;
   }

   @Override
   public boolean isLoadedStatusLabelAvailable() {
      return false;
   }
}
