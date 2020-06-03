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

/**
 * @author David N. Phillips
 * @author Andy Jury
 */
public class UutItemPath implements IUutItem {
   private boolean selected;
   private String path;
   private String rate;
   private UutItemPartition parent;

   public UutItemPath(String path) {
      this.path = path;
      rate = "";
   }
   
   public UutItemPath() {
      parent = null;
   }
   
   @Override
   public boolean isSelected() {
      return selected && !getPath().isEmpty();
   }
   
   @Override
   public void setSelected(boolean selected) {
      if (selected && parent != null && parent.getSelectedChild() != null) {
         parent.getSelectedChild().setSelected(false);
      }
      this.selected = selected;
   }
   
   @Override
   public String getPath() {
      return path;
   }
   
   public void setPath(String path) {
      this.path = path;
   }

   public UutItemPartition getParent() {
      return parent;
   }
   
   public void setParent(UutItemPartition parent) {
      if (this.parent == parent) {
         return;
      }
      if (this.parent != null) {
         this.parent.removeChild(this);
      }
      this.parent = parent;
      if (parent != null) {
         parent.addChild(this);
      }
   }
   
   @Override
   public boolean isLeaf() {
      return true;
   }

   @Override
   public String getPartition() {
      if (parent != null) {
         return parent.getPartition();
      }
      return null;
   }

   @Override
   public String getRate() {
      return rate;
   }
   
   public void setRate(String rate) {
      if (rate == null) {
         rate = "";
      }
      else if (rate.matches("^\\d*$")) {
         this.rate = rate;
      }
   }

   public UutItemPath deepCopy(UutItemPartition copyParent) {
      UutItemPath copy = new UutItemPath();
      copy.parent = copyParent;
      copy.path = this.path;
      copy.rate = this.rate;
      copy.selected = this.selected;
      return copy;
   }

}
