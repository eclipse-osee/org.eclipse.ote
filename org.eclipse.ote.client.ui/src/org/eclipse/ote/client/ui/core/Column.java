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
package org.eclipse.ote.client.ui.core;

/**
 * @author Andrew M. Finkbeiner
 */
public enum Column {
   CONNECTED("", 20) {
      @Override
      public String getColumnText(TestHostItem item) {
         return "";
      }
   },
   HOST_COLUMN("Host", 150) {
      @Override
      public String getColumnText(TestHostItem item) {
         return item.getProperties().getStation();
      }
   },
   COMMENT_COLUMN("Comment", 240) {
      @Override
      public String getColumnText(TestHostItem item) {
         return item.getProperties().getName();
      }
   },
   USERS_COLUMN("Users", 120) {
      @Override
      public String getColumnText(TestHostItem item) {
         return item.getProperties().getUserList();
      }
   },
   TYPE_COLUMN("Type", 70) {
      @Override
      public String getColumnText(TestHostItem item) {
         return item.getProperties().getType();
      }
   },
   UPDATE_COLUMN("Last Update", 160) {
      @Override
      public String getColumnText(TestHostItem item) {
         return item.getProperties().getDateStarted().toString();
      }
   },
   VERSION_COLUMN("Version", 120) {
      @Override
      public String getColumnText(TestHostItem item) {
         return item.getProperties().getVersion();
      }
   };

   private final int width;
   private final String displayText;

   private Column(String displayText) {
      this.displayText = displayText;
      width = name().length();
   }

   private Column(String displayText, int width) {
      this.displayText = displayText;
      this.width = width;
   }

   public int getWidth() {
      return width;
   }

   public String getColumnName() {
      return displayText;
   }

   public abstract String getColumnText(TestHostItem item);
}