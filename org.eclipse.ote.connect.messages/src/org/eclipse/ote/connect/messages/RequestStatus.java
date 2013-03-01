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

public enum RequestStatus implements IEnumValue<RequestStatus> {
	
   yes(1),   no(2),

   __undefined(-99999);

   private int value;
   
   private RequestStatus(int value) {
      this.value = value;
   }
   
   public int getIntValue(){
      return value;
   }

   @Override
   public RequestStatus getEnum(int value) {
      return base.toEnum(value);
   }

   public static RequestStatus toEnum(int value) {
      return base.toEnum(value);
   }
   
   public static RequestStatus[] valuesWithoutUndefined(int value) {
      return base.getValues();
   }
   
   private static EnumBase<RequestStatus> base;
   static {
      base = new EnumBase<RequestStatus>(values(), RequestStatus.class, __undefined, new EnumSetter() {
         @Override
         public void setValue(int value) {
            __undefined.value = value;
         }
      });
   }
}