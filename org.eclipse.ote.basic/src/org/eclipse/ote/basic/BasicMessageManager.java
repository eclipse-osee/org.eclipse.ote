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
package org.eclipse.ote.basic;

import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.ote.message.manager.AbstractMessageManager;
import org.eclipse.ote.message.manager.DataTypeProvider;
import org.eclipse.ote.message.manager.NamespaceMapper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * Mostly needed for binding some services for use in the super and specifying the concrete types for the generic
 * methods.
 *
 * @author Michael P. Masterson
 */
@Component(service = {IMessageManager.class}, immediate = true)
public class BasicMessageManager extends AbstractMessageManager<MessageData, Message> {

   @Override
   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void bindEnv(TestEnvironmentInterface env) {
      super.bindEnv(env);
   }

   @Override
   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void bindNamespaceMapper(NamespaceMapper nsMapper) {
      super.bindNamespaceMapper(nsMapper);
   }

   @Override
   @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
   public void addDataTypeProvider(DataTypeProvider provider) {
      super.addDataTypeProvider(provider);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ote.message.manager.AbstractMessageManager#removeDataTypeProvider(org.eclipse.ote.message.manager.DataTypeProvider)
    */
   @Override
   public void removeDataTypeProvider(DataTypeProvider provider) {
      super.removeDataTypeProvider(provider);
   }

   @Activate
   public void start() {
      super.init();
   }
}
