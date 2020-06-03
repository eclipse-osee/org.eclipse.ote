/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.ui.internal;

import java.io.IOException;

import org.eclipse.osee.framework.jdk.core.util.IConsoleInputListener;
import org.eclipse.osee.ote.ui.IOteConsoleService;
import org.eclipse.osee.ote.ui.OteConsoleWrapper;

/**
 * @author Roberto E. Escobar
 */
public class OteConsoleServiceImpl implements IOteConsoleService {

   private final OteConsoleWrapper console = new OteConsoleWrapper("OTE Console");

   private OteConsoleWrapper getConsole() {
      return console;
   }

   @Override
   public void addInputListener(IConsoleInputListener listener) {
      if (listener != null) {
         getConsole().addInputListener(listener);
      }
   }

   @Override
   public void removeInputListener(IConsoleInputListener listener) {
      if (listener != null) {
         getConsole().removeInputListener(listener);
      }
   }

   @Override
   public void write(String value) {
      getConsole().write(value);
   }

   @Override
   public void writeError(String value) {
      getConsole().writeError(value);
   }

   @Override
   public void prompt(String value) throws IOException {
      getConsole().prompt(value);
   }

   @Override
   public void popup() {
      getConsole().popup();
   }

   @Override
   public void write(String value, int type, boolean popup) {
      getConsole().write(value, type, popup);
   }

   public void close() {
      getConsole().shutdown();
   }
 }
