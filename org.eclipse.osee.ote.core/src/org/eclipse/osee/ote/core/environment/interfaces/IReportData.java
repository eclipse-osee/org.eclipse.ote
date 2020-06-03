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

package org.eclipse.osee.ote.core.environment.interfaces;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.reportdata.ReportDataListener;

public interface IReportData {
   // void addQueueListener(ReportDataListener listener, List<TestEnvironmentCommand> cmds);
   void addQueueListener(ReportDataListener listener, List cmds);

   void removeQueueListener(ReportDataListener listener);

   // void updateQueueListeners(List<TestEnvironmentCommand> cmds);
   void updateQueueListeners(List cmds);
}
