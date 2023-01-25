/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.ote.bytemessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;

import org.eclipse.osee.ote.message.elements.ArrayElement;

public class SerializedClassMessage<T> extends OteByteMessage {

	public static final int _BYTE_SIZE = 0;

	public ArrayElement OBJECT;

	public SerializedClassMessage(String topic) {
		super(SerializedClassMessage.class.getSimpleName(), topic, 1, _BYTE_SIZE);
		OBJECT = new ArrayElement(this, "CLAZZ", getDefaultMessageData(), 0, 0, 0);
		addElements(OBJECT);
	}
	

	public SerializedClassMessage(String topic, Serializable object) throws IOException {
		this(topic);
		setObject(object);
	}
	
	public SerializedClassMessage(byte[] bytes) {
		super(bytes);
		OBJECT = new ArrayElement(this, "CLAZZ", getDefaultMessageData(), 0, 0, 0);
		addElements(OBJECT);
	}

	public void setObject(Serializable obj) throws IOException{
		byte[] data = serializeObject(obj);
		int offset = OBJECT.getByteOffset() + getHeaderSize();
		byte[] newData = new byte[data.length + offset];
		System.arraycopy(getData(), 0, newData, 0, offset);
		System.arraycopy(data, 0, newData, offset, data.length);
		getDefaultMessageData().setNewBackingBuffer(newData);
	}
	
	@SuppressWarnings("unchecked")
   public T getObject() throws IOException, ClassNotFoundException{
		int offset = OBJECT.getByteOffset() + getHeaderSize();
		ByteArrayInputStream bis = new ByteArrayInputStream(getData(), offset, getData().length - offset);
		ObjectInputStream ois = new ObjectInputStream(bis);
		return (T)ois.readObject();
	}
	
	public T getObject(final Class<T> clazz) throws IOException, ClassNotFoundException{
		return getObject(clazz, clazz.getClassLoader());
	}
	
	public T getObject(final Class<T> clazz, final ClassLoader loader) throws IOException, ClassNotFoundException{
		int offset = OBJECT.getByteOffset() + getHeaderSize();
		ByteArrayInputStream bis = new ByteArrayInputStream(getData(), offset, getData().length - offset);
		ObjectInputStream ois = new ObjectInputStream(bis) {

			@Override
			protected Class<?> resolveClass(ObjectStreamClass desc)
					throws IOException, ClassNotFoundException {
				try {
					return loader.loadClass(desc.getName());
				} catch (ClassNotFoundException e) {
					try {
						return Thread.currentThread().getContextClassLoader().loadClass(desc.getName());
					} catch (ClassNotFoundException e1) {
						return super.resolveClass(desc);
					}
				}
			}};
		return clazz.cast(ois.readObject());
	}
	
}  
