/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.ote.simple.io;

import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.ote.io.GenericOteIoType;
import org.eclipse.ote.io.mux.lookup.MuxDataLookup;
import org.eclipse.ote.message.manager.NamespaceMapper;


/**
 * This will be the injection point for any Simple IO specific set up with the env
 * 
 * @author Michael P. Masterson
 */
public class StartSimpleIoComponent {
   
   private NamespaceMapper nsMapper;
   private IMessageManager<?> messageManager;
   private TestEnvironment env;
   private SimpleMuxReceiver receiver;
   private SimpleIOWriter writer;
   private SimpleMuxWriter muxWriter;

   public void start() {
      System.out.println("Started Simple IO Comp");
      Namespace namespace = nsMapper.getNamespace(SimpleDataType.SIMPLE);
      writer = new SimpleIOWriter(namespace);
      messageManager.getDDSListener().registerWriter(writer);
      
      namespace = nsMapper.getNamespace(GenericOteIoType.MUX);
      muxWriter = new SimpleMuxWriter(namespace);
      messageManager.getDDSListener().registerWriter(muxWriter);
      
      MuxDataLookup muxDataLookup = new MuxDataLookup();
      messageManager.putMessageDataLookup(namespace, muxDataLookup);
      
      receiver = new SimpleMuxReceiver(env, muxDataLookup);
      receiver.startThread();
      
   }
   
   public void stop() {
      System.out.println("Stopping Simple IO Comp");

      if (writer != null) {
         messageManager.getDDSListener().unregisterWriter(writer);
         writer.destroy();
         writer = null;
      }
      
      if (muxWriter != null) {
         messageManager.getDDSListener().unregisterWriter(muxWriter);
         muxWriter.destroy();
         muxWriter = null;
      }
      
      if(receiver != null) {
         receiver.destroy();
         receiver = null;
      }
   }
   
   public void bindEnv(TestEnvironment env) {
      this.env = env;
   }
   
   /**
    * @param env Not used
    */
   public void unbindEnv(TestEnvironment env) {
      this.env = null;
   }
   
   public void setNamespaceMapper(NamespaceMapper mapper) {
      this.nsMapper = mapper;
   }
   
   public void bindMsgManager(IMessageManager<?> messageManager) {
      this.messageManager = messageManager;
   }
}
