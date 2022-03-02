/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.io.pubsub.service;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.InitializeEnvironment;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.MessageDataLookup;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.ote.io.GenericOteIoType;
import org.eclipse.ote.io.pubsub.config.PubSubEthernetConfigurationProvider;
import org.eclipse.ote.message.manager.NamespaceMapper;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * @author Michael P. Masterson
 */
@Component(service = {StartComponent.class}, immediate = true)
public class StartComponent {

   private IMessageManager<?> messageManager;
   private TestEnvironment env;

   private PubSubReceive pubsubReceiver;
   private PubSubSend pubsubSender;
   private NamespaceMapper nsMapper;
   private MessageDataLookup lookup;
   private PubSubEthernetConfigurationProvider config;
   private ServiceRegistration<PubSubReceive> serviceReg;

   @Activate
   protected void start() {
      try{
         Namespace namespace = nsMapper.getNamespace(GenericOteIoType.PUB_SUB);
         PubSubDataLookup lookup = new PubSubDataLookup();
         pubsubReceiver = new PubSubReceive(namespace, messageManager.getPublisher(), lookup, config, env);
         pubsubSender = new PubSubSend(namespace, config);
         messageManager.getDDSListener().registerWriter(pubsubSender);
         messageManager.putMessageDataLookup(namespace, lookup);

         pubsubReceiver.startThread();
         serviceReg = FrameworkUtil.getBundle(getClass()).getBundleContext().registerService(PubSubReceive.class,
            pubsubReceiver, null);

      } catch (Throwable th) {
         OseeLog.log(getClass(), Level.SEVERE, th);
      }
   }

   @Deactivate
   public void stop(){
      if (serviceReg != null) {
         serviceReg.unregister();
         serviceReg = null;
      }
      if (pubsubReceiver != null) {
         pubsubReceiver.destroy();
         pubsubReceiver = null;
      }
      if (pubsubSender != null) {
         messageManager.getDDSListener().unregisterWriter(pubsubSender);
         pubsubSender.destroy();
         pubsubSender = null;
      }
   }

   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void bindPubSubConfigurationService(PubSubEthernetConfigurationProvider config) {
      this.config = config;
   }

   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void bindMessageManager(IMessageManager<?> messageManager) {
      this.messageManager = messageManager;
   }

   public void unbindMessageManager(IMessageManager<?> messageManager) {
      this.messageManager = null;
   }

   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void bindTestEnvironmentInterface(TestEnvironment env) {
      this.env = env;
   }

   public void unbindTestEnvironmentInterface(TestEnvironment env) {
      this.env = null;
   }

   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
   public void bindNamespaceMapper(NamespaceMapper nsMapper) {
      this.nsMapper = nsMapper;
   }

   public void unbindNamespaceMapper(NamespaceMapper nsMapper) {
      this.nsMapper = null;
   }

   @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC, target = "(org.eclipse.ote.io.pubsub=true)")
   public void bindInitializeEnvironment(InitializeEnvironment init) {
      // Do nothing
   }

}
