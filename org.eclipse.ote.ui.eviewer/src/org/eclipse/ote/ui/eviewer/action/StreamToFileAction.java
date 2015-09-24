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


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ElementViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ken J. Aguilar
 */
public class StreamToFileAction extends Action {
	private final ElementViewer elementViewer;


	public StreamToFileAction(ElementViewer elementViewer) {
		super("Stream To File", IAction.AS_CHECK_BOX);
		this.elementViewer = elementViewer;
		setImageDescriptor(Activator.getImageDescriptor("icons/stream.gif"));
	}

	@Override
	public void run() {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (isChecked()) {
			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			dialog.setFilterExtensions(new String[] {"*.csv"});
			dialog.setText("Save CSV file");
			String result = dialog.open();
			if (result != null) {
				setChecked(elementViewer.startStreaming(null, result, false));
			} else {
				setChecked(false);
			}
		} else {
			elementViewer.stopStreaming();
		}
	}

}
