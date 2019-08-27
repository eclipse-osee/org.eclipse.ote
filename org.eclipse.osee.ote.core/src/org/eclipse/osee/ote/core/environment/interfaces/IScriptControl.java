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
package org.eclipse.osee.ote.core.environment.interfaces;

public interface IScriptControl {

   boolean isExecutionUnitPaused();

   boolean isScriptPaused();

   boolean isScriptReady();

   boolean isLocked();

   void lock();

   void setExecutionUnitPause(boolean pause);

   void setScriptPause(boolean pause);

   void setScriptReady(boolean ready);

   boolean shouldStep();

   void unlock();

   boolean hasLock();

   boolean isHeldByCurrentThread();
}