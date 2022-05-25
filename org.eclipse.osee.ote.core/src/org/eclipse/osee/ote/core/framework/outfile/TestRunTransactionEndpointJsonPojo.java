/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ote.core.framework.outfile;

/**
 * @author Andy Jury
 */
public class TestRunTransactionEndpointJsonPojo {

   private String branch;
   private String txComment;

   private CreateArtifactsJsonPojo createArtifacts;

   public String getBranch() {
      return branch;
   }

   public void setBranch(String branch) {
      this.branch = branch;
   }

   public String getTxComment() {
      return txComment;
   }

   public void setTxComment(String txComment) {
      this.txComment = txComment;
   }

   public CreateArtifactsJsonPojo getCreateArtifacts() {
      return createArtifacts;
   }

   public void setCreateArtifacts(CreateArtifactsJsonPojo createArtifacts) {
      this.createArtifacts = createArtifacts;
   }
}
