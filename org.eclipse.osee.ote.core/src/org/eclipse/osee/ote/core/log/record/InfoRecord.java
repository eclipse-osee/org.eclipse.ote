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
package org.eclipse.osee.ote.core.log.record;

import java.util.logging.Level;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;

public class InfoRecord extends TestRecord {

   private static final long serialVersionUID = -3124953320400273382L;

   /**
    * InfoRecord Constructor. Sets up a Warning log message.
    * 
    * @param source The object requesting the logging.
    * @param msg The log message.
    * @param timeStamp <b>True </b> if a timestamp should be recorded, <b>False </b> if not.
    */
   public InfoRecord(ITestEnvironmentAccessor source, String msg, boolean timeStamp) {
      super(source, Level.INFO, msg, timeStamp);
   }

   /**
    * InfoRecord Constructor. Sets up a Warning log message.
    * 
    * @param source The object requesting the logging.
    * @param msg The log message.
    */
   public InfoRecord(ITestEnvironmentAccessor source, String msg) {
      this(source, msg, true);
   }
}