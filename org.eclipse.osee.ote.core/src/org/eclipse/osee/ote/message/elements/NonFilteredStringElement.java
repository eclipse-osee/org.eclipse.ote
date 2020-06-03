/*********************************************************************
 * Copyright (c) 2011 Boeing
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


public class NonFilteredStringElement extends StringElement{

	public NonFilteredStringElement(StringElement element) {
		super(element.getMessage(), element.getName(), element.getMsgData(), element.getByteOffset(), element.getMsb(), element.getLsb());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.message.elements.StringElement#getValue()
	 */
	@Override
	public String getValue() {
		return getMsgData().getMem().getUnfilteredASCIIString(byteOffset, msb, lsb);
	}

}
