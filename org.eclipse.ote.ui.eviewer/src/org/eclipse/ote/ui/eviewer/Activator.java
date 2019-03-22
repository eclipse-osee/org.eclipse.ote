/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.ote.ui.eviewer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.eclipse.ote.ui.eviewer";
	private static Activator plugin;

	private BundleContext context;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		this.context = null;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}



	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		reg.put("ACTIVE_PNG", getImageDescriptor("OSEE-INF/images/active.png"));
		reg.put("INACTIVE_PNG", getImageDescriptor("OSEE-INF/images/inactive.png"));
		reg.put("DUPLICATE_PNG", getImageDescriptor("OSEE-INF/images/question_white.png"));
	}

	BundleContext getBundleContext() {
		return context;
	}
}
