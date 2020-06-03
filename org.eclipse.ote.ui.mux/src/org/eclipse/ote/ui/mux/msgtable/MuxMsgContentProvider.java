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

package org.eclipse.ote.ui.mux.msgtable;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ote.ui.mux.model.MessageModel;

/**
 * @author Ky Komadino
 */
public class MuxMsgContentProvider implements IStructuredContentProvider {
   private final static Object[] EMPTY_ARRAY = new Object[0];
   private Viewer viewer;

   public void refresh() {
      viewer.refresh();
   }

   @Override
   public void inputChanged(Viewer v, Object oldInput, Object newInput) {
      viewer = v;
   }

   @Override
   public void dispose() {
   }

   @Override
   public Object[] getElements(Object parent) {
      if (parent instanceof MessageModel) {
         return ((MessageModel) parent).getChildren().toArray();
      } else {
         return EMPTY_ARRAY;
      }
   }
}
