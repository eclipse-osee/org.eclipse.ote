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

import java.util.logging.Level;
import java.util.regex.Pattern;

import org.eclipse.osee.framework.logging.ILoggerFilter;
import org.eclipse.osee.ote.properties.OtePropertiesCore;

public class TestEnvironmentLogFilter implements ILoggerFilter {
   private static Level level;
   static {
      level = Level.parse(System.getProperty("ote.testenv.filter.level", "INFO"));
   }

   @Override
   public Pattern bundleId() {
      return null;
   }

   @Override
   public Level getLoggerLevel() {
      return level;
   }

   @Override
   public Pattern name() {
      return null;
   }

}
