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
package org.eclipse.ote.test.manager.uut.selector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutItemPartition implements IUutItem {
   private boolean selected;
   private String partition;
   private List<UutItemPath> children;
   private UutItemCollection parent;

   UutItemPartition(UutItemCollection parent, String partition) {
      children = new ArrayList<>();
      this.parent = parent;
      this.partition = partition;
   }

   public UutItemCollection getParent() {
      return parent;
   }
   
   @Override
   public boolean isSelected() {
      if (selected && getPath().isEmpty()) {
         selected = false;
      }
      return selected;
   }
   
   @Override
   public void setSelected(boolean selected) {
      this.selected = selected;
   }
   
   @Override
   public String getPartition() {
      return partition;
   }
   
   public void setPartition(String partition) {
      this.partition = partition.toUpperCase();
   }
   
   @Override
   public String getPath() {
      UutItemPath child = getSelectedChild();
      if (child != null) {
         return child.getPath();
      }
      return "";
   }
   
   public List<UutItemPath> getChildren() {
      return children;
   }
   
   public void addChild(UutItemPath item) {
      if (!children.contains(item)) {
         children.add(item);
      }
      item.setParent(this);
   }
   
   public void removeChild(UutItemPath item) {
      children.remove(item);
   }
   
   public boolean hasChildren() {
      return children.size() > 0;
   }

   public void select(UutItemPartition item) {
      item.setSelected(true);
   }
   
   public UutItemPath getSelectedChild() {
      for (UutItemPath child : children) {
         if (child.isSelected()) {
            return child;
         }
      }
      return null;
   }
   
   @Override
   public boolean isLeaf() {
      return false;
   }

   @Override
   public String getRate() {
      IUutItem child = getSelectedChild();
      if (child != null) {
         return child.getRate();
      }
      return "";
   }

   public UutItemPartition deepCopy(UutItemCollection copyParent) {
      UutItemPartition copy = new UutItemPartition(copyParent, this.partition);
      copy.selected = this.selected;
      for (UutItemPath uutPath : children) {
         copy.children.add(uutPath.deepCopy(copy));
      }
      return copy;
   }

}
