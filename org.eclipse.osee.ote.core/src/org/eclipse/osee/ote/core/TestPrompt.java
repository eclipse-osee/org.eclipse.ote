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

package org.eclipse.osee.ote.core;

import java.io.Serializable;
import org.eclipse.osee.ote.core.enums.PromptResponseType;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class TestPrompt implements Serializable {

   private static final long serialVersionUID = 5960067878239875110L;
   private final String prompt;
   private final boolean waitForResponse;
   private final boolean uutStep;
   private final PromptResponseType type;

   public TestPrompt(String prompt) {
      this(prompt, PromptResponseType.NONE);
   }

   public TestPrompt(String prompt, PromptResponseType type) {
      super();
      this.prompt = XmlSupport.convertNonPrintableCharacers(prompt);
      this.waitForResponse =
         type == PromptResponseType.SCRIPT_PAUSE || type == PromptResponseType.PASS_FAIL || type == PromptResponseType.SCRIPT_STEP || type == PromptResponseType.USER_INPUT || type == PromptResponseType.YES_NO ? true : false;
      this.uutStep = type == PromptResponseType.SCRIPT_STEP ? true : false;
      this.type = type;
   }

   public PromptResponseType getType() {
      return type;
   }

   public boolean isWaiting() {
      return this.waitForResponse;
   }

   @Override
   public String toString() {
      return this.prompt;
   }

   public boolean isUutStep() {
      return uutStep;
   }
}