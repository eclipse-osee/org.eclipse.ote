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

import java.util.HashSet;

import org.eclipse.jface.action.Action;
import org.eclipse.ote.ui.eviewer.view.ElementColumn;
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @author Ken J. Aguilar
 */
public class RemoveColumnAction extends Action {

	private final ElementContentProvider elementContentProvider;

	public RemoveColumnAction(ElementContentProvider elementContentProvider) {
		super("Remove columns");
		this.elementContentProvider = elementContentProvider;
	}

	@Override
	public void run() {

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(Display.getCurrent().getActiveShell(),new ElementMessageLabelProvider());
		dialog.setElements(elementContentProvider.getColumns().toArray());
		dialog.setTitle("Remove Columns");
		dialog.setMessage("Select one or more columns to remove");
		dialog.setEmptySelectionMessage("no columns selected");
		dialog.setIgnoreCase(true);
		dialog.setMultipleSelection(true);
		dialog.open();
		Object[] selection = dialog.getResult();
		if (selection != null) {
			HashSet<ElementColumn> removed = new HashSet<ElementColumn>();
			for (Object obj : selection) {
				removed.add((ElementColumn)obj);
			}
			elementContentProvider.removeColumn(removed);
		}
	}
	
	
}
