/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.ote.ui.eviewer.action;

import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.ote.ui.eviewer.view.ViewerColumnLong;

/**
 * @author Ken J. Aguilar
 */
public class ShowTimeDeltaAction extends ShowHideColumnAction {

   private final ElementContentProvider elementContentProvider;

   public ShowTimeDeltaAction(ElementContentProvider elementContentProvider) {
      super("Show Delta Time");
      this.elementContentProvider = elementContentProvider;
   }

   @Override
   protected ViewerColumnLong getViewerColumn() {
      return elementContentProvider.getTimeDeltaColumn();
   }

}
