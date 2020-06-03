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

package org.eclipse.osee.ote.core;

import java.util.logging.Level;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteLevel extends Level {

   private static final long serialVersionUID = -1545385440588581634L;

   public static final Level TEST_EVENT = new OteLevel("TEST_EVENT", SEVERE.intValue() + 50);
   public static final Level TEST_SEVERE = new OteLevel("TEST_SEVERE", SEVERE.intValue() + 100);
   public static final Level ENV_SEVERE = new OteLevel("ENV_SEVERE", SEVERE.intValue() + 200);

   protected OteLevel(String name, int value) {
      super(name, value);
   }

}
