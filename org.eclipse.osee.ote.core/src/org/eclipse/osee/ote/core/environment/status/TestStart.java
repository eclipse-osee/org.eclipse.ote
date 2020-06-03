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

package org.eclipse.osee.ote.core.environment.status;

import java.io.Serializable;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestStart implements IServiceStatusData, Serializable {

	private static final long serialVersionUID = -3458459132672153638L;
	private final String testClassName;
	private final String serverOutfilePath;
	private final String clientOutfilePath;

	public TestStart(String testClassName, String serverOutfilePath, String clientOutfilePath) {
		this.testClassName = testClassName;
		this.serverOutfilePath = serverOutfilePath;
		this.clientOutfilePath = clientOutfilePath;
	}

	@Override
	public void accept(IServiceStatusDataVisitor visitor) {
		visitor.asTestStart(this);
	}

	public String getTestClassName() {
		return testClassName;
	}

	public String getServerOutfilePath() {
		return serverOutfilePath;
	}

	public String getClientOutfilePath() {
		return clientOutfilePath;
	}


}
