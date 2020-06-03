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

package org.eclipse.osee.ote.core.environment.interfaces;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.RetryGroup;

/**
 * The TestPoint interface should be implemented by objects that store pass/fail
 * data.
 * 
 * @author Robert A. Fisher
 */
@JsonSubTypes({ @JsonSubTypes.Type(value = CheckGroup.class), //
		@JsonSubTypes.Type(value = RetryGroup.class) })
public interface ITestGroup extends ITestPoint {
	public int size();
	
	@JsonProperty
	public ArrayList<ITestPoint> getTestPoints();
}
