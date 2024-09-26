/*******************************************************************************
 * Copyright (c) 2024 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.ote.cat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A class annotation to indicate to the Compiler Applicability Tool (CAT) that the annotated class is to be processed
 * for <i>applicabilityStatements</i> and a version of the annotated class is to be created for each Product
 * Configuration in the selected Product Line Engineering (PLE) Configuration.
 * 
 * @author Loren K. Ashley
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PLE {
}
