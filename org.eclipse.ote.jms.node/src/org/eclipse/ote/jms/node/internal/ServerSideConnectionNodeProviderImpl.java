/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.jms.node.internal;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.ote.jms.node.JmsConnectionNodeProvider;



/**
 * @author Michael P. Masterson
 */
public final class ServerSideConnectionNodeProviderImpl implements JmsConnectionNodeProvider {
   private IHostTestEnvironment testEnv;
   private MessageService messageService;

   private static JmsConnectionNodeProvider instance;

   public void start() {
      instance = this;
   }

   public void stop() {
   }

   public synchronized void bindHostTestEnvironment(IHostTestEnvironment testEnv) {
      this.testEnv = testEnv;
   }

   public synchronized void unbindHostTestEnvironment(IHostTestEnvironment testEnv) {
      this.testEnv = null;
   }

   public synchronized void setMessageService(MessageService messageService) {
      this.messageService = messageService;
   }

   public synchronized void unsetMessageService(MessageService messageService) {
      this.messageService = null;
   }

   public static JmsConnectionNodeProvider getInstance() {
      return instance;
   }

   @Override
   public synchronized ConnectionNode getConnectionNode() {
      try {
         NodeInfo info = testEnv.getBroker();
         ConnectionNode connection = messageService.get(info);
         return connection;
      } catch (Exception ex) {
         OseeLog.log(this.getClass(), Level.SEVERE, ex);
      }
      return null;
   }
}
