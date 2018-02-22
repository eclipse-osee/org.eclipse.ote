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
package org.eclipse.osee.ote.define.parser.handlers;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.ote.define.TestRunField;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class DemoInfoHandler extends AbstractParseHandler {

   @Override
   protected void processSaxChunk(Element element) {
      String formalityLevel = element.getAttribute("level");
      String buildId = element.getAttribute("buildId");

      if (Strings.isValid(formalityLevel) != true) {
         formalityLevel = "DEVELOPMENT";
      }
      notifyOnDataEvent(TestRunField.QUALIFICATION_LEVEL.toString(), formalityLevel.toUpperCase());

      if (Strings.isValid(buildId) != true) {
         buildId = "unknown";
      }
      notifyOnDataEvent(TestRunField.BUILD_ID.toString(), buildId);
   }
}
