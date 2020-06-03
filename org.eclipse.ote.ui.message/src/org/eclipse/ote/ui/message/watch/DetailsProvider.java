/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.ote.ui.message.watch;

import org.eclipse.ote.ui.message.tree.AbstractTreeNode;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Ken J. Aguilar
 */
public abstract class DetailsProvider extends Composite {

   public DetailsProvider(Composite parent, int style) {
      super(parent, style);
   }

   public abstract void render(AbstractTreeNode node);

   public abstract String getTabText();

   public abstract String getTabToolTipText();
}
