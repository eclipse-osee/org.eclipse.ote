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


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ote.ui.eviewer.Activator;
import org.eclipse.ote.ui.eviewer.view.ViewerColumn;
import org.eclipse.swt.graphics.Image;

final class ElementMessageLabelProvider extends LabelProvider {
	final Image activeImg = Activator.getDefault().getImageRegistry().get("ACTIVE_PNG");
	final Image inactive = Activator.getDefault().getImageRegistry().get("INACTIVE_PNG");

	@Override
	public String getText(Object element) {
		return ((ViewerColumn) element).getVerboseName();
	}

	@Override
	public Image getImage(Object element) {
		return ((ViewerColumn) element).isActive() ? activeImg : inactive;
	}
}