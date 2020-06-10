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

import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.ote.io.GenericOteIoType;
import org.eclipse.ote.message.manager.NamespaceMapper;

/**
 * This will be the injection point for any Simple IO specific set up with the env
 * 
 * @author Michael P. Masterson
 */
public class StartSimpleIoComponent {
   
   private NamespaceMapper nsMapper;
   private IMessageManager<?> messageManager;

   public void start() {
      System.out.println("Started Simple IO Comp");
      Namespace namespace = nsMapper.getNamespace(SimpleDataType.SIMPLE);
      SimpleIOWriter writer = new SimpleIOWriter(namespace);
      messageManager.getDDSListener().registerWriter(writer);
      
      namespace = nsMapper.getNamespace(GenericOteIoType.MUX);
      SimpleMuxWriter muxWriter = new SimpleMuxWriter(namespace);
      messageManager.getDDSListener().registerWriter(muxWriter);
      
   }
   
   public void setNamespaceMapper(NamespaceMapper mapper) {
      this.nsMapper = mapper;
   }
   
   public void bindMsgManager(IMessageManager<?> messageManager) {
      this.messageManager = messageManager;
   }
}
