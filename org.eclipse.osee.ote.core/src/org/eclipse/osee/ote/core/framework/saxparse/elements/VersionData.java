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

package org.eclipse.osee.ote.core.framework.saxparse.elements;

/**
 * @author Andrew M. Finkbeiner
 */
public class VersionData {

   private final String name;
   private final String underTest;
   private final String version;
   private final String versionUnit;

   VersionData(String name, String underTest, String version, String versionUnit) {
      this.name = name;
      this.underTest = underTest;
      this.version = version;
      this.versionUnit = versionUnit;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the underTest
    */
   public String getUnderTest() {
      return underTest;
   }

   /**
    * @return the version
    */
   public String getVersion() {
      return version;
   }

   /**
    * @return the versionUnit
    */
   public String getVersionUnit() {
      return versionUnit;
   }

}