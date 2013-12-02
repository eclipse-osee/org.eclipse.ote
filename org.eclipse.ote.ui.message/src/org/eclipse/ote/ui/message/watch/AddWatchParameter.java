/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
