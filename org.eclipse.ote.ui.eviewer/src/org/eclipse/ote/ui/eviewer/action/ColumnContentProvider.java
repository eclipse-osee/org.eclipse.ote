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

import java.util.Collection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ote.ui.eviewer.view.ColumnConfiguration;
import org.eclipse.ote.ui.eviewer.view.ColumnDetails;
import org.eclipse.ote.ui.eviewer.view.IColumnConfigurationListener;

/**
 * @author Ken J. Aguilar
 */
public class ColumnContentProvider implements IStructuredContentProvider, IColumnConfigurationListener {

   private ColumnConfiguration configuration;
   private TableViewer viewer;

   @Override
   public Object[] getElements(Object inputElement) {
      return configuration.getColumns().toArray();
   }

   @Override
   public void dispose() {
      if (configuration != null) {
         configuration.removeListener(this);
      }
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      if (oldInput != null) {
         ((ColumnConfiguration) oldInput).removeListener(this);
      }
      if (newInput != null) {
         this.viewer = (TableViewer) viewer;
         configuration = (ColumnConfiguration) newInput;
         configuration.addListener(this);
      }
   }

   @Override
   public void activeStateChanged(Collection<ColumnDetails> columns) {
      viewer.update(columns.toArray(), null);
   }

   @Override
   public void changed() {
      viewer.refresh();
   }

}
