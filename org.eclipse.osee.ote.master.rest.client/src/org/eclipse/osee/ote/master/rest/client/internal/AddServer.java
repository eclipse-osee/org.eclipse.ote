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
package org.eclipse.osee.ote.master.rest.client.internal;

import java.net.URI;
import java.util.concurrent.Callable;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerResult;
import org.eclipse.osee.ote.master.rest.model.OTEServer;

public class AddServer implements Callable<OTEMasterServerResult> {

    private final JaxRsApi webClientProvider;
    private final OTEServer server;
    private final URI uri;

    public AddServer(JaxRsApi webClientProvider, URI uri, OTEServer server) {
        this.webClientProvider = webClientProvider;
        this.uri = uri;
        this.server = server;
    }

    @Override
    public OTEMasterServerResult call() throws Exception {
        OTEMasterServerResult result = new OTEMasterServerResult();
        try {
            URI targetUri = UriBuilder.fromUri(uri).path(OTEMasterServerImpl.CONTEXT_NAME)
                    .path(OTEMasterServerImpl.CONTEXT_SERVERS).build();

            if (HttpUtil.canConnect(targetUri)) {
                WebTarget target = webClientProvider.newTargetUrl(targetUri.toString());
                Builder builder = target.request(MediaType.APPLICATION_JSON);
                builder.post(Entity.json(server));
            } else {
                result.setSuccess(false);
            }
        } catch (Throwable th) {
            result.setSuccess(false);
        }
        return result;
    }
}
