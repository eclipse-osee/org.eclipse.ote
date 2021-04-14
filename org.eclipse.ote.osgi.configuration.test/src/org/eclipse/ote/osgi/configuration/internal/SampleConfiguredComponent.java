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

import java.util.Map;
import org.osgi.service.component.ComponentContext;

/**
 * @author Michael P. Masterson
 */
public class SampleConfiguredComponent {
   
   private Map<String, Object> properties;
   
   public void start(ComponentContext context, Map<String, Object> properties){
      this.properties = properties;
   }
   
   public Map<String, Object> getProperties(){
      return properties;
   }
   
}
