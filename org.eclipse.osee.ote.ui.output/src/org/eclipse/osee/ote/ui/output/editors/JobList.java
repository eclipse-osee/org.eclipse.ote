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

import java.util.Arrays;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class JobList extends Job {

   private final OteJob[] jobs;

   public JobList(OteJob... jobs) {
      super("Job List " + Arrays.deepToString(jobs));
      this.jobs = jobs;
   }

   @Override
   protected IStatus run(IProgressMonitor monitor) {

      for (OteJob job : jobs) {
         this.setName(job.toString());
         job.run(monitor);
      }
      return Status.OK_STATUS;
   }

}
