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
package org.eclipse.osee.ote.client.msg.core.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.client.msg.core.internal.MessageReference;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.osee.ote.messaging.dds.entity.DataReader;
import org.eclipse.osee.ote.messaging.dds.entity.EntityFactory;

/**
 * @author Ken J. Aguilar
 */
public abstract class AbstractMessageDataBase {

	private final HashMap<MessageReference, MessageInstance> referenceToMsgMap =
			new HashMap<MessageReference, MessageInstance>();
	private final ConcurrentHashMap<Integer, MessageInstance> idToMsgMap =
			new ConcurrentHashMap<Integer, MessageInstance>();

	private Executor worker = Executors.newSingleThreadExecutor();
	
	private IMsgToolServiceClient client;
	private volatile boolean connected = false;
	private final DataReader reader = new DataReader(null, null, true, null, new EntityFactory() {

		@Override
		public boolean isEnabled() {
			return true;
		}

	});

	protected AbstractMessageDataBase(IMsgToolServiceClient service) {
	   client = service;
	}

	public MessageInstance findInstance(String name, MessageMode mode, DataType type) {
		MessageReference reference = new MessageReference(type, mode, name);
		return referenceToMsgMap.get(reference);
	}

	public MessageInstance acquireInstance(String name) throws Exception {
		return acquireInstance(name, MessageMode.READER, (DataType) null);
	}

	public MessageInstance acquireInstance(String name, MessageMode mode, DataType type) throws Exception {
		if (type == null) {
			Class<? extends Message> msgClass = ExportClassLoader.getInstance().loadClass(name).asSubclass(Message.class);

			type = msgClass.newInstance().getDefaultMessageData().getType();
		}
		MessageReference reference = new MessageReference(type, mode, name);
		MessageInstance instance = referenceToMsgMap.get(reference);
		if (instance == null) {
			Class<? extends Message> msgClass = ExportClassLoader.getInstance().loadClass(name).asSubclass(Message.class);
			Message msg = createMessage(msgClass);
			for (ArrayList<MessageData> dataList : (Collection<ArrayList<MessageData>>) msg.getAllData()) {
				for (MessageData data : dataList) {
					data.setReader(reader);
				}
			}
			msg.setMemSource(type);
			instance = new MessageInstance(msg, mode, type);
			referenceToMsgMap.put(reference, instance);
		}
		instance.incrementReferenceCount();
		if (connected && !instance.isAttached()) {
			doInstanceAttach(instance);
		}
		return instance;
	}

	public MessageInstance acquireInstance(String name, MessageMode mode, String dataType) throws Exception {
		Class<? extends Message> msgClass = ExportClassLoader.getInstance().loadClass(name).asSubclass(Message.class);
		Message msg = msgClass.newInstance();

		//Set<DataType> available = msg.getAvailableMemTypes();
		Set<DataType> available = msg.getAssociatedMessages().keySet();
		DataType requestDataType = msg.getDefaultMessageData().getType();
		for (DataType type : available) {
			if (type.name().equals(dataType)) {
				requestDataType = type;
				break;
			}

		}
		MessageReference reference = new MessageReference(requestDataType, mode, name);
		MessageInstance instance = referenceToMsgMap.get(reference);
		if (instance == null) {
			msg = createMessage(msgClass);
			msg.setMemSource(requestDataType);
			for (ArrayList<MessageData> dataList : (Collection<ArrayList<MessageData>>) msg.getAllData()) {
				for (MessageData data : dataList) {
					data.setReader(reader);
				}
			}
			instance = new MessageInstance(msg, mode, requestDataType);
			referenceToMsgMap.put(reference, instance);
		}
		instance.incrementReferenceCount();
		if (connected && !instance.isAttached()) {
			doInstanceAttach(instance);
		}
		return instance;
	}


	public void releaseInstance(MessageInstance instance) throws Exception {
		instance.decrementReferenceCount();
		if (!instance.hasReferences()) {
			if (instance.isAttached()) {
				doInstanceDetach(instance);
			}
			MessageReference reference =
					new MessageReference(instance.getType(), instance.getMode(), instance.getMessage().getClass().getName());
			referenceToMsgMap.remove(reference);
			destroyMessage(instance.getMessage());
		}

	}

	protected abstract Message createMessage(Class<? extends Message> msgClass) throws Exception;

	protected abstract void destroyMessage(Message message) throws Exception;

	public void attachToService(IMsgToolServiceClient client) {
	   connected = true;
		this.client = client;
		for (MessageInstance instance : referenceToMsgMap.values()) {
			try {
				doInstanceAttach(instance);
			} catch (Exception e) {
				OseeLog.log(AbstractMessageDataBase.class, Level.SEVERE,
						"could not attach instance for " + instance.toString(), e);
			}
		}
	}

	public void detachService() {
		for (MessageInstance instance : referenceToMsgMap.values()) {
			doInstanceDetach(instance);
		}
		connected = false;
		this.client = null;
	}

	public MessageInstance findById(int id) {
		return idToMsgMap.get(id);
	}

	private boolean doInstanceAttach(MessageInstance instance) throws Exception {
	   worker.execute(new Runnable() {
         @Override
         public void run() {
            Integer id;
            try {
               id = instance.attachToService(client);
               if (id != null) {
                  idToMsgMap.put(id, instance);
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
		return true;
	}

	private void doInstanceDetach(MessageInstance instance) {
		try {
			Integer id = instance.getId();
			if (id != null) {
				idToMsgMap.remove(id);
			}
			instance.detachService(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
