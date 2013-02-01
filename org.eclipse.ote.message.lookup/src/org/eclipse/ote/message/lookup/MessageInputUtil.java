package org.eclipse.ote.message.lookup;

import java.util.ArrayList;
import java.util.List;

public class MessageInputUtil {

	public static List<MessageInputItem> messageLookupResultToMessageInputItem(List<MessageLookupResult> messageLookupResults){
		List<MessageInputItem> messageInput = new ArrayList<MessageInputItem>(messageLookupResults.size());
		for(MessageLookupResult result:messageLookupResults){
			MessageInputItem inputItem = new MessageInputItem(result.getMessageName(),result.getMessageType(), result.getClassName());
			messageInput.add(inputItem);
			for(String element:result.getElements()){
				MessageInputItem elementItem = new MessageInputItem(element, result.getClassName());
				elementItem.addElementPath(result.getClassName(), element);
				inputItem.addChild(elementItem);
			}
		}
		return messageInput;
	}
	
}
