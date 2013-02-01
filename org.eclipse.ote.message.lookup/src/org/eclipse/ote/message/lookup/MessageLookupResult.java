/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.message.lookup;

import java.util.ArrayList;
import java.util.List;

public class MessageLookupResult {

	private final String messageName;
	private List<String> elements;
	private String messageClass;
	private String messageType;
	private int id;
	
	public MessageLookupResult(String messageClass, String messageName, String messageType, int id){
		this.messageName = messageName;
		this.messageClass = messageClass;
		this.messageType = messageType;
		this.id = id;
		elements = new ArrayList<String>();
	}
	
	public String getMessageType(){
	   return messageType;
	}
	
	public String toString(){
		return String.format("%s name[%s] type[%s] id[%d]", messageClass, messageName, messageType, id);
	}

	public String getMessageName() {
		return messageName;
	}
	
	public String getClassName() {
		return messageClass;
	}

	public void addElement(String element) {
		elements.add(element);
	}

	public List<String> getElements() {
		return elements;
	}
}
