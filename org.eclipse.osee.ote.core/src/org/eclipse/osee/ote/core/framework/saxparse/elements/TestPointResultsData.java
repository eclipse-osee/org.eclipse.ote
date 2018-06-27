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

   /**
    * @return the total
    */
   public String getTotal() {
      return total;
   }

   private final String fail;
   private final String pass;
   private final String total;

   public TestPointResultsData(String aborted, String fail, String pass, String total) {
      this.aborted = aborted;
      this.fail = fail;
      this.pass = pass;
      this.total = total;
   }

}
