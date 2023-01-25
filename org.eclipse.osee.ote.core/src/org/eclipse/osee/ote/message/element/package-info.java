/*********************************************************************
 * Copyright (c) 2023 Boeing
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

/**
 * This package currently contains newer message elements that wrap the legacy element types located in org.eclipse.osee.ote.message.elements.
 * These new classes remove the need to separate a reader and writer buffers and instead choose the correct buffer based on the api context.
 * For example, when calling element.set(), the writer is used.  When using element.check(), the reader is used.  
 * <br><br>
 * Eventually, these classes will no longer be wrappers for the legacy classes but replace them completely. 
 * @author Michael P. Masterson
 */
package org.eclipse.osee.ote.message.element;