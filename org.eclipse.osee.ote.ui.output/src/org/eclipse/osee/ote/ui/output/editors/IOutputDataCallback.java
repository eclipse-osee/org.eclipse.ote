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

import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.osee.ote.ui.output.tree.items.IOutfileTreeItem;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public interface IOutputDataCallback {
   void addOverviewData(String name, String value);

   void addSummaryData(IOutfileTreeItem item);

   void addUutLogData(final IOutfileTreeItem item);

   void addUutVersionData(final IOutfileTreeItem item);

   void addOteLogData(final IOutfileTreeItem item);

   void addSummaryHeader(String header);

   void addDetailsData(IOutfileTreeItem item);

   void complete();

   void addMarkersToDelete(List<IMarker> markers);

   String getScriptName();

   void setSummaryData(IOutfileTreeItem rootTestPointSummaryItem);

   void addJumpToList(IOutfileTreeItem testpoint);

   void setLargeFile(boolean isLarge);

   void setFailCount(int failCount);
}
