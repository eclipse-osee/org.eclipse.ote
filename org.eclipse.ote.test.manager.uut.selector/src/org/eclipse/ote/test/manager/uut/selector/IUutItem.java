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

package org.eclipse.ote.test.manager.uut.selector;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public interface IUutItem {
   public String getPath();
   public String getPartition();
   public String getRate();
   public boolean isSelected();
   public void setSelected(boolean selected);
   public boolean isLeaf();
}
