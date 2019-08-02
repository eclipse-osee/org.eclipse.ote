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
