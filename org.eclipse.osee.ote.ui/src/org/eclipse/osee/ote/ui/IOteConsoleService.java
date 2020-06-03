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

package org.eclipse.osee.ote.ui;

import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.util.IConsoleInputListener;

/**
 * @author Roberto E. Escobar
 */
public interface IOteConsoleService {

   void addInputListener(IConsoleInputListener listener);

   void removeInputListener(IConsoleInputListener listener);

   void write(String value);

   void write(String value, int type, boolean popup);

   void writeError(String string);

   void prompt(String str) throws IOException;

   void popup();
}
