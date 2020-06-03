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

package org.eclipse.osee.ote.ui.test.manager.core;

import java.io.File;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class TestManagerEditorInputFactory implements IElementFactory {

   public TestManagerEditorInputFactory() {
   }

   @Override
   public IAdaptable createElement(IMemento memento) {
      String path = memento.getString("path");
      if(path != null){
         File file = new File(path);
         TestManagerEditorInput input = new TestManagerEditorInput(file);
         return input;
      } else {
         return null;
      }
   }
}
