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
package org.eclipse.osee.ote.core.framework.saxparse.elements;

/**
 * @author Andrew M. Finkbeiner
 */
public class OfpErrorEntryData {

   private final String nodeId;
   private final String count;
   private final String severity;
   private final String version;

   /**
    * @return the nodeId
    */
   public String getNodeId() {
      return nodeId;
   }

   /**
    * @return the count
    */
   public String getCount() {
      return count;
   }

   /**
    * @return the severity
    */
   public String getSeverity() {
      return severity;
   }

   /**
    * @return the version
    */
   public String getVersion() {
      return version;
   }

   OfpErrorEntryData(String nodeId, String count, String severity, String version) {
      this.nodeId = nodeId;
      this.count = count;
      this.severity = severity;
      this.version = version;
   }

}
