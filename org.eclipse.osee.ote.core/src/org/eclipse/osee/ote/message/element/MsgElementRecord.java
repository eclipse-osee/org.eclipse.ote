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
 * @param <R> Source record element type
 */
public class MsgElementRecord<R extends RecordElement> {

   protected R sourceRecord;

   public MsgElementRecord(R sourceRecord) {
      this.sourceRecord = sourceRecord;
   }

   /**
    * By setting the path of each of the "fake" source record elements, it makes the search via getElements(path) work.
    * Otherwise the paths would not match and no reader or writer elements would be found.
    * 
    * @param sourceRecord
    * @param sourceMessageClass
    * @param index
    */
   public void setPathsBasedOnSourceRecord(Class<? extends Message> sourceMessageClass, int index) {
      String recordName = sourceRecord.getName();
      Collection<Element> elements = sourceRecord.getElementMap().values();
      for (Element element : elements) {
         element.addPath(sourceMessageClass, recordName, index);
      }
   }

   /**
    * For records within records it is important to prepend the parent record name and index in the path
    * 
    * @param parent
    */
   public void addParentRecordPath(RecordElement parent) {
      Collection<Element> values = sourceRecord.getElementMap().values();
      for (Element element : values) {
         // 0 index should be the parent message name so leave that alone
         element.getElementPath().add(1, parent.getElementName());
         element.getElementPath().add(2, parent.getIndex());
      }
   }


}
