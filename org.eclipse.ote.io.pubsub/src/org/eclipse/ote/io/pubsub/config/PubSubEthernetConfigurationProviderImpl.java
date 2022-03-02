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
package org.eclipse.ote.io.pubsub.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Michael P. Masterson
 */
public abstract class PubSubEthernetConfigurationProviderImpl implements PubSubEthernetConfigurationProvider {

   private static final String CONFIG_PROPERTY = "ote.pubsub.config";
   private static final String DEFAULT_FILE_NAME = "defaultConfig.txt";
   private final Map<String, InetSocketAddress> map = new HashMap<>();
   private static final String configurationFile = System.getProperty(CONFIG_PROPERTY);

   @Override
   public InetSocketAddress getAddress(String id) {
      if (map.isEmpty()) {
         lateLoadConfigFile();
      }
      return map.get(id);
   }


   @Override
   public void putAddress(String id, String address, int port) {
      try {
         InetAddress inetAddress;
         if (address.equalsIgnoreCase("localhost")) {
            // This will always work but sometimes getByName("localhost") will not due to etc/host not being set correctly
            inetAddress = InetAddress.getLocalHost();
         } else {
            inetAddress = InetAddress.getByName(address);
         }
         this.map.put(id, new InetSocketAddress(inetAddress, port));
      } catch (UnknownHostException ex) {
         throw new OseeCoreException(ex, "Error attaching to PubSub participant %s at %s:%s", id, address, port);
      }
   }

   private void lateLoadConfigFile() {
      InputStream stream;
      if (Strings.isValid(configurationFile)) {
         try {
            stream = new FileInputStream(configurationFile);
         } catch (FileNotFoundException ex) {
            throw new OseeCoreException(ex, "Error reading command line pubsub config file %s = %s", CONFIG_PROPERTY, configurationFile);
         }
      } else {
         stream = loadStream();
      }

      if (stream == null) {
         stream = OseeInf.getResourceAsStream(DEFAULT_FILE_NAME, this.getClass());
      }

      parseAndAddConfigStream(stream);
   }


   protected abstract InputStream loadStream();

   private void parseAndAddConfigStream(InputStream stream) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
         while (reader.ready()) {
            String line = reader.readLine();
            parseAndAddAddress(line);
         }
      } catch (IOException ex) {
         throw new OseeCoreException(ex, "Error reading file at %s", configurationFile);
      }
   }

   private void parseAndAddAddress(String line) {
      String[] split = line.split(",");
      if (split.length != 3) {
         throw new IllegalArgumentException(
            String.format("Line %s must be exactly <ParticipantName>,<IP_Address>,<Port>", line));
      }

      String id = split[0];
      String address = split[1];
      String portStr = split[2];

      int port;
      try {
         port = Integer.parseInt(portStr);
      } catch (NumberFormatException ex) {
         throw new OseeCoreException(ex,
            "Error parsing file %s: Port for pubsub entry ='%s' could not be converted to an integer",
            configurationFile, line);
      }

      putAddress(id, address, port);

   }

}
