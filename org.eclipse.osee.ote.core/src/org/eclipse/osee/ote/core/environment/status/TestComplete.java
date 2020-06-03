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

package org.eclipse.osee.ote.core.environment.status;

import java.io.Serializable;
import java.util.List;
import org.eclipse.osee.framework.logging.IHealthStatus;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestComplete implements Serializable, IServiceStatusData {
   private static final long serialVersionUID = -7969548718769021626L;
   private final String className;
   private final CommandEndedStatusEnum status;
   private final String serverOutfilePath;
   private final String clientOutfilePath;
   private final List<IHealthStatus> healthStatus;

   public TestComplete(String className, String serverOutfilePath, String clientOutfilePath, CommandEndedStatusEnum status, List<IHealthStatus> healthStatus) {
      this.className = className;
      this.serverOutfilePath = serverOutfilePath;
      this.clientOutfilePath = clientOutfilePath;
      this.status = status;
      this.healthStatus = healthStatus;
   }

   @Override
   public void accept(IServiceStatusDataVisitor visitor) {
      visitor.asTestComplete(this);
   }

   public String getClassName() {
      return className;
   }

   public CommandEndedStatusEnum getStatus() {
      return status;
   }

   public String getServerOutfilePath() {
      return serverOutfilePath;
   }

   public String getClientOutfilePath() {
      return clientOutfilePath;
   }

   public List<IHealthStatus> getHealthStatus() {
      return this.healthStatus;
   }

}
