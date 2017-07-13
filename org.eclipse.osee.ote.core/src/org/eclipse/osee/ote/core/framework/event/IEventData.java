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
package org.eclipse.osee.ote.core.framework.event;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;

public interface IEventData {
   public TestScript getTest();

   public TestCase getTestCase();

   public IPropertyStore getProperties();
}
