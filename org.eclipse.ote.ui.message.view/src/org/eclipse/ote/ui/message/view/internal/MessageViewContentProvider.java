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
package org.eclipse.ote.ui.message.view.internal;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ote.message.lookup.MessageInputItem;

public class MessageViewContentProvider implements ITreeContentProvider {

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	@Override
	public void dispose() {
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof List){
			List list = (List)element;
			return list.size() > 0;
		} else if(element instanceof MessageInputItem){
			MessageInputItem result = (MessageInputItem)element;
			return result.getChildren().size() > 0;
		}
		return false;
	}
	@Override
	public Object getParent(Object element) {
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object[] getElements(Object element) {
		if(element instanceof List){
			List list = (List)element;
			return list.toArray();
		} else if(element instanceof MessageInputItem){
			MessageInputItem result = (MessageInputItem)element;
			return result.getChildren().toArray();
		}
		return null;
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}
}
