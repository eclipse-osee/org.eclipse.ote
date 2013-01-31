package org.eclipse.ote.message.lookup;

import java.util.List;

public interface MessageInput {

	String getLabel();
	
	void add(List<MessageInputItem> items);
	
}
