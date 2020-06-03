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

/**
 * @author Andrew M. Finkbeiner
 */
public class UutErrorEntryData {

   private final String nodeId;
   private final String count;
   private final String severity;
   private final String version;

   public String getNodeId() {
      return nodeId;
   }

   public String getCount() {
      return count;
   }

   public String getSeverity() {
      return severity;
   }

   public String getVersion() {
      return version;
   }

   UutErrorEntryData(String nodeId, String count, String severity, String version) {
      this.nodeId = nodeId;
      this.count = count;
      this.severity = severity;
      this.version = version;
   }

}
