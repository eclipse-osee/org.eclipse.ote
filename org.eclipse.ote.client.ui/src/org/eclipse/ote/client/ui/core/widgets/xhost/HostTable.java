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
package org.eclipse.ote.client.ui.core.widgets.xhost;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class HostTable extends XViewer {

   public HostTable(Composite parent, int style) {
      super(parent, style, new HostTableTestFactory());
   }
   
   public void setFilter(String filter){
      getCustomizeMgr().setFilterText(filter, false);      
   }

}
