/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.ote.osgi.configuration.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.eclipse.ote.osgi.configuration.OTEConfigurationProvider;
import org.eclipse.ote.services.core.BundleUtility;

/**
 * @author Michael P. Masterson
 */
public class SampleConfigProvider implements OTEConfigurationProvider {

   @Override
   public InputStream[] getPropertyFiles() {
      URL url = BundleUtility.findEntry("org.eclipse.ote.osgi.configuration.test", "data/sample.properties");
      InputStream stream = null;
      try{
         stream = url.openStream();
      } catch (IOException ex){
         ex.printStackTrace();
      }
      InputStream[] streams = new InputStream[1];
      streams[0]= stream;
      return streams;
   }
}
