/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.ote.rest.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew M. Finkbeiner
 */
@XmlRootElement
public class OTETestRun {

   private OTEConfiguration jarConfiguration;
   private Properties globalProperties;
   private final List<Properties> tests;

   public OTETestRun() {
      tests = new ArrayList<>();
      globalProperties = new Properties();
   }

   public Properties getGlobalProperties() {
      return globalProperties;
   }

   @XmlElementWrapper
   @XmlElement(name = "Properties")
   public List<Properties> getTests() {
      return tests;
   }

   public void setGlobalProperties(Properties globalProperties) {
      this.globalProperties = globalProperties;
   }

   public void addTest(Properties test) {
      tests.add(test);
   }

   public OTEConfiguration getJarConfiguration() {
      return jarConfiguration;
   }

   public void setJarConfiguration(OTEConfiguration jarConfiguration) {
      this.jarConfiguration = jarConfiguration;
   }

}
