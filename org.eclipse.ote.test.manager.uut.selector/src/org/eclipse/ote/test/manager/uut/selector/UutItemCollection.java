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

package org.eclipse.ote.test.manager.uut.selector;

import java.util.ArrayList;
import java.util.List;


/**
 * This class is the root node for the IUutItems and takes care of certain add/remove/chage logic of partition/path trees.
 *
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutItemCollection {
   private final List<UutItemPartition> partitionItems;
   
   public UutItemCollection() {
      partitionItems = new ArrayList<>();
   }
   
   public UutItemPartition[] getPartitions() {
      return partitionItems.toArray(new UutItemPartition[partitionItems.size()]);
   }

   public UutItemPartition getPartitionItem(String partition) {
      UutItemPartition item = null;
      for (UutItemPartition partitionItem : partitionItems) {
         if (partitionItem.getPartition().equals(partition)) {
            item = partitionItem;
            break;
         }
      }
      if (item == null) {
         item = new UutItemPartition(this, partition);
         partitionItems.add(item);
      }
      return item;
   }

   public UutItemPath createItem(String partition, String path) {
      UutItemPath item = new UutItemPath(path);
      UutItemPartition parent = getPartitionItem(partition);
      parent.addChild(item);
      return item;
   }

   public void remove(Object target) {
      for (UutItemPartition partItem : partitionItems) {
         if (partItem == target) {
            partitionItems.remove(partItem);
            return;
         }
         for (UutItemPath item : partItem.getChildren()) {
            if (item == target) {
               partItem.removeChild(item);
               return;
            }
         }
      }
   }

   public void updatePartition(UutItemPath item, String partition) {
      partition = partition.toUpperCase();
      if (item.getParent().getPartition().equals(partition)) {
         return;
      }
      item.setSelected(false);
      item.setParent(getPartitionItem(partition));
   }

   public void clear() {
      partitionItems.clear();
   }
   
   public UutItemCollection deepCopy() {
      UutItemCollection copy = new UutItemCollection();
      for (UutItemPartition partition : partitionItems) {
         copy.partitionItems.add(partition.deepCopy(copy));
      }
      return copy;
   }

}
