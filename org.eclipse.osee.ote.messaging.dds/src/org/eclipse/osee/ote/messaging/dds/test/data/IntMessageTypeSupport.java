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

package org.eclipse.osee.ote.messaging.dds.test.data;

import org.eclipse.osee.ote.messaging.dds.service.Key;
import org.eclipse.osee.ote.messaging.dds.service.TypeSupport;

/**
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class IntMessageTypeSupport extends TypeSupport {

   @Override
   protected int getTypeDataSize() {
      return Integer.SIZE / 8;
   }

   @Override
   protected Key getKey() {
      return null;
   }

   @Override
   protected String getReaderName() {
      return IntMessageReader.class.getCanonicalName();
   }

   @Override
   protected String getWriterName() {
      return IntMessageWriter.class.getCanonicalName();
   }

}
