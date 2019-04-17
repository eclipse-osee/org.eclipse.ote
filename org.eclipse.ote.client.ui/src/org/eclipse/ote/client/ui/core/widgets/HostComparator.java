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
package org.eclipse.ote.client.ui.core.widgets;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.ote.service.OteServiceProperties;
import org.eclipse.ote.client.ui.core.TestHostItem;

/**
 * @author Andrew M. Finkbeiner
 */
public class HostComparator extends ViewerComparator {

   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      OteServiceProperties host1 = ((TestHostItem) e1).getProperties();
      OteServiceProperties host2 = ((TestHostItem) e2).getProperties();
      int result = host1.getType().compareTo(host2.getType());
      if (result == 0) {
         result = host1.getStation().compareTo(host2.getStation());
         if (result == 0) {
            result = host1.getDateStarted().compareTo(host2.getDateStarted());
         }
      }
      return result;
   }

}
