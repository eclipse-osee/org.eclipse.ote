/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.client.msg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.message.commands.RecordCommand;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.tool.IFileTransferHandle;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ken J. Aguilar
 */
public interface IOteMessageService {
   IMessageSubscription subscribe(String name);

   IMessageSubscription subscribe(String name, MessageMode mode);
   
   IMessageSubscription subscribe(String name, DataType dataType, MessageMode mode);

   IMessageSubscription subscribe(String name, String dataType, MessageMode mode);

   IFileTransferHandle startRecording(String fileName, List<RecordCommand.MessageRecordDetails> list) throws FileNotFoundException, IOException;

   void stopRecording() throws Exception;
}
