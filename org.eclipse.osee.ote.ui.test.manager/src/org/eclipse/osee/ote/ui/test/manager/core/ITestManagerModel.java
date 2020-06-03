/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ote.ui.test.manager.core;

import org.eclipse.osee.framework.jdk.core.type.Pair;

public interface ITestManagerModel {

   public abstract boolean hasParseExceptions();

   public abstract Pair<Integer, Integer> getParseErrorRange();

   public abstract String getParseError();

   public abstract boolean setFromXml(String xmlText);

   public abstract String getRawXml();

}