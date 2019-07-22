/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.ote.simple.test.environment.outfile;

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
