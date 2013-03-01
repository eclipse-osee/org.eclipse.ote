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
package org.eclipse.ote.ui.eviewer.view;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ken J. Aguilar
 */
public final class ElementUpdate {
	private final Object[] values;
	private final BitSet deltaSet;
	private final HashMap<ElementColumn, Integer> valueMap;

	ElementUpdate(HashMap<ElementColumn, Integer> valueMap, List<ElementColumn> allColumns) {
		this.valueMap = valueMap;
		int size = allColumns.size();
		values = new Object[size];
		this.deltaSet = new BitSet(size);
		for (int i = 0; i < size; i++) {
			ElementColumn col = allColumns.get(i);
			values[i] = col.getValue();  
			deltaSet.set(i);
		}
	}



	protected ElementUpdate(Object[] values, BitSet deltaSet,
			HashMap<ElementColumn, Integer> valueMap) {
		super();
		this.values = values;
		this.deltaSet = deltaSet;
		this.valueMap = valueMap;
	}



	public ElementUpdate next(HashMap<ElementColumn, Integer> valueMap, List<ElementColumn> allColumns) {
		int size = allColumns.size();
		// if the value map is not equal then a change in the columns (ordering, adding, etc) has occurred
		if (valueMap == this.valueMap) {
			// no column changes so we can clone and then find deltas
			Object[] newValues = this.values.clone();
			BitSet newDeltaSet = new BitSet(size);
			for (int i = 0; i < size; i++) {
				ElementColumn col = allColumns.get(i);
				if (col.getAndClearUpdateState()) {
					Object value = col.getValue();
					newValues[i] = value; 
					// even though a update flag is set, the value may have
					// reverted to the same value of the last visual update
					// which can happen in inactive columns
					newDeltaSet.set(i, !value.equals(values[i]));
				}
				
			}
			return new ElementUpdate(newValues, newDeltaSet, valueMap);
		} else {
			Object[] newValues = new Object[size];
			BitSet newDeltaSet = new BitSet(size);
			for (int i = 0; i < size; i++) {
				ElementColumn col = allColumns.get(i);
				Object value = col.getValue();
				newValues[i] = value;      
				if (!value.equals(getValue(col))) {
					newDeltaSet.set(i);
				}
			}
			return new ElementUpdate(newValues, newDeltaSet, valueMap);
		}
	}

	public Object getValue(ElementColumn column) {
		Integer index = valueMap.get(column);
		return index != null ? values[index] : null;
	}

	public boolean isChanged(ElementColumn column) {
		Integer index = valueMap.get(column);
		return index != null ? deltaSet.get(index) : false;
	}

}
