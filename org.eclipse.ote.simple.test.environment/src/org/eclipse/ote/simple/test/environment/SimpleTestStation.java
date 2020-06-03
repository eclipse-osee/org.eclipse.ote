/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.ote.simple.test.environment;

import java.util.List;

import org.eclipse.osee.ote.core.environment.interfaces.IOTypeDefinition;
import org.eclipse.osee.ote.core.environment.interfaces.IOTypeHandlerDefinition;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;

/**
 * @author Andy Jury
 */
public class SimpleTestStation implements ITestStation {

   private String outletIp;
   private int outletPort;
   protected String vmeConnectionName;

   public SimpleTestStation() {
      this.vmeConnectionName = "NA";
   }

   @Override
   public List<IOTypeHandlerDefinition> getSupportedDriverTypes() {
      return null;
   }

   @Override
   public boolean isPhysicalTypeAvailable(IOTypeDefinition type) {
      return false;
   }

   @Override
   public String getOutletIp() {
      return outletIp;
   }

   @Override
   public void setOutletIp(String outletIp) {
      this.outletIp = outletIp;
   }

   @Override
   public int getOutletPort() {
      return outletPort;
   }

   @Override
   public void setOutletPort(int outletPort) {
      this.outletPort = outletPort;
   }

   @Override
   public String getVmeConnectionName() {
      return vmeConnectionName;
   }

   @Override
   public void turnPowerSupplyOnOff(boolean turnOn) {
      // Intentionally empty block
   }
}
