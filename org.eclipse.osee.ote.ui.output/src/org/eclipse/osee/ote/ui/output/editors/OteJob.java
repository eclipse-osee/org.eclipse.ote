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

package org.eclipse.osee.ote.ui.output.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public abstract class OteJob {

   private final String name;

   public OteJob(String name) {
      this.name = name;
   }

   abstract IStatus run(IProgressMonitor monitor);

   @Override
   public String toString() {
      return name;
   }
}
