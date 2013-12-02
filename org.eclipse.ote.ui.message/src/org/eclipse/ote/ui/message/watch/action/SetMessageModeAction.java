/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.message.watch.action;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.ote.ui.message.tree.WatchedMessageNode;
import org.eclipse.ote.ui.message.watch.WatchView;

/**
 * @author Ken J. Aguilar
 */
public class SetMessageModeAction extends Action {

	private final WatchedMessageNode node;
	private final MessageMode mode;
	private final WatchView watchView;

	public SetMessageModeAction(WatchView watchView, WatchedMessageNode node, MessageMode mode) {
		super(mode.name());
		this.watchView = watchView;
		this.node = node;
		this.mode = mode;
		setChecked(node.getSubscription().getMessageMode() == mode);
	}

	@Override
	public void run() {
		node.getSubscription().changeMessageMode(mode);
		watchView.saveWatchFile();
	}
}
