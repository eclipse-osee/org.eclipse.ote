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
package org.eclipse.osee.ote.remote.terminal;

/**
 * @author Nydia Delgado
 */
public interface OteRemoteTerminal {

   public OteRemoteTerminalResponse open() throws Exception;

   public OteRemoteTerminalResponse close() throws Exception;

   /**
    * Issues command to open remote terminal session
    * 
    * @param command
    * @return {@link OteRemoteTerminalResponse} if no exceptions while sending
    *         command to remote terminal session, otherwise an
    *         {@link OteRemoteTerminalResponseException} that fails all
    *         verifications
    */
   public OteRemoteTerminalResponse command(String command);

   public String getHostName();

   public boolean isConnected();
}
