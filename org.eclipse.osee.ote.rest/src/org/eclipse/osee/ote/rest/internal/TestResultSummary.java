/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ote.rest.internal;

class TestResultSummary {

   private static final String NA = "N/A";
   private String name;
   private String results;
   private String time;
   
   public TestResultSummary(String name, String results, String time) {
      this.name = name;
      this.results = results;
      this.time = time;
   }

   public TestResultSummary(String name) {
      this.name = name;
      this.results = NA;
      this.time = NA;
   }

   public String getName() {
      return name;
   }

   public String getResult() {
      return results;
   }

   public String getTime() {
      return time;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TestResultSummary other = (TestResultSummary) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

}
