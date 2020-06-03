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

package org.eclipse.ote.ui.message.tree;

import org.eclipse.jface.viewers.ViewerSorter;

public class MessageTreeSorter extends ViewerSorter {

   @Override
   public int category(Object element) {
      // TODO: we should categorize by message type (Mux, PUB SUB, WIRE, etc)
      return 1;
   }
}