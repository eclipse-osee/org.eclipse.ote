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

package org.eclipse.osee.ote.core.environment.interfaces;

import java.util.List;

/**
 * @author Robert A. Fisher
 */
public interface ITestStation {

   public String getOutletIp();

   public void setOutletIp(String outletIp);

   public int getOutletPort();

   public void setOutletPort(int outletPort);

   public String getVmeConnectionName();

   public void turnPowerSupplyOnOff(boolean turnOn);

   public List<IOTypeHandlerDefinition> getSupportedDriverTypes();

   public boolean isPhysicalTypeAvailable(IOTypeDefinition physicalType);
}
