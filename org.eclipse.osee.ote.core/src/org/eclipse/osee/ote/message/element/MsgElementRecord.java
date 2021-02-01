/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ote.message.element;

import java.util.Collection;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.RecordElement;

/**
 * This class represents a grouping, or record, of other elements. The elements within this record could include other
 * records.
 * 
 * @author Michael P. Masterson
 */
public class MsgElementRecord {

   /**
    * By setting the path of each of the "fake" source record elements, it makes the search via getElements(path) work.
    * Otherwise the paths would not match and no reader or writer elements would be found.
    * 
    * @param sourceRecord
    * @param sourceMessageClass
    * @param index
    */
   public void setPathsOnSourceRecord(RecordElement sourceRecord, Class<? extends Message> sourceMessageClass, int index) {
      String recordName = sourceRecord.getName();
      Collection<Element> elements = sourceRecord.getElementMap().values();
      for (Element element : elements) {
         element.addPath(sourceMessageClass, recordName, index);
      }
   }

}
