/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.JavaType;

/**
 * @author Andy Jury
 */
public class TmoTypeResolverBuilder extends DefaultTypeResolverBuilder {

   private static final long serialVersionUID = 7969848188152891576L;

   public TmoTypeResolverBuilder() {
      super(DefaultTyping.NON_FINAL);
   }

   @SuppressWarnings("rawtypes")
   @Override
   public boolean useForType(JavaType t) {
      final Class clazz = t.getRawClass();
      final String name = clazz.getName();
      if (name.startsWith("java.") || !name.contains(".")) {
         return false;
      }
      return true;
   }
}
