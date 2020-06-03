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

package org.eclipse.osee.ote.ui.markers;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ote.core.framework.saxparse.elements.StacktraceData;

/**
 * @author Andrew M. Finkbeiner
 */
public class StackTraceCollection {

   public List<StacktraceData> data = new ArrayList<>();

   public void addTrace(StacktraceData obj) {
      data.add(obj);
   }

   public List<StacktraceData> getStackTrace() {
      return data;
   }

}
