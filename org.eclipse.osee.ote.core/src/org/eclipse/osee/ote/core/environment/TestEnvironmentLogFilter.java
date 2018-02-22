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
