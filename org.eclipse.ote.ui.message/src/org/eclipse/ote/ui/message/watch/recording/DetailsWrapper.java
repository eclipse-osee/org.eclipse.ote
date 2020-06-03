/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.ote.ui.message.watch.recording;

import org.eclipse.osee.ote.message.commands.RecordCommand.MessageRecordDetails;
import org.eclipse.ote.ui.message.tree.MessageNode;
import org.eclipse.ote.ui.message.watch.ElementPath;

/**
 * @author Andrew M. Finkbeiner
 */
public class DetailsWrapper implements IElementPath {

   private MessageRecordDetails details;
   private ElementPath elementPath;
   private MessageNode messageNode;

   public DetailsWrapper(MessageRecordDetails details, ElementPath elementPath) {
      this.details = details;
      this.elementPath = elementPath;
   }

   public DetailsWrapper(MessageRecordDetails details) {
      this.details = details;

   }

   public DetailsWrapper(MessageNode messageNode) {
      this.messageNode = messageNode;
      elementPath = new ElementPath(messageNode.getMessageClassName());
   }

   public MessageRecordDetails getDetails() {
      return details;
   }

   @Override
   public ElementPath getElementPath() {
      return elementPath;
   }

   public MessageNode getMessageNode() {
      return messageNode;
   }
}
