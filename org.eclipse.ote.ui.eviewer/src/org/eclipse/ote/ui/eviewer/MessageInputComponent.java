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
package org.eclipse.ote.ui.eviewer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.ote.message.lookup.MessageInput;
import org.eclipse.ote.message.lookup.MessageInputItem;
import org.eclipse.ote.ui.eviewer.view.ElementViewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class MessageInputComponent implements MessageInput {

	@Override
	public String getLabel() {
		return "Element Viewer";
	}

	@Override
	public void add(List<MessageInputItem> items) {
		try {
			final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			ElementViewer elementViewer;
			elementViewer = (ElementViewer) page.showView(ElementViewer.VIEW_ID);
			recursiveAdd(elementViewer, items);
		} catch (PartInitException e) {
			OseeLog.log(getClass(), Level.SEVERE, "Unable to add messages to MessageWatch", e);
		}
	}
	
	private void recursiveAdd(ElementViewer elementViewer, List<MessageInputItem> items){
		for(MessageInputItem item:items){
			Object[] obj = item.getElementPath();
			if(obj != null){
				elementViewer.addElement(new ElementPath(Arrays.asList(item.getElementPath())));
			} 
			recursiveAdd(elementViewer, item.getChildren());
		}
	}

}