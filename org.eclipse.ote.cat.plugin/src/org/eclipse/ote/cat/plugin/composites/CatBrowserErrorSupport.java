/*********************************************************************
 * Copyright (c) 2024 Boeing
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.ote.cat.plugin.composites;

import java.net.URL;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * A {@link Composite} extension containing a browser used to display user support information about an exception.
 * 
 * @author Loren K. Ashley
 */

public class CatBrowserErrorSupport extends Composite {

   /**
    * Creates a new {@link Composite} containing a browser.
    * 
    * @param parent the {@link Composite} the browser is attached to.
    * @param url the initial URL to be displayed in the browser.
    */

   public CatBrowserErrorSupport(Composite parent, URL url) {
      super(parent, SWT.NONE);
      this.browser = new Browser(parent, SWT.NONE);
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      browser.setLayoutData(gridData);
      browser.setUrl(url.toExternalForm());

      browser.addDisposeListener(this::dispose);
   }

   /**
    * Saves the {@link Control} for the browser.
    */

   private Browser browser;

   /**
    * Releases operating system resources for the browser.
    * 
    * @param disposeEvent unused
    */

   private void dispose(DisposeEvent disposeEvent) {
      this.browser.dispose();
      this.browser = null;
   }

}
