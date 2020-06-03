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

package org.eclipse.ote.ui.message.watch.recording.xform;

import java.util.LinkedHashSet;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Andrew M. Finkbeiner
 */
public class DetermineElementColumns extends AbstractSaxHandler {

   private final String[] pubSubHeaderElementsToStore = new String[] {"timeTag", "sequenceNum"};
   private final LinkedHashSet<String> elementNames = new LinkedHashSet<String>();
   private String message;

   @Override
   public void endElementFound(String uri, String localName, String qName) throws SAXException {
   }

   @Override
   public void startElementFound(String uri, String localName, String qName, Attributes attributes) throws SAXException {

      if ("Update".equals(localName)) {
         message = attributes.getValue("message");
      } else if ("PubSubHeaderInfo".equals(localName)) {
         for (String str : pubSubHeaderElementsToStore) {
            elementNames.add(String.format("%s.PubSubHeader.%s", message, str));
         }
      } else if ("Element".equals(localName)) {
         elementNames.add(String.format("%s.%s", message, attributes.getValue("name")));
      }
   }

   public String[] getElementColumns() {
      return elementNames.toArray(new String[elementNames.size()]);
   }

}
