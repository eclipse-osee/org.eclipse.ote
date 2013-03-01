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
package org.eclipse.ote.message.lookup;

import java.util.ArrayList;
import java.util.List;

public class MessageInputItem {

	private String messageClass;
	private String name;
	private Object[] elementPath;
	private List<MessageInputItem> children;
   private String type;
	

   public MessageInputItem(String messageClass){
		this.messageClass = messageClass;
		this.name = messageClass;
		this.type = "";
		children = new ArrayList<MessageInputItem>();
	}
	
   public MessageInputItem(String name, String messageClass){
      this(messageClass);
      this.type = "";
      this.name = name;
   }
   
	public MessageInputItem(String name, String type, String messageClass){
		this(messageClass);
		this.type = type;
		this.name = name;
	}
	
	public String getType() {
	   return type;
	}

	public void addElementPath(Object... elementPath){
		this.elementPath = elementPath;
	}
	
	public Object[] getElementPath(){
		return elementPath;
	}
	
	public List<MessageInputItem> getChildren(){
		return children;
	}
	
	public String getMessageClass(){
		return messageClass;
	}
	
	public String getName(){
		return name;
	}

	public void addChild(MessageInputItem elementItem) {
		children.add(elementItem);
	}
	
	public String toString(){
		return name;
	}
}
