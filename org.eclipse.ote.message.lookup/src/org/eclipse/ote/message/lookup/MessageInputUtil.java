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

public class MessageInputUtil {

	public static List<MessageInputItem> messageLookupResultToMessageInputItem(List<MessageLookupResult> messageLookupResults){
		List<MessageInputItem> messageInput = new ArrayList<MessageInputItem>(messageLookupResults.size());
		for(MessageLookupResult result:messageLookupResults){
			MessageInputItem inputItem = new MessageInputItem(result.getMessageName(),result.getMessageType(), result.getClassName());
			messageInput.add(inputItem);
			for(String element:result.getElements()){
				MessageInputItem elementItem = new MessageInputItem(element, result.getClassName());
				String[] paths = element.split("\\+");
				List<Object> elementObjs = new ArrayList<Object>();
				elementObjs.add(result.getClassName());
				for(String path:paths){
				   int number = 0;
				   try{
				      number = Integer.parseInt(path);
				      elementObjs.add(number);
				   } catch(NumberFormatException ex){
				      elementObjs.add(path);
				   }
				}
				elementItem.addElementPath(elementObjs.toArray());
				inputItem.addChild(elementItem);
			}
		}
		return messageInput;
	}
	
}
