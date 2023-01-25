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
package org.eclipse.osee.ote.filetransfer;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class TcpFileTransferHandle {

	private final Future<Boolean> future;
	private final TransferProgressMonitor progressMonitor;

	private final InetSocketAddress localAddress;
	
	TcpFileTransferHandle(TransferProgressMonitor progressMonitor, InetSocketAddress localAddress,
			Future<Boolean> future) {
		super();
		this.progressMonitor = progressMonitor;
		this.future = future;
		this.localAddress = localAddress;
	}
	
	public void cancelTransfer() {
		future.cancel(true);
	}
	
	public TransferProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}


	
	/**
	 * blocks until transfer is complete.
	 * @return true if transfer was a success or false otherwise
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public boolean awaitCompletion() throws ExecutionException, InterruptedException {
		return future.get();
	}
	
	/**
	 * returns whether or not the transfer has completed. A transfer is considered complete
	 * once all bytes have been sent/received or if transfer was aborted due to an error
	 * @return
	 */
	public boolean isComplete() {
		return future.isDone();
	}

	public InetSocketAddress getLocalAddress() {
		return localAddress;
	}
}
