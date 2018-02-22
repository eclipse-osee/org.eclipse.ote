/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.define.AUTOGEN;

import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;

public final class OteRelationTypes {

   public static final IRelationTypeSide TEST_SCRIPT_TO_RUN_RELATION__TEST_SCRIPT =
      TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, 0x2000000000000176L, "Test Case to Run Relation");
   public static final IRelationTypeSide TEST_SCRIPT_TO_RUN_RELATION__TEST_RUN = TokenFactory.createRelationTypeSide(
      RelationSide.SIDE_B, 0x2000000000000176L, "Test Case to Run Relation");

   private OteRelationTypes() {
      // Constants
   }

}