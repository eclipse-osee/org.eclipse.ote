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

package org.eclipse.osee.ote.core.environment;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.osgi.framework.BundleException;

/**
 * @author Robert A. Fisher
 */
public class BundleResolveException extends Exception {
   private static final long serialVersionUID = 5506351677181297953L;
   private final Collection<BundleException> bundleExceptions;

   public BundleResolveException(String message, Collection<BundleException> bundleExceptions) {
      super(message);
      this.bundleExceptions = bundleExceptions;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();

      builder.append(super.toString() + "\n");
      builder.append(Collections.toString("\n", bundleExceptions));

      return builder.toString();
   }
}
