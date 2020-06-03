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

package org.eclipse.osee.ote.messaging.dds;

/**
 * The class which stores all of the available view states for a sample. The name of any of the values can be acquired
 * from the <code>getKindName()</code> method inherited from <code>Kind</code>.
 * 
 * @see org.eclipse.osee.ote.messaging.dds.Kind
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class ViewStateKind extends Kind {

   public final static ViewStateKind NEW = new ViewStateKind("New view state", 1);
   public final static ViewStateKind NOT_NEW = new ViewStateKind("Not new view state", 2);

   /**
    * Local constructor for creating <code>ViewStateKind</code> objects.
    * 
    * @param kindName The name of the kind
    * @param kindId The id value of the kind
    */
   private ViewStateKind(String kindName, long kindId) {
      super(kindName, kindId);
   }

}
