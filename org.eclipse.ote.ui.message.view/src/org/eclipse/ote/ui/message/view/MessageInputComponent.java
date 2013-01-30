package org.eclipse.ote.ui.message.view;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.ote.message.lookup.MessageInput;

public class MessageInputComponent {
	
	public List<MessageInput> messageInputs;
	
	public MessageInputComponent(){
		messageInputs = new CopyOnWriteArrayList<MessageInput>();
	}
	
	public void addMessageInput(MessageInput messageInput){
		if(!messageInputs.contains(messageInput)){
			messageInputs.add(messageInput);
		}
	}
	
	public void removeMessageInput(MessageInput messageInput){
		messageInputs.remove(messageInput);
	}
	
	public List<MessageInput> getMessageInputs(){
		return messageInputs;
	}
}
