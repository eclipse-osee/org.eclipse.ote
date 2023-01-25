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
package org.eclipse.osee.ote.master.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.ote.master.OTELookup;

@ApplicationPath("otemaster")
public class OTERestApplication extends Application {

   private static OTELookup oteLookup;

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> classes = new HashSet<>();
      classes.add(OTEAvailableServersResource.class);
      return classes;
   }

   public void start() {
   }

   public void stop() {
   }

   public void bindOTELookup(OTELookup oteLookupSrv) {
      OTERestApplication.oteLookup = oteLookupSrv;
   }

   public void unbindOTELookup(OTELookup oteLookupSrv) {
      OTERestApplication.oteLookup = null;
   }

   static OTELookup getOTELookup() {
      return oteLookup;
   }

}
