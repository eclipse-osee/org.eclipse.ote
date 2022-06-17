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
package org.eclipse.ote.simple.rest.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.ote.simple.rest.server.endpoints.OteSimpleEndpoint;

/**
 * @author Nydia Delgado
 */
public class SimpleApp {
   public static void main(String[] args) throws Exception {
      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
      context.setContextPath("/");

      Server jettyServer = new Server(8080);
      jettyServer.setHandler(context);

      ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
      jerseyServlet.setInitOrder(0);

      // Tells the Jersey Servlet which REST service/class to load.
      jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
            OteSimpleEndpoint.class.getCanonicalName());

      try {
         jettyServer.start();
         jettyServer.join();
      } finally {
         jettyServer.destroy();
      }
   }
}
