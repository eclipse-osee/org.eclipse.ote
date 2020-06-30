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

package org.eclipse.osee.ote.message.elements;

/**
 * @author Andy Jury
 */
public interface IElementVisitor {

   void asGenericElement(Element element);

   void asBooleanElement(BooleanElement element);

   void asIntegerElement(IntegerElement element);

   void asRealElement(RealElement element);

   void asCharElement(CharElement element);

   void asEnumeratedElement(EnumeratedElement<?> element);

   void asFixedPointElement(FixedPointElement element);

   void asRecordElement(RecordElement element);

   void asFloat32(Float32Element element);

   void asFloat64(Float64Element element);

   void asStringElement(StringElement element);

   void asRecordMap(RecordMap<? extends RecordElement> element);

   void asEmptyEnumElement(EmptyEnum_Element element);

   void asLongIntegerElement(LongIntegerElement element);

   void asSignedInteger16Element(SignedInteger16Element element);

   void asArrayElement(ArrayElement element);

   void asUnsignedIntegerElement(UnsignedIntegerElement unsignedIntegerElement);
}
