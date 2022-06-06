/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.ote.simple.io.rest;

import java.io.InputStream;

import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.ote.rest.OteRestConfigurationProvider;
import org.eclipse.osee.ote.rest.OteRestConfigurationProviderImpl;
import org.osgi.service.component.annotations.Component;

/**
 * @author Michael P. Masterson
 */
@Component(service = {OteRestConfigurationProvider.class}, immediate = true)
public class SimpleOteRestConfig extends OteRestConfigurationProviderImpl {
   private static final String CONFIG_FILE = "SimpleRestProperties.txt";

   @Override
   protected InputStream loadStream() {
      InputStream stream = OseeInf.getResourceAsStream(CONFIG_FILE, this.getClass());
      return stream;
   }

}
