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

package org.eclipse.ote.ui.message.tree;

import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.ote.ui.message.watch.ElementPath;

/**
 * @author Ken J. Aguilar
 */
public class HeaderElementNode extends ElementNode {

   private final Element headerElement;

   public HeaderElementNode(Element headerElement) {
      super(new ElementPath(true, headerElement.getElementPath()));
      this.headerElement = headerElement;
   }

   public int getByteOffset() {
      return headerElement.getByteOffset();
   }

   public int getMsb() {
      return headerElement.getMsb();
   }

   public int getLsb() {
      return headerElement.getLsb();
   }
}
