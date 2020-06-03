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

package org.eclipse.ote.ui.message.search;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ote.message.lookup.MessageInputItem;

public class MessageSearchViewContentProvider implements ITreeContentProvider {

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
