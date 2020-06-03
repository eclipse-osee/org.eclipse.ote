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

package org.eclipse.osee.ote.ui.output.tree.items;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public enum OutfileRowType {
   unknown,
   additionalinfo,
   location,
   testpoint,
   stacktrace,
   attention,
   checkgroup,
   checkpoint,
   infogroup,
   info,
   log,
   retrygroup,
   scriptinit,
   softkey,
   Support,
   testcase,
   method,
   executionstatus,
   details,
   uutinfo,
   summary,
   version,
   versionentry
}
