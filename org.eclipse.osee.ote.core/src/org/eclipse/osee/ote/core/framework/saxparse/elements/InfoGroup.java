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

package org.eclipse.osee.ote.core.framework.saxparse.elements;

import org.eclipse.osee.ote.core.framework.saxparse.ElementHandlers;
import org.xml.sax.Attributes;

/**
 * @author Andrew M. Finkbeiner
 */
public class InfoGroup extends ElementHandlers {

   public InfoGroup() {
      super("InfoGroup");
   }

   @Override
   public Object createStartElementFoundObject(String uri, String localName, String name, Attributes attributes) {
      return new InfoGroupData(attributes.getValue("title"));
   }

}
