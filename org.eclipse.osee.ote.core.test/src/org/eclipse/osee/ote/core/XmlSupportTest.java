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
package org.eclipse.osee.ote.core;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Michael P. Masterson
 */
public class XmlSupportTest {

   /**
    * Test method for {@link org.eclipse.osee.ote.core.XmlSupport#convertNonPrintableCharacers(java.lang.String)}.
    */
   @Test
   public void testConvertXmlCharacers() {
      String testString = "abc\n\r\0\t 123\f&<>";
      String expected = "abc\n\r[ASCII=0]\t 123[ASCII=12][ampersand][less-than][greater-than]";
      String actual = XmlSupport.convertXmlCharacters(testString);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testNonPrintableCharacter() {
      String testString = "abc\n\r\0\t 123\f&<>";
      String expected = "abc\n\r[ASCII=0]\t 123[ASCII=12]&<>";

      String actual = XmlSupport.convertNonPrintableCharacers(testString);
      Assert.assertEquals(expected, actual);
   }

}
