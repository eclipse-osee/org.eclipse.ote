/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ote.core.log.record;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;

public class PropertyStoreRecord extends TestRecord {

	private static final long serialVersionUID = -6515147544821433102L;
	private IPropertyStore store;

	public PropertyStoreRecord(IPropertyStore store) {
		super(null, Level.INFO, "PropertyStoreRecord", false);
		this.store = store;
	}

	@Override
	public void toXml(XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("PropertyStore");
		if(store != null){
			for(String key:store.keySet()){
				String message = store.get(key);
				writer.writeStartElement("Property");
				writer.writeAttribute("key", key);
				writer.writeCharacters(message);
				writer.writeEndElement();
			}
		}
		writer.writeEndElement();
	}
	
	@JsonProperty
	public Map<String, String> getProperties() {
		Map<String, String> result = new HashMap<>();
		if (store != null && !store.isEmpty()) {
			for (String key : store.keySet()) {
				if (nonEmptyString(store.get(key)) != null) {
					result.put(key, store.get(key));
				}
			}
			return result;
		} else {
			return null;
		}
	}
	
	@Override
   @JsonIgnore
	public String getMessage() {
		return super.getMessage();
	}
}
