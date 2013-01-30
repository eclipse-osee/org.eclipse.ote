package org.eclipse.ote.message.lookup;

import java.util.ArrayList;
import java.util.List;

public class MessageInputItem {

	private String messageClass;
	private String name;
	private Object[] elementPath;
	private List<MessageInputItem> children;
	
	public MessageInputItem(String messageClass){
		this.messageClass = messageClass;
		this.name = messageClass;
		children = new ArrayList<MessageInputItem>();
	}
	
	public MessageInputItem(String name, String messageClass){
		this(messageClass);
		this.name = name;
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
