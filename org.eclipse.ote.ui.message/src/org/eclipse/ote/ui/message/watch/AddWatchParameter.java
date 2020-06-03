/*********************************************************************
 * Copyright (c) 2010 Boeing
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ken J. Aguilar
 */
public class AddWatchParameter {
	private final HashMap<String, MessageParameter> watchMap = new HashMap<String, MessageParameter>(64);

	public AddWatchParameter() {
		super();
	}

	public AddWatchParameter(String message) {
		addMessage(message);
	}

	public AddWatchParameter(String message, ElementPath element) {
		addMessage(message, element);
	}

	public AddWatchParameter(Map<String, MessageParameter> map) {
		watchMap.putAll(map);
	}

	public void addMessage(String name, Collection<ElementPath> elements) {
		MessageParameter parameter = watchMap.get(name);
		if (parameter == null) {
			parameter = new MessageParameter(name);
			watchMap.put(name, parameter);
		}
		parameter.addAll(elements);
	}

	public void addMessageWithAllElements(String name) {
		watchMap.put(name, null);
	}

	public void addMessage(String name, ElementPath... elements) {
		MessageParameter parameter = watchMap.get(name);
		if (parameter == null) {
			parameter = new MessageParameter(name);
			watchMap.put(name, parameter);
		}
		for (ElementPath element : elements) {
			parameter.add(element);
		}
	}

	public void addMessage(String name) {
		MessageParameter parameter = watchMap.get(name);
		if (parameter == null) {
			parameter = new MessageParameter(name);
			watchMap.put(name, parameter);
		}
	}

	public void addMessage(String name, ElementPath element) {
		MessageParameter parameter = watchMap.get(name);
		if (parameter == null) {
			parameter = new MessageParameter(name);
			watchMap.put(name, parameter);
		}
		parameter.add(element);
	}

	public Collection<String> getMessages() {
		return watchMap.keySet();
	}

	public Collection<ElementPath> getMessageElements(String messageNmae) {
		return watchMap.get(messageNmae).getElements();
	}

	public boolean containsMessage(String name) {
		return watchMap.containsKey(name);
	}
	
	public void setIsWriter(String messageName, boolean isWriter) {
		MessageParameter parameter = watchMap.get(messageName);
		if (parameter == null) {
			parameter = new MessageParameter(messageName);
			watchMap.put(messageName, parameter);
		}
		parameter.setIsWriter(isWriter);
	}
	
	public void setDataType(String messageName, String type) {
		MessageParameter parameter = watchMap.get(messageName);
		if (parameter == null) {
			parameter = new MessageParameter(messageName);
			watchMap.put(messageName, parameter);
		}
		parameter.setDataType(type);
	}
	
	public void setValue(ElementPath path, String value) {
		String messageName = path.getMessageName();
		MessageParameter parameter = watchMap.get(messageName);
		if (parameter == null) {
			parameter = new MessageParameter(messageName);
			watchMap.put(messageName, parameter);
		}
		parameter.setValue(path, value);
		
	}
	
	public Collection<MessageParameter> getMessageParameters() {
		return watchMap.values();
	}
}
