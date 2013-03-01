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
package org.eclipse.ote.bytemessage;

import java.lang.reflect.Array;

import org.eclipse.osee.ote.message.elements.IEnumValue;

/**
 * This is a utility class for all use with Enums that implement IEnumValue which are used by EnumeratedElement.
 * It requires the last enum to be the __undefined enum.
 * 
 * @author Andrew M. Finkbeiner
 *
 * @param <T>
 */
public class EnumBase<T extends IEnumValue<T>> {

   private int maxValue = 0;
   
   private T[] values;
   private final T[] lookup;
   private final T undefined;

   private EnumSetter undefinedSetter;
   
   @SuppressWarnings({"unchecked", "rawtypes" })
   public EnumBase(T[] enumvalues, Class<T> clazz, T undefined, EnumSetter undefinedSetter) {
      T lastElement = enumvalues[enumvalues.length-1];
      if(lastElement instanceof Enum){
         if(!((Enum)lastElement).name().equalsIgnoreCase("__undefined")){
            throw new IllegalStateException("Last enum item must be named [ __undefined | __UNDEFINED]");
         }
      } else {
         throw new IllegalStateException("EnumBase requires array of type Enum");
      }
      this.values = (T[])Array.newInstance(clazz, enumvalues.length - 1);;
      this.undefinedSetter = undefinedSetter;
      this.undefined = undefined;
      for(int i = 0; i < enumvalues.length - 1; i++){
         if(enumvalues[i].getIntValue() > maxValue){
            maxValue = enumvalues[i].getIntValue();
         }
         values[i] = enumvalues[i];
      }
      lookup = (T[])Array.newInstance(clazz, maxValue + 1);
      for(int i = 0; i < values.length; i++){
         lookup[values[i].getIntValue()] = values[i];
      }
   }
   
   public T toEnum(int value){
      if(lookup == null){
         for(T myEnum: getValues()){
            if(myEnum.getIntValue() == value){
               return myEnum;
            }
         }
         undefinedSetter.setValue(value);
         return undefined;
      } else if(value < 0 || value >= lookup.length ){
         undefinedSetter.setValue(value);
         return undefined;
      } else {
         T enumeration = lookup[value];
         if(enumeration == null){
            undefinedSetter.setValue(value);
            return undefined;
         } else {
            return enumeration;
         }
      }  
   }
   
   public T[] getValues(){
      return values;
   }
}
