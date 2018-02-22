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
package org.eclipse.osee.ote.service;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;

/**
 * @author Ken J. Aguilar
 */
public interface IEnvironmentConfigurer {
   boolean configure(IHostTestEnvironment event, SubProgressMonitor subProgressMonitor) throws Exception;
}
