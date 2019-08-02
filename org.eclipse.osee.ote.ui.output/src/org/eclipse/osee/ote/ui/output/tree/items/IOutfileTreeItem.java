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
package org.eclipse.osee.ote.ui.output.tree.items;

import java.util.List;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public interface IOutfileTreeItem {
   void setImage(Image image);

   Image getImage();

   List<IOutfileTreeItem> getChildren();

   IOutfileTreeItem getParent();

   void setParent(IOutfileTreeItem item);

   void run();

   void setRunnable(Runnable runme);

   void setLineNumber(String line);

   String getColumnText(int column);

   void setColumnText(int column, String text);

   StyledString getColumnStyledString(int column);

   void setStyledString(int column, StyledString string);

   void setSoftHighlight(boolean highlight);

   public OutfileRowType getType();

   public Object getData();

   public void childTestPointResult(boolean pass);

   public int getChildPasses();

   public int getChildFails();

   @Deprecated
   String getFirstColumn();

   @Deprecated
   String getFourthColumn();

   @Deprecated
   String getFifthColumn();

   @Deprecated
   void setFirstColumn(String label);

   @Deprecated
   void setFourthColumn(String value);

   @Deprecated
   void setFifthColumn(String value);

   @Deprecated
   String getSecondColumn();

   @Deprecated
   void setSecondColumn(String title);

   @Deprecated
   void setThirdColumn(String description);

   @Deprecated
   String getThirdColumn();
}
