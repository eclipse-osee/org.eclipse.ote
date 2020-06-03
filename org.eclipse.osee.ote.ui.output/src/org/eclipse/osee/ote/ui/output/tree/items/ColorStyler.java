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

package org.eclipse.osee.ote.ui.output.tree.items;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextStyle;

/**
 * @author Andrew M. Finkbeiner
 * @author Andy Jury
 */
public class ColorStyler extends Styler {

   private final String symbolicName;

   public ColorStyler(String symbolicName, RGB rgb) {
      this.symbolicName = symbolicName;
      JFaceResources.getColorRegistry().put(symbolicName, rgb);
   }

   @Override
   public void applyStyles(TextStyle textStyle) {
      textStyle.foreground = JFaceResources.getColorRegistry().get(symbolicName);
   }

}