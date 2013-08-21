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
import org.eclipse.ote.ui.eviewer.view.ElementContentProvider;

public class PauseUpdatesAction extends Action {
	private final ElementContentProvider elementContentProvider;

//private boolean test = true;
	
	public PauseUpdatesAction(ElementContentProvider elementContentProvider) {
		super("Pause Updates", IAction.AS_CHECK_BOX);
		this.elementContentProvider = elementContentProvider;
		setImageDescriptor(Activator.getImageDescriptor("icons/pause.gif"));
	}

	@Override
	public void run() {
	   elementContentProvider.togglePauseUpdates();
//	   test = !test;
//	   elementContentProvider.setUpdateView(test);
	}

}