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
package org.eclipse.osee.ote.ui.output.tree;

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class OutfileCustomizations implements IXViewerCustomizations {

   @Override
   public void deleteCustomization(CustomizeData custData) throws Exception {
      // Intentionally Empty Block
   }

   @Override
   public List<CustomizeData> getSavedCustDatas() {
      return null;
   }

   @Override
   public CustomizeData getUserDefaultCustData() {
      return null;
   }

   @Override
   public boolean isCustomizationPersistAvailable() {
      return false;
   }

   @Override
   public boolean isCustomizationUserDefault(CustomizeData custData) {
      return false;
   }

   @Override
   public void saveCustomization(CustomizeData custData) throws Exception {
      // Intentionally Empty Block
   }

   @Override
   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) {
      // Intentionally Empty Block
   }

}
