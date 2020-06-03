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

package org.eclipse.osee.ote.core.environment;

import java.io.Serializable;
import java.util.List;

public class BundleConfigurationReport implements Serializable {
   private static final long serialVersionUID = 2948282371713776849L;
   private final List<BundleDescription> missing;
   private final List<BundleDescription> versionMismatch;
   private final List<BundleDescription> partOfInstallation;

   public static long getSerialversionuid() {
      return serialVersionUID;
   }

   public List<BundleDescription> getMissing() {
      return missing;
   }

   public List<BundleDescription> getVersionMismatch() {
      return versionMismatch;
   }

   public List<BundleDescription> getPartOfInstallation() {
      return partOfInstallation;
   }

   public BundleConfigurationReport(List<BundleDescription> missing, List<BundleDescription> versionMismatch, List<BundleDescription> partOfInstallation) {
      this.missing = missing;
      this.partOfInstallation = partOfInstallation;
      this.versionMismatch = versionMismatch;
   }
}
