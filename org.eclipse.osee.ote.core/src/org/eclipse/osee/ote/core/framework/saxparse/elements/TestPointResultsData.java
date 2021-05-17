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
public class TestPointResultsData {

   private final String aborted;

   /**
    * @return the aborted
    */
   public String getAborted() {
      return aborted;
   }

   /**
    * @return the fail
    */
   public String getFail() {
      return fail;
   }

   /**
    * @return the pass
    */
   public String getPass() {
      return pass;
   }

   public String getInteractive() {
      return interactive;
   }

   /**
    * @return the total
    */
   public String getTotal() {
      return total;
   }

   private final String fail;
   private final String pass;
   private final String total;
   private final String interactive;

   public TestPointResultsData(String aborted, String fail, String pass, String interactive, String total) {
      this.aborted = aborted;
      this.fail = fail;
      this.pass = pass;
      this.interactive = interactive;
      this.total = total;
   }

}
