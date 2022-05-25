/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ote.core.framework.outfile;

import static org.junit.Assert.assertEquals;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

/**
 * @author Andy Jury
 */
public class AttributeJsonPojoTest {

   @Test
   public void testAttributeJsonPojoTest() throws Exception {

      String path = "data/attribute.json";
      String jsonFileString = new String(Files.readAllBytes(Paths.get(path)));

      AttributeJsonPojo attribute = new AttributeJsonPojo();
      attribute.setTypeName("Outfile URL");
      attribute.setValue("TBD");

      ObjectMapper objectMapper = new ObjectMapper();
      String inputJson = objectMapper.writeValueAsString(attribute);

      assertEquals(jsonFileString, inputJson);
   }
}
