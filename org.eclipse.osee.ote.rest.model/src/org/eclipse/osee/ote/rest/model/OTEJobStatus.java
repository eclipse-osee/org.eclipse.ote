/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.rest.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import java.net.URL;

@XmlRootElement
public class OTEJobStatus {

   private URL updatedJobStatus;
   private String jobId;

   private int totalUnitsOfWork;
   private int unitsWorked;
   private String errorLog;
   private boolean jobComplete;
   private boolean success;

   public OTEJobStatus() {
      errorLog = "";
      jobComplete = false;
      success = true;
      totalUnitsOfWork = 0;
      unitsWorked = 0;
   }

   public int getTotalUnitsOfWork() {
      return totalUnitsOfWork;
   }

   public int getUnitsWorked() {
      return unitsWorked;
   }

   public String getErrorLog() {
      return errorLog;
   }

   public boolean isJobComplete() {
      return jobComplete;
   }

   public void setTotalUnitsOfWork(int i) {
      totalUnitsOfWork = i;
   }

   public void jobComplete() {
      jobComplete = true;
   }

   public void incrememtUnitsWorked() {
      unitsWorked++;
   }

   public void setUnitsWorked(int unitsWorked) {
      this.unitsWorked = unitsWorked;
   }

   public void setErrorLog(String log) {
      this.errorLog = log;
   }

   public void setJobComplete(boolean jobComplete) {
      this.jobComplete = jobComplete;
   }

   public boolean isSuccess() {
      return success;
   }

   public void setSuccess(boolean success) {
      this.success = success;
   }

   public URL getUpdatedJobStatus() {
      return updatedJobStatus;
   }

   public void setUpdatedJobStatus(URL updatedJobStatus) {
      this.updatedJobStatus = updatedJobStatus;
   }

   public String getJobId() {
      return jobId;
   }

   public void setJobId(String jobId) {
      this.jobId = jobId;
   }

}
