/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.ote.ui.message.watch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class WatchListConfiguration {
	private final AddWatchParameter addWatchParameter = new AddWatchParameter();
	private final List<ElementPath> recBodyElementsToAdd;
	private final List<ElementPath> recHeaderElementsToAdd;
	private final Set<String> recHeaderHex;
	private final Set<String> recBodyHex;
	
	public WatchListConfiguration() {
		recBodyElementsToAdd = new ArrayList<ElementPath>();
		recHeaderElementsToAdd = new ArrayList<ElementPath>();
		recHeaderHex = new HashSet<String>();
		recBodyHex = new HashSet<String>();
	}


	public void addMessage(String message) {
		addWatchParameter.addMessage(message);
	}
	
	public void addPath(ElementPath path) {
		addWatchParameter.addMessage(path.getMessageName(), path);
	}
	
	public void setIsWriter(String message, boolean isWriter) {
		addWatchParameter.setIsWriter(message, isWriter);
	}

	public void setDataType(String message, String type) {
		addWatchParameter.setDataType(message, type);
	}

	public List<ElementPath> getRecBodyElementsToAdd() {
		return recBodyElementsToAdd;
	}

	public List<ElementPath> getRecHeaderElementsToAdd() {
		return recHeaderElementsToAdd;
	}

	public Set<String> getRecHeaderHex() {
		return recHeaderHex;
	}

	public Set<String> getRecBodyHex() {
		return recBodyHex;
	}
	
	AddWatchParameter getAddWatchParameter() {
		return addWatchParameter;
	}
	
}