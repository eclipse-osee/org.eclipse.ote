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
package org.eclipse.osee.ote.message.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;

public class MyObjectInputStream extends ObjectInputStream {

   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
      try {
         return ExportClassLoader.getInstance().loadClass(desc.getName());
      } catch (Exception e) {
      }
      return super.resolveClass(desc);
   }

   public MyObjectInputStream(InputStream in) throws IOException {
      super(in);
   }

}