package org.eclipse.ote.ui.message.view.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.ote.message.lookup.MessageInput;

public class MessageInputComponent {
	
	public List<MessageInput> messageInputs;
	
	public MessageInputComponent(){
		messageInputs = new ArrayList<MessageInput>();
	}
	
	public synchronized void addMessageInput(MessageInput messageInput){
		if(!messageInputs.contains(messageInput)){
			messageInputs.add(messageInput);
			Collections.sort(messageInputs, new Comparator<MessageInput>() {
            @Override
            public int compare(MessageInput arg0, MessageInput arg1) {
               return arg0.getLabel().compareTo(arg1.getLabel());
            }
         });
		}
	}
	
	public synchronized void removeMessageInput(MessageInput messageInput){
		messageInputs.remove(messageInput);
	}
	
	public synchronized List<MessageInput> getMessageInputs(){
	   return new ArrayList<MessageInput>(messageInputs);
	}
}
