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

package org.eclipse.osee.ote.server;

/**
 * @author Andrew M. Finkbeiner
 */
public class PropertyParamter {
   private final String type;
   private final String version;
   private final String comment;
   private final String station;
   private final boolean useJiniLookup;
   private final boolean isLocalConnector;

   public PropertyParamter(String version, String comment, String station, String type, boolean useJiniLookup, boolean isLocalConnector) {
      this.version = version;
      this.comment = comment;
      this.station = station;
      this.type = type;
      this.useJiniLookup = useJiniLookup;
      this.isLocalConnector = isLocalConnector;
   }

   public String getType() {
      return type;
   }

   public String getVersion() {
      return version;
   }

   public String getComment() {
      return comment;
   }

   public String getStation() {
      return station;
   }

   public boolean isLocalConnector() {
      return isLocalConnector;
   }

   public boolean useJiniLookup() {
      return useJiniLookup;
   }

}