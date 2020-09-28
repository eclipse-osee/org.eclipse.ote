/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.ote.verify;

import org.eclipse.osee.framework.jdk.core.type.BaseId;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Michael P. Masterson
 */
public class OteVerifierAttribute implements Named {

   // TODO: Add use of these to all constructor calls
   public static final boolean REQUIRED = true;
   public static final boolean OPTIONAL = false;

   private final String name;
   private Object value;
   private final boolean isRequired;

   /**
    * @param name the name used for logging results
    * @param isRequired True if this attribute will always be checked. If true {@link #setValue(Object)} must be called
    * before calling {@link #matches(OteVerifierAttribute)}
    */
   public OteVerifierAttribute(String name, boolean isRequired) {
      this.name = name;
      this.value = BaseId.SENTINEL;
      this.isRequired = isRequired;
   }

   /**
    * Compares this object to the 'actual' argument.
    * 
    * @param actual The attribute class to compare to this object
    * @return PASSED if the object is required/used and the objects match.<br>
    * FAILED if the object is required/used and the objects do not match.<br>
    * NOT_USED if the attributes are optional and never used.
    * @throws OseeCoreException If the attribute is required but the expected or actual value was never set. Also thrown
    * if the value is optional and the expected value was set but the actual value was never set
    */
   public OteMatchResult matches(OteVerifierAttribute actual) {
      if (this.isRequired) {
         if (this.value.equals(BaseId.SENTINEL)) {
            throw new OseeCoreException("Required attribute '%s' was never set on expected OTE verifier attribute",
               this.name);
         }

         if (actual.value.equals(BaseId.SENTINEL)) {
            throw new OseeCoreException("Required attribute '%s' was never set on actual OTE verifier attribute",
               actual.name);
         }

         return this.value.equals(actual.value) ? OteMatchResult.PASSED : OteMatchResult.FAILED;
      } else {
         if (this.value.equals(BaseId.SENTINEL)) {
            return OteMatchResult.NOT_USED;
         } else if (actual.value.equals(BaseId.SENTINEL)) {
            throw new OseeCoreException("Optional attribute '%s' was set on expected but not on actual", actual.name);
         } else {
            return this.value.equals(actual.value) ? OteMatchResult.PASSED : OteMatchResult.FAILED;
         }
      }
   }

   /**
    * @return the name
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * @return the value
    */
   public Object getValue() {
      return value;
   }

   /**
    * @param value the value to set
    */
   public void setValue(Object value) {
      this.value = value;
   }

   @Override
   public String toString() {
      return value == null ? "NULL" : value.toString();
   }

}
