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

/**
 * @author Andrew M. Finkbeiner
 */
public class BodyElements extends DetailsWrapper {

   public BodyElements(MessageRecordDetails details) {
      super(details);
   }

   public BodyElements(MessageNode messageNode) {
      super(messageNode);
   }

   @Override
   public String toString() {
      return "Body Elements";
   }
}