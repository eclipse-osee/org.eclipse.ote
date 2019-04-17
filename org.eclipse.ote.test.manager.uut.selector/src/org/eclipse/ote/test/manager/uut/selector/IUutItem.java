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
