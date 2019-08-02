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
package org.eclipse.osee.ote.ui.output.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class TagRule extends MultiLineRule {

   public TagRule(IToken token) {
      super("<", ">", token);
   }

   @Override
   protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
      int c = scanner.read();
      if (sequence[0] == '<') {
         if (c == '?') {
            // processing instruction - abort
            scanner.unread();
            return false;
         }
         if (c == '!') {
            scanner.unread();
            // comment - abort
            return false;
         }
      } else if (sequence[0] == '>') {
         scanner.unread();
      }
      return super.sequenceDetected(scanner, sequence, eofAllowed);
   }
}
