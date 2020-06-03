/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ote.ui;

import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Andy Jury
 */
public class OteNavigateComposite extends XNavigateComposite {

   public OteNavigateComposite(XNavigateViewItems navigateViewItems, Composite parent, int style) {
      super(navigateViewItems, parent, style);
   }

   public OteNavigateComposite(XNavigateViewItems navigateViewItems, Composite parent, int style, String filterText) {
      super(navigateViewItems, parent, style, filterText);
   }

   @Override
   protected void handleDoubleClick(XNavigateItem item, TableLoadOption... tableLoadOptions) {
      disposeTooltip();

      if (item.getChildren().size() > 0) {
         filteredTree.getViewer().setExpandedState(item, true);
      } else {
         try {
            item.run(tableLoadOptions);
         } catch (Exception ex) {
            OseeLog.log(OteNavigateComposite.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

}
