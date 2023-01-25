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
import java.util.Collection;
import java.util.HashMap;

final class MessageParameter {

	private final ArrayList<ElementPath> paths =new ArrayList<ElementPath>(32);
	private final HashMap<ElementPath, String> valueMap = new HashMap<ElementPath, String>();
	private final String messageName;
	private boolean isWriter;
	private String dataType;
	
	MessageParameter(String messageName) {
		this.messageName = messageName;
		this.isWriter = false;
		dataType = null;
	}
	
	public void addAll(Collection<ElementPath> paths) {
		this.paths.addAll(paths);
	}
	
	public void add(ElementPath path) {
		this.paths.add(path);
	}
	
	public String getMessageName() {
		return messageName;
	}
	
	public Collection<ElementPath> getElements() {
		return paths;
	}
	
	
	
	public boolean isWriter() {
		return isWriter;
	}
	
	public void setIsWriter(boolean isWriter) {
		this.isWriter = isWriter;
	}
	
	public void setValue(ElementPath path, String value) {
		valueMap.put(path, value);
	}

	public HashMap<ElementPath, String> getValueMap() {
		return valueMap;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	
}
