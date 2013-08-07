/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.message.view;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class MessageInfoView extends ViewPart {
   public static final String VIEW_ID = "org.eclipse.ote.ui.message.view.MessageInfoView";
   public static final String PLUGIN_ID = "org.eclipse.ote.ui.message.view";

   public MessageInfoView() {
      super();
   }

   @Override
   public void createPartControl(Composite parent) {
      GridLayout layout = GridLayoutFactory.swtDefaults().numColumns(2).create();

   }

   @Override
   public void setFocus() {
   }


}