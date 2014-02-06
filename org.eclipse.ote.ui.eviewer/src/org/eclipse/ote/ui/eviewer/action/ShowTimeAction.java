/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.eviewer.action;

import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.ote.ui.eviewer.view.ViewerColumnLong;

/**
 * @author Ken J. Aguilar
 */
public class ShowTimeAction extends ShowHideColumnAction {

   private final ElementContentProvider elementContentProvider;

   public ShowTimeAction(ElementContentProvider elementContentProvider) {
      super("Show Env Time");
      this.elementContentProvider = elementContentProvider;
   }

   @Override
   protected ViewerColumnLong getViewerColumn() {
      return elementContentProvider.getTimeColumn();
   }

}
