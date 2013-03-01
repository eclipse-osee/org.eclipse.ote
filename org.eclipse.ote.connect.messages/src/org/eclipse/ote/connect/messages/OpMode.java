/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.connect.messages;

import org.eclipse.osee.ote.message.elements.IEnumValue;
import org.eclipse.ote.bytemessage.EnumBase;
import org.eclipse.ote.bytemessage.EnumSetter;

public enum OpMode implements IEnumValue<OpMode>{
   inProgress(1),
   success(2),
   fail(3),
   __undefined(-99999);

   private int value;
   
   private OpMode(int value) {
      this.value = value;
   }
   
   public int getIntValue(){
      return value;
   }

   @Override
   public OpMode getEnum(int value) {
      return base.toEnum(value);
   }

   public static OpMode toEnum(int value) {
      return base.toEnum(value);
   }
   
   public static OpMode[] valuesWithoutUndefined(int value) {
      return base.getValues();
   }
   
   private static EnumBase<OpMode> base;
   static {
      base = new EnumBase<OpMode>(values(), OpMode.class, __undefined, new EnumSetter() {
         @Override
         public void setValue(int value) {
            __undefined.value = value;
         }
      });
   }

}
