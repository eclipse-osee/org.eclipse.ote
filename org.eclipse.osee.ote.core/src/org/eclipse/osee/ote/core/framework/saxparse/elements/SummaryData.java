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
public class SummaryData {

   private final String criticalCount;
   private final String exceptionCount;
   private final String informationCount;
   private final String minorCount;
   private final String nodeId;
   private final String seriousCount;
   private final String startNumber;

   SummaryData(String criticalCount, String exceptionCount, String informationCount, String minorCount, String nodeId, String seriousCount, String startNumber) {
      this.criticalCount = criticalCount;
      this.exceptionCount = exceptionCount;
      this.informationCount = informationCount;
      this.minorCount = minorCount;
      this.nodeId = nodeId;
      this.seriousCount = seriousCount;
      this.startNumber = startNumber;
   }

   /**
    * @return the criticalCount
    */
   public String getCriticalCount() {
      return criticalCount;
   }

   /**
    * @return the exceptionCount
    */
   public String getExceptionCount() {
      return exceptionCount;
   }

   /**
    * @return the informationCount
    */
   public String getInformationCount() {
      return informationCount;
   }

   /**
    * @return the minorCount
    */
   public String getMinorCount() {
      return minorCount;
   }

   /**
    * @return the nodeId
    */
   public String getNodeId() {
      return nodeId;
   }

   /**
    * @return the seriousCount
    */
   public String getSeriousCount() {
      return seriousCount;
   }

   /**
    * @return the startNumber
    */
   public String getStartNumber() {
      return startNumber;
   }
}
